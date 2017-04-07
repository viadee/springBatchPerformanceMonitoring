package de.viadee.spring.batch.integrationtest.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

public interface InfrastructureConfiguration {

    @Bean
    public abstract DataSource dataSource();

    @Bean
    public abstract PlatformTransactionManager platformTransactionManager();

    @Bean
    public abstract NamedParameterJdbcTemplate template();
    
    @Bean
	public abstract TaskExecutor taskExecutor();
}
