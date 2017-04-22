package de.viadee.spring.batch.integrationtest.common;

public class CustomerEnhanced extends Customer {

    private String firstNameUpperCase, lastNameUpperCase, firstNameLowerCase, lastNameLowerCase;

    public CustomerEnhanced() {
        super();
    }

    public CustomerEnhanced(final int customerID, final String firstName, final String lastName,
            final float transactionTotal) {
        super(customerID, firstName, lastName, transactionTotal);
    }

    public String getFirstNameUpperCase() {
        return firstNameUpperCase;
    }

    public void setFirstNameUpperCase(final String firstNameUpperCase) {
        this.firstNameUpperCase = firstNameUpperCase;
    }

    public String getLastNameUpperCase() {
        return lastNameUpperCase;
    }

    public void setLastNameUpperCase(final String lastNameUpperCase) {
        this.lastNameUpperCase = lastNameUpperCase;
    }

    public String getFirstNameLowerCase() {
        return firstNameLowerCase;
    }

    public void setFirstNameLowerCase(final String firstnameLowerCase) {
        this.firstNameLowerCase = firstnameLowerCase;
    }

    public String getLastNameLowerCase() {
        return lastNameLowerCase;
    }

    public void setLastNameLowerCase(final String lastNameLowerCase) {
        this.lastNameLowerCase = lastNameLowerCase;
    }

}
