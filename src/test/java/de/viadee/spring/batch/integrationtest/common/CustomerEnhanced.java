package de.viadee.spring.batch.integrationtest.common;

import org.apache.log4j.Logger;

public class CustomerEnhanced extends Customer {

    private static Logger LOG = Logger.getLogger(CustomerEnhanced.class);

    private String firstNameUpperCase, lastNameUpperCase, firstNameLowerCase, lastNameLowerCase;

    public CustomerEnhanced() {
        super();
    }

    public CustomerEnhanced(int customerID, String firstName, String lastName, float transactionTotal) {
        super(customerID, firstName, lastName, transactionTotal);
    }

    public String getFirstNameUpperCase() {
        return firstNameUpperCase;
    }

    public void setFirstNameUpperCase(String firstNameUpperCase) {
        this.firstNameUpperCase = firstNameUpperCase;
    }

    public String getLastNameUpperCase() {
        return lastNameUpperCase;
    }

    public void setLastNameUpperCase(String lastNameUpperCase) {
        this.lastNameUpperCase = lastNameUpperCase;
    }

    public String getFirstNameLowerCase() {
        return firstNameLowerCase;
    }

    public void setFirstNameLowerCase(String firstnameLowerCase) {
        this.firstNameLowerCase = firstnameLowerCase;
    }

    public String getLastNameLowerCase() {
        return lastNameLowerCase;
    }

    public void setLastNameLowerCase(String lastNameLowerCase) {
        this.lastNameLowerCase = lastNameLowerCase;
    }

}
