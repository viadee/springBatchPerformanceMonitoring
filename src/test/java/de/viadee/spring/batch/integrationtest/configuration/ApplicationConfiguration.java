package de.viadee.spring.batch.integrationtest.configuration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ StandaloneInfrastructureConfiguration.class, de.viadee.spring.batch.infrastructure.Configurator.class,
        de.viadee.spring.batch.integrationtest.jobs.JobCreator.class, JobLaunchController.class })
public class ApplicationConfiguration {

    private static Logger LOG = Logger.getLogger(ApplicationConfiguration.class);

    @Autowired
    JobLaunchController jobLaunchController;
}
