package de.viadee.spring.batch.integrationtest.jobs.formatnames;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.CustomerEnhanced;

@Component
public class CustomerItemProcessorUpperCase implements ItemProcessor<Customer, CustomerEnhanced> {

    private static Logger LOG = Logger.getLogger(CustomerItemProcessorUpperCase.class);

    public CustomerEnhanced process(Customer item) throws Exception {

        CustomerEnhanced studentEnhanced = new CustomerEnhanced(item.getCustomerID(), item.getFirstName(),
                item.getLastName(), item.getTransactionTotal());
        studentEnhanced.setFirstNameUpperCase(item.getFirstName().toUpperCase());
        studentEnhanced.setLastNameUpperCase(item.getLastName().toUpperCase());
        return studentEnhanced;
    }

}
