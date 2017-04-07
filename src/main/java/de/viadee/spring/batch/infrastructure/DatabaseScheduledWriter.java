/**
 * Copyright � 2016, viadee Unternehmensberatung GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the viadee Unternehmensberatung GmbH.
 * 4. Neither the name of the viadee Unternehmensberatung GmbH nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.spring.batch.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.persistence.SPBMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.SPBMItemQueue;
import de.viadee.spring.batch.persistence.types.SPBMChunkExecution;
import de.viadee.spring.batch.persistence.types.SPBMItem;

/**
 * 
 * We are monitoring each single Item, a lot of Monitoring Data is gathered.
 * Writing each Monitoring Event as a single commit will end up in creating a
 * HUGE delay in the job. Since we are monitoring Performance, adding an
 * unwanted delay into the Batch Process would distort our measurements. Simply
 * storing the gathered Data into a List and flushing them after the Job
 * finished will kill your RAM on bigger Jobs.
 * 
 * This class represents an asnynchronous Thread, which runs aside the
 * Batch-Thread. It is called on an interval basis. (See the SchedulingHolder
 * class for further detail).
 * 
 * The main purpose of the DatabaseScheduledWriter is to grep a bunch of
 * Logging-Data from the Item- and the Chunk-Queue and write this batch into the
 * Monitoring-Database. By splitting this into a separate Thread, the main Batch
 * Process will be affected as less as possible.
 * 
 * Note: This Thread will be spawned as a Daemon-Thread. This might lead to this
 * thread being destroyed whilst writing our precious Monitoring data into the
 * Monitoring-Database. To prevent this, each time this Thread becomes active,
 * it registers itself into a specific variable indicating, that its running.
 * The JdbcTemplateHolder (containing the JdbcTemplate) checks this variable
 * before it is destroyed, blocking its destruction until all the Queues have
 * been emptied.
 * 
 * TODO: Use the DAOs to write into the Database
 * 
 */
class DatabaseScheduledWriter implements Runnable {

	private final int MAXBATCHSIZE = 100;

	private static final Logger LOG = LoggingWrapper.getLogger(DatabaseScheduledWriter.class);

	private SPBMItemQueue sPBMItemQueue;

	private JdbcTemplateHolder jdbcTemplateHolder;

	private SPBMChunkExecutionQueue sPBMChunkExecutionQueue;

	private ChronoHelper chronoHelper;

	private final String ITEMINSERTSQL = "INSERT INTO \"Item\" (\"ActionID\",\"ChunkExecutionID\",\"ItemName\",\"TimeInMS\",\"Error\") VALUES (:actionID,:chunkExecutionID,:itemName,:timeInMS,:error);";

	private final String CHUNKEXECUTIONINSERTSQL = "INSERT INTO \"ChunkExecution\" (\"ChunkExecutionID\", \"StepID\", \"StepName\", \"Iteration\",\"ChunkTime\") VALUES (:chunkExecutionID,:stepID,:stepName,:iteration,:chunkTime);";

	public void setSPBMItemQueue(final SPBMItemQueue sPBMItemQueue) {
		this.sPBMItemQueue = sPBMItemQueue;
	}

	public void setSPBMChunkExecutionQueue(final SPBMChunkExecutionQueue sPBMChunkExecutionQueue) {
		this.sPBMChunkExecutionQueue = sPBMChunkExecutionQueue;
	}

	public void setJdbcTemplateHolder(final JdbcTemplateHolder jdbcTemplateHolder) {
		this.jdbcTemplateHolder = jdbcTemplateHolder;
	}

	public void setChronoHelper(final ChronoHelper chronoHelper) {
		this.chronoHelper = chronoHelper;
	}

	@Override
	public void run() {
		if (this.chronoHelper != null) {
			this.chronoHelper.addDaemonsRunning(1);
		}

		// Empty the Item Queue
		SPBMItem item = null;
		final List<SPBMItem> itemList = new ArrayList<SPBMItem>();
		item = this.sPBMItemQueue.getItem();
		int counter = 0;
		while (item != null && counter <= MAXBATCHSIZE) {
			itemList.add(item);
			item = this.sPBMItemQueue.getItem();
		}
		if (!itemList.isEmpty()) {
			flushItemList(itemList);
		}

		// Empty the Chunk Queue
		SPBMChunkExecution chunkExecution = null;
		final List<SPBMChunkExecution> chunkExecutionList = new ArrayList<SPBMChunkExecution>();
		chunkExecution = this.sPBMChunkExecutionQueue.getChunk();
		counter = 0;
		while (chunkExecution != null && counter < MAXBATCHSIZE) {
			chunkExecutionList.add(chunkExecution);
			chunkExecution = this.sPBMChunkExecutionQueue.getChunk();
		}
		if (!chunkExecutionList.isEmpty()) {
			flushChunkExecutionList(chunkExecutionList);
		}
		if (this.chronoHelper != null) {
			this.chronoHelper.addDaemonsRunning(-1);
		}
	}

	// TODO: Use DAO
	public void flushItemList(final List<SPBMItem> itemList) {
		LOG.debug("Flushlist with " + itemList.size() + " Items");
		final long startCalc = System.currentTimeMillis();
		final Map<String, String>[] parameters = new Map[itemList.size()];
		Map<String, String> params;
		int counter = 0;
		for (final SPBMItem item : itemList) {
			params = new HashMap<String, String>();
			params.put("actionID", "" + item.getActionID());
			params.put("chunkExecutionID", "" + item.getChunkExecutionID());
			params.put("itemName", "" + item.getItemName());
			params.put("timeInMS", "" + item.getTimeInMS());
			params.put("error", "" + item.isError());
			parameters[counter++] = params;
		}
		final long startFlush = System.currentTimeMillis();
		this.jdbcTemplateHolder.getJdbcTemplate().batchUpdate(ITEMINSERTSQL, parameters);
		final long endFlush = System.currentTimeMillis();
		LOG.debug((endFlush - startFlush) + "ms to flush " + itemList.size() + " Items and " + (startFlush - startCalc)
				+ "ms to build the map");
		LOG.debug("Flushed");
	}

	public void flushChunkExecutionList(final List<SPBMChunkExecution> chunkExecutionList) {
		LOG.debug("Flushing ChunkList with " + chunkExecutionList.size() + " ChunkExecutions");
		final long startCalc = System.currentTimeMillis();
		final Map<String, String>[] parameters = new Map[chunkExecutionList.size()];
		Map<String, String> params;
		int counter = 0;
		for (final SPBMChunkExecution sPBMChunkExecution : chunkExecutionList) {
			params = new HashMap<String, String>();
			params.put("chunkExecutionID", "" + sPBMChunkExecution.getChunkExecutionID());
			params.put("stepID", "" + sPBMChunkExecution.getStepID());
			params.put("stepName", sPBMChunkExecution.getStepName());
			params.put("iteration", "" + sPBMChunkExecution.getIteration());
			params.put("chunkTime", "" + sPBMChunkExecution.getChunkTime());
			parameters[counter++] = params;
		}
		final long startFlush = System.currentTimeMillis();
		this.jdbcTemplateHolder.getJdbcTemplate().batchUpdate(CHUNKEXECUTIONINSERTSQL, parameters);
		final long endFlush = System.currentTimeMillis();
		LOG.debug((endFlush - startFlush) + "ms to flush " + chunkExecutionList.size() + " ChunkExecutions and "
				+ (startFlush - startCalc) + "ms to build the map");
		LOG.debug("Flushed");
	}

}