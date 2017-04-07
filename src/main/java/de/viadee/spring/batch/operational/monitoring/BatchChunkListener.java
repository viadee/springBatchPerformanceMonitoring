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
package de.viadee.spring.batch.operational.monitoring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.operational.chronometer.ChronometerType;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.chronometer.TimeLogger;
import de.viadee.spring.batch.persistence.SPBMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.types.SPBMChunkExecution;

/**
 * The BatchChunkListener is created and assigned by the BeanPostProcessor class. It takes care of all the actions
 * needed, to perform performance-measuring of a certain Chunk execution.
 * 
 */
public class BatchChunkListener implements ChunkListener {

    private SPBMChunkExecutionQueue sPBMChunkExecutionQueue;

    ChronoHelper chronoHelper;

    private static final Logger LOGGER = LoggingWrapper.getLogger(BatchChunkListener.class);

    private TimeLogger timeLogger;

    private TimeLogger reader, processor, writer;

    private final ConcurrentHashMap<Thread, SPBMChunkExecution> threadChunkExecution = new ConcurrentHashMap<Thread, SPBMChunkExecution>();

    private final Map<String, TimeAwareSPBMChunkExecution> exeMap = new ConcurrentHashMap<String, TimeAwareSPBMChunkExecution>();

    public SPBMChunkExecution getSPBMChunkExecution(final Thread thread) {
        return this.threadChunkExecution.get(thread);
    }

    public void setSPBMChunkExecutionQueue(final SPBMChunkExecutionQueue sPBMChunkExecutionQueue) {
        this.sPBMChunkExecutionQueue = sPBMChunkExecutionQueue;
    }

    public ChronoHelper getChronoHelper() {
        return chronoHelper;
    }

    public void setChronoHelper(final ChronoHelper chronoHelper) {
        this.chronoHelper = chronoHelper;
    }

    public TimeLogger getReader() {
        return reader;
    }

    public void setReader(final TimeLogger reader) {
        this.reader = reader;
    }

    public TimeLogger getProcessor() {
        return processor;
    }

    public void setProcessor(final TimeLogger processor) {
        this.processor = processor;
    }

    public TimeLogger getWriter() {
        return writer;
    }

    public void setWriter(final TimeLogger writer) {
        this.writer = writer;
    }

    public TimeLogger getTimeLogger() {
        return timeLogger;
    }

    public void setThreadChunkExecution(final Thread thread, final SPBMChunkExecution sPBMChunkExecution) {
        this.threadChunkExecution.put(thread, sPBMChunkExecution);
    }

    /**
     * This method creates all needed requirements to measure time inside a chunk.
     * 
     */
    @Override
    public synchronized void beforeChunk(final ChunkContext context) {
        LOGGER.trace("BatchChunkListener invoked \"before\" advice");
        final int chunkExecutionNumber = context.getStepContext().getStepExecution().getCommitCount() + 1;
        final String timeLoggerName = context.getStepContext().getStepName() + " chunkCount " + chunkExecutionNumber;
        timeLogger = TimeLogger.getTimeLoggerFor(timeLoggerName);
        final SPBMChunkExecution sPBMChunkExecution = new SPBMChunkExecution(chronoHelper.getNextBatchChunkID(),
                chronoHelper.getBatchStepListener().getSPBMStep(Thread.currentThread()).getStepID(),
                context.getStepContext().getStepName(), chunkExecutionNumber, 0);
        this.setThreadChunkExecution(Thread.currentThread(), sPBMChunkExecution);
        final TimeLogger tempLogger = new TimeLogger();
        final TimeAwareSPBMChunkExecution timeAwareSPBMChunkExecution = new TimeAwareSPBMChunkExecution(
                sPBMChunkExecution, tempLogger);
        tempLogger.setName(timeLoggerName);
        tempLogger.getOwnChronometer().setObjectName(timeLoggerName);
        tempLogger.getOwnChronometer().startChronometer();
        LOGGER.trace("Pusing in Context " + context.getStepContext().getStepName() + " - " + context.hashCode() + " - "
                + timeAwareSPBMChunkExecution.getsPBMChunkExecution().getStepName());
        exeMap.put(context.getStepContext().getStepName(), timeAwareSPBMChunkExecution);
        chronoHelper.setBatchChunkListener(this);
        LOGGER.trace("Just put Templogger with name \"" + tempLogger.getName() + "\" in the Map belonging to \""
                + sPBMChunkExecution.getStepName() + "\" Step" + " in Thread - " + Thread.currentThread().toString());
        LOGGER.trace("Just put the Map (see above) into the context " + context.getStepContext().getJobName());
        timeLogger.getOwnChronometer().startChronometer();
        setReader(new TimeLogger(ChronometerType.READER));
        setProcessor(new TimeLogger(ChronometerType.PROCESSOR));
        setWriter(new TimeLogger(ChronometerType.WRITER));
        getReader().setParent(timeLogger.getOwnChronometer());
        getReader().setName("Reader");
        getProcessor().setParent(timeLogger.getOwnChronometer());
        getProcessor().setName("Processor");
        getWriter().setParent(timeLogger.getOwnChronometer());
        getWriter().setName("Writer");
        getReader().getOwnChronometer().startChronometer();
        getProcessor().getOwnChronometer().startChronometer();
        LOGGER.trace("BatchChunkListener before method has sucessfully set up its environment");
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public synchronized void afterChunk(final ChunkContext context) {
        getReader().getOwnChronometer().stop();
        getProcessor().getOwnChronometer().stop();
        getWriter().getOwnChronometer().stop();
        LOGGER.trace("Getting the Map for the following Context: " + context.getStepContext().getStepName() + " - "
                + context.hashCode() + " - In Thread: " + Thread.currentThread().toString());
        final TimeAwareSPBMChunkExecution timeAwareSPBMChunkExecution = exeMap
                .get(context.getStepContext().getStepName());
        LOGGER.trace("Got the Map! Step name according to map is"
                + timeAwareSPBMChunkExecution.getsPBMChunkExecution().getStepName() + " - in Thread "
                + Thread.currentThread().toString());
        timeAwareSPBMChunkExecution.getTimeLogger().getOwnChronometer().stop();
        timeAwareSPBMChunkExecution.getsPBMChunkExecution()
                .setChunkTime((int) timeAwareSPBMChunkExecution.getTimeLogger().getOwnChronometer().getDuration());
        sPBMChunkExecutionQueue.addChunkExecution(timeAwareSPBMChunkExecution.getsPBMChunkExecution());
        LOGGER.trace("BatchChunkListener after method has stopped its chronometers");
    }

    @Override
    public void afterChunkError(final ChunkContext context) {
        LOGGER.warn("Performance Logging received an unhandled Error Event in a chunk context " + context);
    }

}
