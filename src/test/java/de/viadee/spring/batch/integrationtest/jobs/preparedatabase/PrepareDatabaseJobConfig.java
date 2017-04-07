package de.viadee.spring.batch.integrationtest.jobs.preparedatabase;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ TaskletOnlyStepConfig.class, DataBaseFiller.class })
@Configuration
public class PrepareDatabaseJobConfig {

    private static Logger LOG = Logger.getLogger(PrepareDatabaseJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private Step taskletOnlyStep;

    @Autowired
    private Step delayTaskletStep;

    @Bean
    public Job prepareDatabaseJob() {
        return jobBuilders.get("prepareDatabaseJob").start(taskletOnlyStep).next(delayTaskletStep).build();
    }

    public Job getPrepareDatabaseJob() {
        return prepareDatabaseJob();
    }

}
