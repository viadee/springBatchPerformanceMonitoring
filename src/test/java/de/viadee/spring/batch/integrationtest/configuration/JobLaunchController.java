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
package de.viadee.spring.batch.integrationtest.configuration;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.viadee.spring.batch.integrationtest.jobs.calculategradepoints.CalculateTransactionTotalJobConfig;
import de.viadee.spring.batch.integrationtest.jobs.calculategradepoints.CalculateTransactionTotalPartitionJob;
import de.viadee.spring.batch.integrationtest.jobs.formatnames.FormatNamesJobConfig;
import de.viadee.spring.batch.integrationtest.jobs.preparedatabase.PrepareDatabaseJobConfig;

@Import({ de.viadee.spring.batch.integrationtest.jobs.preparedatabase.PrepareDatabaseJobConfig.class,
        CalculateTransactionTotalJobConfig.class, CalculateTransactionTotalPartitionJob.class, FormatNamesJobConfig.class })

@Configuration
public class JobLaunchController {

    private static Logger LOG = Logger.getLogger(JobLaunchController.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    PrepareDatabaseJobConfig prepareDatabaseJob;

    @Autowired
    FormatNamesJobConfig formatNamesJob;

    @Autowired
    CalculateTransactionTotalJobConfig calculateTransactionTotalJob;

    @Autowired
    CalculateTransactionTotalPartitionJob calculateTransactionTotalPartitionJob;

    private Job getPrepareDatabaseJob() {
        return prepareDatabaseJob.prepareDatabaseJob();
    }

    private Job getCalculateTransactionTotalJob() {
        return calculateTransactionTotalJob.calculateTransactionTotalJob();
    }

    private Job getCalculateTransactionTotalPartitionJob() {
        return calculateTransactionTotalPartitionJob.calculateTransactionTotaPartitionlJob();
    }

    private Job getFormatNamesJob() {
        return formatNamesJob.formatNamesJob();
    }

    public void launchJobs() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException {
        long before, after;
        LOG.debug("Launching Job1");
        before = System.currentTimeMillis();
        jobLauncher.run(getPrepareDatabaseJob(), new JobParameters());
        after = System.currentTimeMillis();
        LOG.info("Runtime for Job1: " + (after - before) + "ms");
        LOG.debug("Launching Job2");
        before = System.currentTimeMillis();
        jobLauncher.run(getCalculateTransactionTotalJob(), new JobParameters());
        after = System.currentTimeMillis();
        LOG.info("Runtime for Job2: " + (after - before) + "ms");

        LOG.debug("Launching Job3");
        before = System.currentTimeMillis();
        jobLauncher.run(getCalculateTransactionTotalPartitionJob(), new JobParameters());
        after = System.currentTimeMillis();
        LOG.info("Runtime for Job3: " + (after - before) + "ms");

        LOG.debug("Launching Job4");
        before = System.currentTimeMillis();
        jobLauncher.run(getFormatNamesJob(), new JobParameters());
        after = System.currentTimeMillis();
        LOG.info("Runtime for Job4: " + (after - before) + "ms");
        LOG.debug("JobLauncher done");

    }
}
