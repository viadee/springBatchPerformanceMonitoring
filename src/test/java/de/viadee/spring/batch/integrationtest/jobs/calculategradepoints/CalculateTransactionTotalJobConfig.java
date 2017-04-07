package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(CalculateTransactionTotalStepConfig.class)
@Configuration
public class CalculateTransactionTotalJobConfig {

    private static Logger LOG = Logger.getLogger(CalculateTransactionTotalJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private CalculateTransactionTotalStepConfig calculateTransactionTotalStep;

    @Bean
    public Job calculateTransactionTotalJob() {
        return jobBuilders.get("calculateTransactionTotalJob")
                .start(calculateTransactionTotalStep.calculateTransactionTotalStep()).build();

    }

    public Job getCalculateTransactionTotalJob() {
        return calculateTransactionTotalJob();
    }

}
