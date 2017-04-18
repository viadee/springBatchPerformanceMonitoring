/**
 * Copyright � 2016, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.spring.batch.operational.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.chronometer.TimeLogger;
import de.viadee.spring.batch.persistence.SPBMStepDAO;
import de.viadee.spring.batch.persistence.types.SPBMStep;

/**
 * The BatchStepListener is created and assigned by the BeanPostProcessor class. It takes care of all the actions
 * needed, to perform performance measuring of a certain step execution.
 */
public class BatchStepListener implements StepExecutionListener {

    private ChronoHelper chronoHelper;

    private static final Logger LOGGER = LoggingWrapper.getLogger(BatchStepListener.class);

    private final TimeLogger timeLogger = TimeLogger.getTimeLoggerFor(this);

    private final Map<StepExecution, Object> exeMap = new ConcurrentHashMap<StepExecution, Object>();

    private final ConcurrentHashMap<Thread, SPBMStep> threadSPBMStep = new ConcurrentHashMap<Thread, SPBMStep>();

    private SPBMStep sPBMStep;

    private SPBMStepDAO sPBMStepDAO;

    public SPBMStep getSPBMStep(final Thread thread) {
        return this.threadSPBMStep.get(thread);
    }

    public void setSPBMStepDAO(final SPBMStepDAO sPBMStepDAO) {
        this.sPBMStepDAO = sPBMStepDAO;
    }

    public void setChronoHelper(final ChronoHelper chronoHelper) {
        this.chronoHelper = chronoHelper;
    }

    private void setStaticBatchStepListener() {
        chronoHelper.setBatchStepListener(this);
    }

    @Override
    public synchronized void beforeStep(final StepExecution stepExecution) {
        final String stepName = stepExecution.getStepName();
        sPBMStep = new SPBMStep(chronoHelper.getNextBatchStepID(),
                chronoHelper.getBatchJobListener().getSPBMJob().getJobID(), stepName, 0);
        final TimeLogger tempLogger = new TimeLogger();
        tempLogger.setName(stepName);
        tempLogger.getOwnChronometer().setObjectName(stepName);
        final Map<SPBMStep, TimeLogger> tempMap = new HashMap<SPBMStep, TimeLogger>();
        tempMap.put(sPBMStep, tempLogger);
        threadSPBMStep.put(Thread.currentThread(), sPBMStep);
        exeMap.put(stepExecution, tempMap);
        tempLogger.getOwnChronometer().startChronometer();
        LOGGER.trace("BatchStepListener before advice active");
        setStaticBatchStepListener();
        timeLogger.setParent(chronoHelper.getBatchJobListener().getTimeLogger().getOwnChronometer());
        timeLogger.setName(stepName);
        timeLogger.getOwnChronometer().setObjectName(stepName);
        timeLogger.getOwnChronometer().startChronometer();
        LOGGER.trace("BatchStepListener before method has sucessfully set up its environment");
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ExitStatus afterStep(final StepExecution stepExecution) {

        final Map<SPBMStep, TimeLogger> lowerMap = (Map<SPBMStep, TimeLogger>) exeMap.get(stepExecution);
        for (final Entry<SPBMStep, TimeLogger> current : lowerMap.entrySet()) {
            current.getValue().getOwnChronometer().stop();
            current.getKey().setStepTime((int) current.getValue().getOwnChronometer().getDuration());
            sPBMStepDAO.insert(current.getKey());
        }

        return stepExecution.getExitStatus();
    }
}
