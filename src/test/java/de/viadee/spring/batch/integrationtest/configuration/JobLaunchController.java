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
