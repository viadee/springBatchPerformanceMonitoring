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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.Transaction;
import de.viadee.spring.batch.integrationtest.common.TransactionRowMapper;
import de.viadee.spring.batch.integrationtest.configuration.StandaloneInfrastructureConfiguration;

@Import(StandaloneInfrastructureConfiguration.class)
@Component
@Scope("step")
public class CalculateTransactionTotalProcessor implements ItemProcessor<Customer, Customer> {

	// Section for multithreading support (partitioning)
	// @Value("#{stepExecutionContext[name]}")
	private String name;

	private static Logger LOG = Logger.getLogger(CalculateTransactionTotalProcessor.class);

	private NamedParameterJdbcTemplate template;

	private static int x = 0;

	public void setTemplate(NamedParameterJdbcTemplate template) {
		this.template = template;
	}

	private final String SELECTSQL = "SELECT CustomerID, Amount FROM Transaction WHERE CustomerID = :custID";

	@Override
	public Customer process(Customer item) throws Exception {
		Thread.sleep(50);
		LOG.debug("Processing: " + item.getFirstName() + " - " + item.getCustomerID());
		Map<String, String> map = new HashMap<String, String>();
		map.put("custID", "" + item.getCustomerID());
		List<Transaction> transaction = template.query(SELECTSQL, map, new TransactionRowMapper());
		// TODO: An dieser Stelle muss der Iterator vom Projekt verwendet werden
		LOG.debug("Item: " + item.getFirstName() + " " + item.getLastName() + " HAS got " + transaction.size()
				+ " Grade entrys");
		if (transaction.size() > 10) {
			// Thread.sleep(1000);
		}
		int accumulated = 0;
		for (Transaction myTransaction : transaction) {
			accumulated += myTransaction.getAmount();
		}
		map.put("TTO", "" + accumulated);

		String name = item.toString();
		if (name.length() >= 300) {
			name = name.substring(0, 300);
		}
		LOG.debug("Total Transaction amount for " + name + " is " + (accumulated / (float) transaction.size())
				+ " having " + transaction.size() + " transactions. - " + ++x);
		item.setTransactionTotal(accumulated);
		// BAD!
		System.out.println(name + " processing Item-ID : " + item.getCustomerID());
		return item;
	}
}
