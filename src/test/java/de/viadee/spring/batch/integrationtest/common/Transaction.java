package de.viadee.spring.batch.integrationtest.common;

import org.apache.log4j.Logger;

public class Transaction {

    private static Logger LOG = Logger.getLogger(Transaction.class);

    private int customerID;

    private int amount;

    public Transaction(int customerID, int amount) {
        super();

        this.customerID = customerID;
        this.amount = amount;
    }

    public Transaction() {
        super();
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
