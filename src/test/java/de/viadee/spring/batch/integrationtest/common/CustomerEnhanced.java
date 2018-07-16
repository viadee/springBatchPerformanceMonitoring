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
