/**
 * Copyright � 2016, viadee Unternehmensberatung AG
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
