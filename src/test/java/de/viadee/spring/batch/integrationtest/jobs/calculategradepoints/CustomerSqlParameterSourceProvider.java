package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import org.apache.log4j.Logger;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;

@Component
public class CustomerSqlParameterSourceProvider implements ItemSqlParameterSourceProvider<Customer> {

    private static Logger LOG = Logger.getLogger(CustomerSqlParameterSourceProvider.class);

    public SqlParameterSource createSqlParameterSource(Customer item) {

        MapSqlParameterSource paramSrc = new MapSqlParameterSource();
        paramSrc.addValue("custID", item.getCustomerID());
        paramSrc.addValue("TTO", item.getTransactionTotal());
        return paramSrc;
    }
}
