package de.viadee.spring.batch.integrationtest.common;

import org.apache.log4j.Logger;

public class Customer {

	private static Logger LOG = Logger.getLogger(Customer.class);

	private int customerID;

	private String firstName, lastName;

	private float transactionTotal;

	public Customer(int customerID, String firstName, String lastName) {
		this.customerID = customerID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.transactionTotal = 0;
	}

	public Customer(int customerID, String firstName, String lastName, float transactionTotal) {
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

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public float getTransactionTotal() {
		return transactionTotal;
	}

	public void setTransactionTotal(float transactionTotal) {
		this.transactionTotal = transactionTotal;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
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
