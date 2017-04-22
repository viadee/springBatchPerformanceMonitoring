package de.viadee.spring.batch.integrationtest.common;

public class Customer {

    private int customerID;

    private String firstName, lastName;

    private float transactionTotal;

    public Customer(final int customerID, final String firstName, final String lastName) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.transactionTotal = 0;
    }

    public Customer(final int customerID, final String firstName, final String lastName, final float transactionTotal) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.transactionTotal = transactionTotal;
    }

    public Customer() {
        super();
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(final int customerID) {
        this.customerID = customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public float getTransactionTotal() {
        return transactionTotal;
    }

    public void setTransactionTotal(final float transactionTotal) {
        this.transactionTotal = transactionTotal;
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("<");
        str.append(this.customerID);
        str.append(">, <");
        str.append(this.firstName);
        str.append(">, <");
        str.append(this.lastName);
        str.append(">, <");
        str.append(this.getTransactionTotal());
        str.append(">");
        return str.toString();
    }

}
