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
package de.viadee.spring.batch.operational.chronometer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.infrastructure.JdbcTemplateHolder;
import de.viadee.spring.batch.operational.monitoring.BatchChunkListener;
import de.viadee.spring.batch.operational.monitoring.BatchJobListener;
import de.viadee.spring.batch.operational.monitoring.BatchStepListener;
import de.viadee.spring.batch.persistence.SBPMActionDAO;
import de.viadee.spring.batch.persistence.SBPMStepActionDAO;
import de.viadee.spring.batch.persistence.types.SBPMAction;
import de.viadee.spring.batch.persistence.types.SBPMStepAction;

/**
 * This is a helper Class which ensures that the gathered
 * Performance-Measurements are stored correctly into the database, concerning
 * referential integrity. It generates the IDs used in the database used to
 * distinguish Jobs, Steps (Tasklets), Actions ... based on their HashCode It
 * also resolves HashCodes into these IDs to keep consistency.
 * 
 * Any measurement needs to be connected with the information, where it emerged
 * in the Spring Batch Process.
 * 
 * This class also keeps track of the current point in the Batch-Job execution
 * whilst runtime. Here you can find the currently active Job, Step,
 * ChunkExecution, and Action.
 * 
 * This is needed, since we can't access the current executional Context
 * (meaning "Which Spring-Batch Element is currently active, that needs to be
 * connected with a just measured log-entry" ) cannot be accessed within an
 * SpringAOP-based measurement (Reader, Processor, Writer). Before persisting
 * measurement information into the Database, the records need to be enriched
 * with these contextual information which this class provides.
 * 
 * Since Spring Batch allows multiple Batch-Processes in different Threads, this
 * class also needs to keep track of the contextual information for each thread
 * separately.
 */
@Component
public class ChronoHelper {

	/**
	 * This value is set by the BatchJobListener class
	 */
	private BatchJobListener batchJobListener;

	/**
	 * This value is set by the BatchStepListener class
	 */
	private BatchStepListener batchStepListener;

	/**
	 * This value is set by the BatchChunkListener so the current active
	 * Chunk-listener can be accessed from within an aspect call.
	 */
	private BatchChunkListener batchChunkListener;

	private final List<Object> itemActions = new ArrayList<Object>();

	private final ConcurrentHashMap<Thread, Integer> threadActiveActionID = new ConcurrentHashMap<Thread, Integer>();

	private int batchChunkLastID = 1;

	private final Object activeItemAction = null;

	private final Map<Integer, Integer> hashToID = new HashMap<Integer, Integer>();

	private final ConcurrentLinkedQueue<SBPMStepAction> stepActionList = new ConcurrentLinkedQueue<SBPMStepAction>();

	private int lastActionID = 1;

	private int batchStepLastID = 1;

	@Autowired
	private SBPMActionDAO sPBMActionDAO;

	@Autowired
	private SBPMStepActionDAO sPBMStepActionDAO;

	@Autowired
	private JdbcTemplateHolder jdbcTemplateHolder;

	@PostConstruct
	public void postConstruct() {
		this.jdbcTemplateHolder.setChronoHelper(this);
	}

	private int activeActionID;

	private final AtomicInteger daemonsRunning = new AtomicInteger(0);

	public void addDaemonsRunning(final int change) {
		this.daemonsRunning.addAndGet(change);
	}

	public int getNextBatchStepID() {
		batchStepLastID++;
		return batchStepLastID;
	}

	public boolean getDaemonRunning() {
		if (daemonsRunning.get() == 0) {
			return false;
		} else {
			return true;
		}
	}

	private int hashToID(final int hashCode) {
		if (hashToID.containsKey(hashCode)) {
			return hashToID.get(hashCode);
		} else {
			final int newID = lastActionID++;
			hashToID.put(hashCode, newID);
			return newID;
		}
	}

	// Deprecated
	public int getActiveActionID() {
		return activeActionID;
	}

	public int getActiveActionID(final Thread thread) {
		return threadActiveActionID.get(thread);
	}

	/**
	 * Set the current Action
	 * 
	 * @param itemAction
	 *            Reader, Processor or Writer
	 * @param actionType
	 *            1=reader, 2=processor, 3=writer
	 * @param thread
	 *            current Thread
	 */
	public void setActiveAction(final Object itemAction, final int actionType, final Thread thread) {
		// Check if the reader changed
		if (activeItemAction != itemAction) {
			threadActiveActionID.put(thread, hashToID(itemAction.hashCode()));
			// Check if we know the reader
			if (!itemActions.contains(itemAction)) {
				// Add the ItemReader
				itemActions.add(itemAction);
				final String callingObjectName = (itemAction.toString()
						.split("\\.")[(itemAction.toString().split("\\.").length) - 1]).split("@")[0].split("@")[0];
				final SBPMAction sPBMItemAction = new SBPMAction(hashToID(itemAction.hashCode()), callingObjectName,
						actionType, 0, 0);
				sPBMActionDAO.insert(sPBMItemAction);
			}
		}
		// Check if Reader is already connected with Step
		boolean contained = false;
		for (final SBPMStepAction curr : stepActionList) {
			if (curr.getActionID() == hashToID(itemAction.hashCode())
					&& curr.getStepID() == batchStepListener.getSPBMStep(Thread.currentThread()).getStepID()) {
				contained = true;
			}
		}
		if (!contained) {
			// Add the Connection between the step and the action
			final SBPMStepAction sPBMStepAction = new SBPMStepAction(
					batchStepListener.getSPBMStep(Thread.currentThread()).getStepID(), hashToID(itemAction.hashCode()));
			stepActionList.add(sPBMStepAction);
			sPBMStepActionDAO.insert(sPBMStepAction);
		}

	}

	// Deprecated
	public Object getActiveItemAction() {
		return activeItemAction;
	}

	public int getNextBatchChunkID() {
		batchChunkLastID++;
		return batchChunkLastID;
	}

	public BatchJobListener getBatchJobListener() {
		return batchJobListener;
	}

	public void setBatchJobListener(final BatchJobListener batchJobListener) {
		this.batchJobListener = batchJobListener;
	}

	public BatchStepListener getBatchStepListener() {
		return batchStepListener;
	}

	public void setBatchStepListener(final BatchStepListener batchStepListener) {
		this.batchStepListener = batchStepListener;
	}

	public BatchChunkListener getBatchChunkListener() {
		return batchChunkListener;
	}

	public void setBatchChunkListener(final BatchChunkListener batchChunkListener) {
		this.batchChunkListener = batchChunkListener;
	}

}
