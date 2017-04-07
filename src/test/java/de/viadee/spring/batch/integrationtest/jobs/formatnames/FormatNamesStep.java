package de.viadee.spring.batch.integrationtest.jobs.formatnames;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.AbstractTaskletStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.CustomerEnhanced;
import de.viadee.spring.batch.integrationtest.common.CustomerRowMapper;

@Configuration
public class FormatNamesStep {

    private static Logger LOG = Logger.getLogger(FormatNamesStep.class);

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public Step getFormatNamesStep() {
        StepBuilder stepBuilder = stepBuilders.get("GetFormatNamesStep");
        SimpleStepBuilder<Customer, CustomerEnhanced> newStepBuilder = stepBuilder
                .<Customer, CustomerEnhanced> chunk(20).reader(customerItemReader())
                .processor(getCompositeCustomerItemProcessor()).writer(getCompositeCustomerItemWriter());
        AbstractTaskletStepBuilder<SimpleStepBuilder<Customer, CustomerEnhanced>> abstractTaskletStep = newStepBuilder
                .transactionManager(platformTransactionManager);
        TaskletStep taskletStep = abstractTaskletStep.build();
        return taskletStep;
    }

    @Bean
    public ItemReader<Customer> customerItemReader() {
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<Customer>();
        reader.setSql("SELECT CustomerID, FirstName, LastName, TransactionTotal FROM Customer");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new CustomerRowMapper());
        return reader;
    }

    @Bean
    public ItemProcessor<Customer, CustomerEnhanced> customerItemProcessorUpperCase() {
        return new CustomerItemProcessorUpperCase();
    }

    @Bean
    public ItemProcessor<CustomerEnhanced, CustomerEnhanced> customerItemProcessorLowerCase() {
        return new CustomerItemProcessorLowerCase();
    }

    @Bean
    public CompositeItemProcessor getCompositeCustomerItemProcessor() {
        CompositeItemProcessor compositeItemProcessor = new CompositeItemProcessor();
        List<ItemProcessor> delegates = new ArrayList<ItemProcessor>();
        delegates.add(customerItemProcessorUpperCase());
        delegates.add(customerItemProcessorLowerCase());
        compositeItemProcessor.setDelegates(delegates);
        return compositeItemProcessor;
    }

    @Bean
    public JdbcBatchItemWriter<CustomerEnhanced> customerItemWriterUpperCase() {
        JdbcBatchItemWriter<CustomerEnhanced> writer = new JdbcBatchItemWriter<CustomerEnhanced>();
        writer.setSql(
                "INSERT INTO CustomerUpperCase (CustomerID, FirstName, LastName, TransactionTotal) VALUES (:customerID,:firstNameUpperCase,:lastNameUpperCase,:transactionTotal)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<CustomerEnhanced>());
        writer.setDataSource(dataSource);
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter<CustomerEnhanced> customerItemWriterLowerCase() {
        JdbcBatchItemWriter<CustomerEnhanced> writer = new JdbcBatchItemWriter<CustomerEnhanced>();
        writer.setSql(
                "INSERT INTO CustomerLowerCase (CustomerID, FirstName, LastName, TransactionTotal) VALUES (:customerID,:firstNameLowerCase,:lastNameLowerCase,:transactionTotal)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<CustomerEnhanced>());
        writer.setDataSource(dataSource);
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public CompositeItemWriter getCompositeCustomerItemWriter() {
        CompositeItemWriter compositeItemWriter = new CompositeItemWriter();
        List<ItemWriter> delegates = new ArrayList<ItemWriter>();
        delegates.add(customerItemWriterUpperCase());
        delegates.add(customerItemWriterLowerCase());
        compositeItemWriter.setDelegates(delegates);
        return compositeItemWriter;
    }
}
