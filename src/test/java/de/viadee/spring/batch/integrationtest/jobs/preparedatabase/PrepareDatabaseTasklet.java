package de.viadee.spring.batch.integrationtest.jobs.preparedatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.common.Customer;
import de.viadee.spring.batch.integrationtest.common.Transaction;

@Component
public class PrepareDatabaseTasklet implements Tasklet {

    private static Logger LOG = Logger.getLogger(PrepareDatabaseTasklet.class);

    private NamedParameterJdbcTemplate template;

    private int testDataSize;

    private int maxGrades;

    private static final String SQL = "INSERT INTO Customer VALUES (:customerID, :firstname, :lastname, 0)";

    private static final String SQL2 = "INSERT INTO Transaction VALUES (:transactionID, :customerID, :amount)";

    public void setTemplate(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Autowired
    DataBaseFiller dataBaseFiller;

    public void setTestDataSize(int amount) {
        this.testDataSize = amount;
    }

    public void setMaxGrades(int maxGrades) {
        this.maxGrades = maxGrades;
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Customer> customers = dataBaseFiller.getCustomerList(testDataSize);
        for (Customer student : customers) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("customerID", "" + student.getCustomerID());
            map.put("firstname", student.getFirstName());
            map.put("lastname", student.getLastName());
            template.update(SQL, map);
        }
        List<Transaction> transactions = dataBaseFiller.generateGrades(customers, maxGrades);
        int i = 0;
        for (Transaction grade : transactions) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("customerID", "" + grade.getCustomerID());
            map.put("amount", "" + grade.getAmount());
            map.put("transactionID", "" + i++);
            template.update(SQL2, map);
        }
        customers = null;
        transactions = null;
        dataBaseFiller.cleanCustomerID();
        return RepeatStatus.FINISHED;
    }

}
