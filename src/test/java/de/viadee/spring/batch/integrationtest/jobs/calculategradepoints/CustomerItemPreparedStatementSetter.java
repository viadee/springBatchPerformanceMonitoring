package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;

@Component
public class CustomerItemPreparedStatementSetter implements ItemPreparedStatementSetter<Customer> {

    private static Logger LOG = Logger.getLogger(CustomerItemPreparedStatementSetter.class);

    public void setValues(Customer item, PreparedStatement ps) throws SQLException {
        ps.setFloat(1, item.getTransactionTotal());
        ps.setInt(2, item.getCustomerID());
    }
}
