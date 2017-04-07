package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import javax.sql.DataSource;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.CustomerRowMapper;

@Component
public class CalculateTransactionTotalPartitionReader implements ItemReader<Customer> {

    @Autowired
    DataSource dataSource;

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return customerIdItemReader().read();
    }

    @Bean
    public ItemReader<Customer> customerIdItemReader() {
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<Customer>();
        // int fromId = Integer.parseInt(reader.getExecutionContextKey("fromId"));
        // int toId = Integer.parseInt(reader.getExecutionContextKey("toId"));
        // System.out.println("From - " + fromId + " To " + toId);
        // reader.
        // System.out.println("ToId is:" + reader.getExecutionContextKey("toId"));

        // System.out.println("To: " + this.stepExecution.getExecutionContext().getInt("toId"));
        // System.out.println("From: " + fromId);
        // System.out.println("To: " + toId);
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

    public void open(ExecutionContext ctx) {
        System.out.println("#####################" + ctx.getInt("fromInt"));

    }

}