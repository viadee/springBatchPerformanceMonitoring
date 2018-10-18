/**
 * Copyright � 2016, viadee Unternehmensberatung AG
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.spring.batch.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.persistence.SBPMChunkExecutionDAO;
import de.viadee.spring.batch.persistence.SBPMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.SBPMItemDAO;
import de.viadee.spring.batch.persistence.SBPMItemQueue;
import de.viadee.spring.batch.persistence.types.SBPMChunkExecution;
import de.viadee.spring.batch.persistence.types.SBPMItem;

/**
 * 
 * We are monitoring each single Item, a lot of Monitoring Data is gathered.
 * Writing each Monitoring Event as a single commit will end up in creating a
 * HUGE delay in the job. Since we are monitoring Performance, adding an
 * unwanted delay into the Batch Process would distort our measurements. Simply
 * storing the gathered Data into a List and flushing them after the Job
 * finished will kill your RAM on bigger Jobs.
 * 
 * This class represents an asynchronous thread, which runs aside the
 * Batch-Thread. It is called on an interval basis. (See the SchedulingHolder
 * class for further detail).
 * 
 * The main purpose of the DatabaseScheduledWriter is to take a bunch of
 * Logging-Data from the Item- and the Chunk-Queue and write this batch into the
 * Monitoring-Database. By splitting this into a separate Thread, the main Batch
 * Process will hardly be affected.
 * 
 * Note: This thread will be spawned as a Daemon-Thread. This might lead to this
 * thread being destroyed whilst writing our monitoring data into the monitoring
 * database. To prevent this, each time this thread becomes active, it registers
 * itself into a specific variable indicating, that its running. The
 * JdbcTemplateHolder (containing the JdbcTemplate) checks this variable before
 * it is destroyed, blocking its destruction until all queues with logging data
 * have been emptied.
 * 
 */
class DatabaseScheduledWriter implements Runnable {

	private final int MAXBATCHSIZE = 100;

	private static final Logger LOG = LoggingWrapper.getLogger(DatabaseScheduledWriter.class);

	private SBPMItemQueue sPBMItemQueue;

	private SBPMItemDAO sPBMItemDao;

	private SBPMChunkExecutionDAO sPBMChunkExecutionDao;

	private SBPMChunkExecutionQueue sPBMChunkExecutionQueue;

	private ChronoHelper chronoHelper;

	public void setSPBMItemQueue(final SBPMItemQueue sPBMItemQueue) {
		this.sPBMItemQueue = sPBMItemQueue;
	}

	public void setSPBMChunkExecutionQueue(final SBPMChunkExecutionQueue sPBMChunkExecutionQueue) {
		this.sPBMChunkExecutionQueue = sPBMChunkExecutionQueue;
	}

	public void setSPBMItemDAO(SBPMItemDAO sPBMItemDao) {
		this.sPBMItemDao = sPBMItemDao;
	}

	public void setJdbcTemplateHolder(final JdbcTemplateHolder jdbcTemplateHolder) {
		this.sPBMItemDao.setJdbcTemplateHolder(jdbcTemplateHolder);
		this.sPBMChunkExecutionDao.setJdbcTemplateHolder(jdbcTemplateHolder);
	}

	public void setChronoHelper(final ChronoHelper chronoHelper) {
		this.chronoHelper = chronoHelper;
	}

	public void setSPBMChunkExecutionDAO(SBPMChunkExecutionDAO sPBMChunkExecutionDao) {
		this.sPBMChunkExecutionDao = sPBMChunkExecutionDao;
	}

	@Override
	public void run() {
		if (this.chronoHelper != null) {
			this.chronoHelper.addDaemonsRunning(1);
		}

		// Empty the Item Queue
		SBPMItem item = null;
		final List<SBPMItem> itemList = new ArrayList<SBPMItem>();
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
		SBPMChunkExecution chunkExecution = null;
		final List<SBPMChunkExecution> chunkExecutionList = new ArrayList<SBPMChunkExecution>();
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

	public void flushItemList(final List<SBPMItem> itemList) {
		LOG.debug("Flushlist with " + itemList.size() + " Items");
		final long startFlush = System.currentTimeMillis();
		sPBMItemDao.insertBatch(itemList);
		final long endFlush = System.currentTimeMillis();
		LOG.debug((endFlush - startFlush) + "ms to flush " + itemList.size() + "items and to build the map");
		LOG.debug("Flushed");
	}

	public void flushChunkExecutionList(final List<SBPMChunkExecution> chunkExecutionList) {
		LOG.debug("Flushing ChunkList with " + chunkExecutionList.size() + " ChunkExecutions");
		final long startFlush = System.currentTimeMillis();
		sPBMChunkExecutionDao.insertBatch(chunkExecutionList);
		final long endFlush = System.currentTimeMillis();
		LOG.debug((endFlush - startFlush) + "ms to flush " + chunkExecutionList.size()
				+ " ChunkExecutions and to build the map");
		LOG.debug("Flushed");
	}

}