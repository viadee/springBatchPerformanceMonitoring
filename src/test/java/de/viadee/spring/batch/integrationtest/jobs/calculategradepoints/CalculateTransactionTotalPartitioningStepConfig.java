package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.CustomerRowMapper;

@Configuration
public class CalculateTransactionTotalPartitioningStepConfig {

	private static Logger LOG = Logger.getLogger(CalculateTransactionTotalStepConfig.class);

	@Autowired
	private StepBuilderFactory stepBuilders;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private NamedParameterJdbcTemplate template;

	@Autowired
	private TaskExecutor taskExecutor;

	// @Bean
	// public CalculateTransactionTotalPartitionReader
	// calculateTransactionTotalPartitionReader() {
	// return new CalculateTransactionTotalPartitionReader();
	// }

	public void sayHello() {
		System.out.println("###### Hai");
	}

	private int from;

	private int to;

	@Bean
	@StepScope
	public JdbcCursorItemReader<Customer> customerIDItemReader(@Value("#{jobParameters[fromId]}") Integer fromId) {
		JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<Customer>();
		// int fromId =
		// Integer.parseInt(reader.getExecutionContextKey("fromId"));
		// int toId = Integer.parseInt(reader.getExecutionContextKey("toId"));
		// System.out.println("From - " + fromId + " To " + toId);
		// reader.
		// System.out.println("ToId is:" +
		// reader.getExecutionContextKey("toId"));

		// System.out.println("To: " +
		// this.stepExecution.getExecutionContext().getInt("toId"));
		System.out.println("##################From: " + fromId);
		// System.out.println("To: " + toId);
		//
		// System.out.println(ctx.getBean("ExecutionContext"));
		reader.setSql(
				"SELECT CustomerID, FirstName, LastName, TransactionTotal FROM Customer WHERE CustomerId >= 100000 and CustomerId < 100010");
		reader.setDataSource(dataSource);
		reader.setRowMapper(new CustomerRowMapper());
		try {
			reader.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reader;
	}

	@Bean
	public ItemProcessor<Customer, Customer> customerItemProcessor() {
		CalculateTransactionTotalProcessor processor = new CalculateTransactionTotalProcessor();
		processor.setTemplate(template);
		return processor;
	}

	@Bean
	public Partitioner partitioner() {
		return new RangePartitioner();
	}

	// Number of Sub-Threads is set here!
	// Due to possible unknown side effects, we shall keep both Values on the
	// same value (see
	// StandaloneInfrastructureConfiguration.java)
	@Bean
	public Step partitionStep() {
		return stepBuilders.get("partitionStep").partitioner(calculateTransactionTotalPartitioningStep())
				.partitioner("calculateTransactionTotalStep", partitioner()).gridSize(4).taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	public Step calculateTransactionTotalPartitioningStep() {
		CalculateTransactionTotalPartitioningListener listener = new CalculateTransactionTotalPartitioningListener();
		listener.setStep(this);
		return stepBuilders.get("calculateTransactionTotalPartitioningStep").<Customer, Customer> chunk(50)
				.reader(customerIDItemReader(null)).chunk(50).processor(customerItemProcessor()).chunk(50)
				.writer(customerItemWriter()).transactionManager(platformTransactionManager).listener(listener).build();
	}

	@Bean
	public JdbcBatchItemWriter<Customer> customerItemWriter() {
		JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<Customer>();
		writer.setSql("UPDATE Customer SET TransactionTotal = :TTO WHERE CustomerID = :custID");
		writer.setItemSqlParameterSourceProvider(new CustomerSqlParameterSourceProvider());
		writer.setDataSource(dataSource);
		writer.afterPropertiesSet();
		return writer;
	}
}
