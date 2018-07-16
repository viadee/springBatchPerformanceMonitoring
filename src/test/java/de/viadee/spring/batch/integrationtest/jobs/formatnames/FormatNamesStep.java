/**
 * Copyright � 2016, viadee Unternehmensberatung AG
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
