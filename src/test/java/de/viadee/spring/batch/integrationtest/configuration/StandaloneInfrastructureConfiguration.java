package de.viadee.spring.batch.integrationtest.configuration;

import java.sql.Driver;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class StandaloneInfrastructureConfiguration implements InfrastructureConfiguration {

	private static Logger LOG = Logger.getLogger(StandaloneInfrastructureConfiguration.class);

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry());
		return jobRegistryBeanPostProcessor;
	}

	@Override
	@Bean
	public DataSource dataSource() {

		SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
		simpleDriverDataSource.setUrl("jdbc:h2:./target/database/ExampleJobDB;MULTI_THREADED=1;DB_CLOSE_ON_EXIT=FALSE");
		simpleDriverDataSource.setUsername("sa");
		simpleDriverDataSource.setPassword("sasa");
		try {
			simpleDriverDataSource.setDriverClass((Class<? extends Driver>) Class.forName("org.h2.Driver"));
		} catch (ClassNotFoundException e) {
			LOG.warn(e);
		}
		// Initialize DB
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(simpleDriverDataSource);
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("SQL/schema-h2.sql"));
		resourceDatabasePopulator.addScript(new ClassPathResource("SQL/prepare-tables.sql"));
		dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
		dataSourceInitializer.afterPropertiesSet();
		return simpleDriverDataSource;
		/*
		 * EmbeddedDatabaseBuilder embeddedDatabaseBulder = new
		 * EmbeddedDatabaseBuilder(); return
		 * embeddedDatabaseBulder.addScript("SQL/schema-h2.sql").addScript(
		 * "SQL/prepare-tables.sql") .setType(EmbeddedDatabaseType.H2).build();
		 */
	}

	// This doesn't seem to set the maximum Number of Threads (see
	// CalculateTransactionTotalPartitioningStep.java)
	// Due to possible unknown side effects, we shall keep both Values on the
	// same value
	@Override
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		return taskExecutor;
	}

	@Override
	@Bean
	public PlatformTransactionManager platformTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Override
	@Bean
	public NamedParameterJdbcTemplate template() {
		return new NamedParameterJdbcTemplate(dataSource());
	}
}
