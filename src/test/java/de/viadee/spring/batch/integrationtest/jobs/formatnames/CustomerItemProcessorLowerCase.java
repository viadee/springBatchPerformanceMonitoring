package de.viadee.spring.batch.integrationtest.jobs.formatnames;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.CustomerEnhanced;

@Component
public class CustomerItemProcessorLowerCase implements ItemProcessor<CustomerEnhanced, CustomerEnhanced> {

    private static Logger LOG = Logger.getLogger(CustomerItemProcessorLowerCase.class);

    public CustomerEnhanced process(CustomerEnhanced item) throws Exception {

        item.setFirstNameLowerCase(item.getFirstName().toLowerCase());
        item.setLastNameLowerCase(item.getLastName().toLowerCase());
        return item;
    }

}
