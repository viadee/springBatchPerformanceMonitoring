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
package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import javax.sql.DataSource;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.CustomerRowMapper;

@Component
public class CalculateTransactionTotalPartitionReader implements ItemReader<Customer> {

    @Autowired
    DataSource dataSource;

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return customerIdItemReader().read();
    }

    @Bean
    public ItemReader<Customer> customerIdItemReader() {
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<Customer>();
        // int fromId = Integer.parseInt(reader.getExecutionContextKey("fromId"));
        // int toId = Integer.parseInt(reader.getExecutionContextKey("toId"));
        // System.out.println("From - " + fromId + " To " + toId);
        // reader.
        // System.out.println("ToId is:" + reader.getExecutionContextKey("toId"));

        // System.out.println("To: " + this.stepExecution.getExecutionContext().getInt("toId"));
        // System.out.println("From: " + fromId);
        // System.out.println("To: " + toId);
        reader.setSql(
                "SELECT CustomerID, FirstName, LastName, TransactionTotal FROM Customer WHERE CustomerId >= 100000 and CustomerId < 100010");
        reader.setDataSource(dataSource);
        reader.setRowMapper(new CustomerRowMapper());
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reader;
    }

    public void open(ExecutionContext ctx) {
        System.out.println("#####################" + ctx.getInt("fromInt"));

    }

}