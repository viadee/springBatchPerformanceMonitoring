package de.viadee.spring.batch.integrationtest.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        final Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("CustomerID"));
        customer.setFirstName(rs.getString("FirstName"));
        customer.setLastName(rs.getString("LastName"));
        customer.setTransactionTotal(rs.getFloat("TransactionTotal"));
        return customer;
    }

}
