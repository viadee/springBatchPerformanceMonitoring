package de.viadee.spring.batch.integrationtest.common;

public class Transaction {

    private int customerID;

    private int amount;

    public Transaction(final int customerID, final int amount) {
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

    public void setCustomerID(final int customerID) {
        this.customerID = customerID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }
}
