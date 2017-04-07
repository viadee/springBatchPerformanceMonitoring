package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.CustomerRowMapper;

@Configuration
public class CalculateTransactionTotalStepConfig {

    private static Logger LOG = Logger.getLogger(CalculateTransactionTotalStepConfig.class);

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Bean
    public ItemReader<Customer> customerIDItemReaderFull() {
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<Customer>();
        reader.setSql("SELECT CustomerID, FirstName, LastName, TransactionTotal FROM Customer");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new CustomerRowMapper());
        return reader;
    }

    @Bean
    public ItemProcessor<Customer, Customer> customerItemProcessor() {
        CalculateTransactionTotalProcessor processor = new CalculateTransactionTotalProcessor();
        processor.setTemplate(template);
        return processor;
    }

    @Bean
    public Step calculateTransactionTotalStep() {
        return stepBuilders.get("calculateTransactionTotalStep").<Customer, Customer> chunk(20)
                .reader(customerIDItemReaderFull()).chunk(20).processor(customerItemProcessor()).chunk(20)
                .writer(customerItemWriter()).transactionManager(platformTransactionManager).build();
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
