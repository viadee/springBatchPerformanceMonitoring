package de.viadee.spring.batch.integrationtest.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerRowMapper implements RowMapper<Customer> {

    private static Logger LOG = Logger.getLogger(CustomerRowMapper.class);

    // TODO: Write Test using Mockito
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {

        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("CustomerID"));
        customer.setFirstName(rs.getString("FirstName"));
        customer.setLastName(rs.getString("LastName"));
        customer.setTransactionTotal(rs.getFloat("TransactionTotal"));
        return customer;
    }

}
