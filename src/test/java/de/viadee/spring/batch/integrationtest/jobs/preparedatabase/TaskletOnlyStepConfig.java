package de.viadee.spring.batch.integrationtest.jobs.preparedatabase;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.viadee.spring.batch.integrationtest.configuration.StandaloneInfrastructureConfiguration;

@Import(StandaloneInfrastructureConfiguration.class)
@Configuration
public class TaskletOnlyStepConfig {

    private static Logger LOG = Logger.getLogger(TaskletOnlyStepConfig.class);

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Bean
    public Tasklet prepareDatabaseTasklet() {
        final PrepareDatabaseTasklet tasklet = new PrepareDatabaseTasklet();
        tasklet.setTemplate(template);
        tasklet.setTestDataSize(80);
        tasklet.setMaxGrades(5);
        return tasklet;
    }

    @Bean
    public Tasklet delayTasklet() {
        return new DelayTasklet();
    }

    @Bean
    public Step delayTaskletStep() {
        return stepBuilders.get("DelayTaskletStep").tasklet(delayTasklet())
                .transactionManager(platformTransactionManager).build();
    }

    public Step getDelayTaskletStep() {
        return delayTaskletStep();
    }

    @Bean
    public Step taskletOnlyStep() {
        return stepBuilders.get("TaskletOnlyStep").tasklet(prepareDatabaseTasklet())
                .transactionManager(platformTransactionManager).build();
    }

    public Step getStep() {
        return taskletOnlyStep();
    }
}
