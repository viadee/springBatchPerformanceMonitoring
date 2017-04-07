package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(CalculateTransactionTotalPartitioningStepConfig.class)
@Configuration
@EnableBatchProcessing
public class CalculateTransactionTotalPartitionJob {

    private static Logger LOG = Logger.getLogger(CalculateTransactionTotalJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private CalculateTransactionTotalPartitioningStepConfig calculateTransactionTotalPartitioningStep;

    @Bean
    public Job calculateTransactionTotaPartitionlJob() {
        return jobBuilders.get("calculateTransactionTotalPartitioningJob")
                .start(calculateTransactionTotalPartitioningStep.partitionStep()).build();

    }

    public Job getCalculateTransactionTotalJob() {
        return calculateTransactionTotaPartitionlJob();
    }

}
