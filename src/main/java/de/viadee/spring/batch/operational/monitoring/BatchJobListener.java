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
package de.viadee.spring.batch.operational.monitoring;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import de.viadee.spring.batch.infrastructure.ActivityNotifier;
import de.viadee.spring.batch.infrastructure.JobEndQueueCleaner;
import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.chronometer.TimeLogger;
import de.viadee.spring.batch.persistence.SBPMJobDAO;
import de.viadee.spring.batch.persistence.types.SBPMJob;

/**
 * The BatchJobListener is created and assigned by the BeanPostProcessor class. It takes care of all the actions needed,
 * to perform performance-measuring of a certain Job execution.
 * 
 */
public class BatchJobListener implements JobExecutionListener {

    private JobEndQueueCleaner asyncTest;

    private static final Logger LOGGER = LoggingWrapper.getLogger(BatchJobListener.class);

    private final TimeLogger timeLogger = TimeLogger.getTimeLoggerFor(this);

    private ChronoHelper chronoHelper;

    private SBPMJobDAO sPBMJobDAO;

    private ActivityNotifier notificationHolder;

    private SBPMJob sPBMJob;

    public void setAsyncTest(final JobEndQueueCleaner asyncTest) {
        this.asyncTest = asyncTest;
    }

    public SBPMJob getSPBMJob() {
        return this.sPBMJob;
    }

    public void setSPBMJobDAO(final SBPMJobDAO dao) {
        this.sPBMJobDAO = dao;
    }

    public void insertDAO() {
        this.sPBMJobDAO.insert(this.sPBMJob);
    }

    public void setSPBMJob(final SBPMJob sPBMJob) {
        this.sPBMJob = sPBMJob;

    }

    public void setNotificationHolder(final ActivityNotifier notificationHolder) {
        this.notificationHolder = notificationHolder;
    }

    public void setChronoHelper(final ChronoHelper chronoHelper) {
        this.chronoHelper = chronoHelper;
    }

    public TimeLogger getTimeLogger() {
        return timeLogger;
    }

    private void setStaticBatchJobListener() {
        chronoHelper.setBatchJobListener(this);
    }

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        LOGGER.trace("BatchJobListener before advice active");
        final String jobName = jobExecution.getJobInstance().getJobName();
        setStaticBatchJobListener();
        this.sPBMJob.setJobStart(Instant.now().getMillis());
        timeLogger.setName(jobName);
        notificationHolder.beforeJob();
        timeLogger.getOwnChronometer().startChronometer();
        timeLogger.getOwnChronometer().setObjectName(jobExecution.getJobInstance().getJobName());
        LOGGER.trace("BatchJobListener before method has sucessfully set up its environment");
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void afterJob(final JobExecution jobExecution) {
        timeLogger.getOwnChronometer().stop();
        LOGGER.trace("BatchJobListener after method has stopped its chronometers");
        this.sPBMJob.setDuration((int) timeLogger.getOwnChronometer().getDuration());
        this.sPBMJob.setJobEnd(Instant.now().getMillis());
        this.insertDAO();
        notificationHolder.afterJob();
        try {
            asyncTest.asyncTest(jobExecution.getJobInstance().getJobName());
        } catch (final InterruptedException e) {
            LOGGER.warn(
                    "BatchJobListener was interrupted while asynchronously writing gathered performance data. Performance data may be incomplete.");
        }
    }

}
