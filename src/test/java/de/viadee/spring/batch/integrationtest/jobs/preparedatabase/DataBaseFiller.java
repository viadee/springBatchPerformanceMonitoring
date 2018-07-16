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
package de.viadee.spring.batch.integrationtest.jobs.preparedatabase;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.Transaction;

@Component
public class DataBaseFiller {

    private static int x = 0;

    private static final Logger LOG = Logger.getLogger(DataBaseFiller.class);

    private static List<Integer> customerIDNumber = new ArrayList<Integer>();

    private static String[] firstNames = { "Bob", "Amelia", "Olivia", "Emily", "Ava", "Isla", "Jessica", "Poppy",
            "Isabella", "Sophie", "Mia", "Ruby", "Lily", "Grace", "Evie", "Sophia", "Ella", "Scarlett", "Chloe",
            "Isabelle", "Freya", "Charlotte", "Sienna", "Daisy", "Phoebe", "Millie", "Eva", "Alice", "Lucy", "Florence",
            "Sofia", "Layla", "Lola", "Holly", "Imogen", "Molly", "Matilda", "Lilly", "Rosie", "Elizabeth", "Erin",
            "Maisie", "Lexi", "Ellie", "Hannah", "Evelyn", "Abigail", "Elsie", "Summer", "Megan", "Jasmine", "Oliver",
            "Jack", "Harry", "Jacob", "Charlie", "Thomas", "Oscar", "William", "James", "George", "Alfie", "Joshua",
            "Noah", "Ethan", "Muhammad", "Archie", "Leo", "Henry", "Joseph", "Samuel", "Riley", "Daniel", "Mohammed",
            "Alexander", "Max", "Lucas", "Mason", "Logan", "Isaac", "Benjamin", "Dylan", "Jake", "Edward", "Finley",
            "Freddie", "Harrison", "Tyler", "Sebastian", "Zachary", "Adam", "Theo", "Jayden", "Arthur", "Toby", "Luke",
            "Lewis", "Matthew", "Harvey", "Harley", "David" };

    private static String[] lastNames = { "Ackland", "Adams", "Allington", "Ashton", "Almond", "Badham", "Bancroft",
            "Bail", "Barrymore", "Baskin", "Beecroft", "Benett", "Bloomfield", "Bostwick", "Bosworth", "Boyle",
            "Bradshaw", "Bridges", "Broderick", "Browning", "Burton", "Callahan", "Carmody", "Catrall", "Chamberlain",
            "Clancy", "Combe", "Cort", "Corraface", "Corey", "Crichton", "Cromwell", "Darabont", "Davonport", "Dennehy",
            "Densham", "Devaney", "Donovan", "Dunn", "Edgecomb", "Eliot", "Farnsworth", "Fairchild", "Featherstone",
            "Forsythe", "Gady", "Gage", "Gallagher", "Gayheart", "Grantham", "Graves", "Graysmark", "Greaves",
            "Haddington", "Haddock", "Hamlin", "Hannigan", "Hardin", "Harrington", "Hathaway", "Hartshorn", "Hawn",
            "Hayman", "Henderson", "Hennessy", "Hensley", "Hiliard", "Hoskins", "Huxley", "Jones", "Kendall", "Kinmont",
            "Kirkwood", "Kober", "Lancaster", "Lankford", "Lansbury", "Leachman", "Leech", "Lester", "Lewis",
            "Lineback", "Lithgow", "Lockhart", "Locklear", "Lockwood", "Longfellow", "Lorring", "Madigan", "Malfoy",
            "Marley", "Marshal", "Mayhew", "McGowan", "McLeod", "Millington", "Mills", "Monroe", "Morriss", "Musgrave",
            "Neil", "Neville", "Nicksay", "Onnington", "Patton", "Payne", "Perlman", "Poe", "Prentiss", "Preston",
            "Primes", "Prinsloo", "Roades", "Robinson", "Reacock", "Ross", "Rushton", "Satchmore", "Shepherd",
            "Simpson", "Stanton", "Sterling", "Thackeray", "Thuringer", "Ward", "Warriner", "Warrington", "Whitman",
            "Willoughby", "Wiltshire", "Winfield", "Winston", "Wyler" };

    public List<Customer> getCustomerList(int amount) {
        LOG.debug("###DBFillerCalled");
        List<Customer> customerList = new ArrayList<Customer>();
        // Old ID logic
        // if (amount < 10000) {
        // while (customerIDNumber.size() < amount) {
        // int rand = 100000 + (int) (Math.random() * ((999999 - 100000) + 1));
        // if (!customerIDNumber.contains((Integer) rand)) {
        // customerIDNumber.add(rand);
        // }
        // }
        // } else {
        // for (int i = 0; i < amount; i++) {
        // customerIDNumber.add(100000 + i);
        // }
        // }

        for (int i = 0; i < amount; i++) {
            customerIDNumber.add(100000 + i);
        }
        for (int x = 0; x < customerIDNumber.size(); x++) {
            LOG.trace("For Number: " + x + " the ID is: " + customerIDNumber.get(x));
        }

        for (int i = 0; i < amount; i++) {
            int randFirstName = (int) (Math.random() * firstNames.length);
            int randLastName = (int) (Math.random() * lastNames.length);
            Customer customer = new Customer(customerIDNumber.get(i), firstNames[randFirstName],
                    lastNames[randLastName]);
            customerList.add(customer);
            if (x % 1000 == 0) {
                LOG.debug(x++ + " Customer Items created!");
            }
            LOG.trace("Random customer has been created: " + customer.toString());
        }
        LOG.debug("DBFiller Finished " + customerList.size());

        return customerList;
    }

    public List<Transaction> generateGrades(List<Customer> customers, int maxTransactions) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        int counter = 0;
        for (Customer customer : customers) {
            // How many grades shall be created
            int noOfTransactions = (int) (Math.random() * 5 + 1);
            if (counter == 6) {
                noOfTransactions = maxTransactions;
                LOG.debug("###Customer " + customer.getFirstName() + " " + customer.getLastName()
                        + " gets the Transactions");
            }
            for (int i = 0; i < noOfTransactions; i++) {
                int amount = (int) (Math.random() * 1500);
                transactions.add(new Transaction(customer.getCustomerID(), amount));
            }
            counter++;
        }
        return transactions;
    }

    public void cleanCustomerID() {
        customerIDNumber = null;
    }
}
