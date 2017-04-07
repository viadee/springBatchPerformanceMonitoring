package de.viadee.spring.batch.integrationtest;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class MainApplication {

    private static final Logger LOG = Logger.getLogger(MainApplication.class);

    // Amount of Student objects generated for the batch processing during the first step.

    public static void main(final String[] args)
            throws BeansException, JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException, InterruptedException {
        // LOG.info("Sleeping 5 Seconds.");
        // Thread.sleep(5000);
        LOG.info("Done sleeping - Now creating ApplicationContext");
        final ApplicationContext ctx = new AnnotationConfigApplicationContext(
                de.viadee.spring.batch.integrationtest.configuration.ApplicationConfiguration.class);
        // ((AbstractApplicationContext) ctx).addApplicationListener(new ContextCloseEventHandler());
        final de.viadee.spring.batch.integrationtest.configuration.JobLaunchController appConfig = ctx
                .getBean(de.viadee.spring.batch.integrationtest.configuration.JobLaunchController.class);
        appConfig.launchJobs();
        // LOG.info("Sleeping 5 Seconds.");
        // Thread.sleep(5000);
        ((AbstractApplicationContext) ctx).close();
    }
}
