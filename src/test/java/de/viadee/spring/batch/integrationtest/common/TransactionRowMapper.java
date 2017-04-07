package de.viadee.spring.batch.integrationtest.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

public class TransactionRowMapper implements RowMapper<Transaction> {

    private static Logger LOG = Logger.getLogger(TransactionRowMapper.class);

    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setCustomerID(rs.getInt("CustomerID"));
        transaction.setAmount(rs.getInt("Amount"));
        return transaction;
    }

}
