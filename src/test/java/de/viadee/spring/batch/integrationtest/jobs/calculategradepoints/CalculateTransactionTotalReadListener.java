package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CalculateTransactionTotalReadListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // int fromId = stepExecution.getExecutionContext().getInt("fromId");
        // int toId = stepExecution.getExecutionContext().getInt("toId");
        // System.out.println("########## From: " + stepExecution.getExecutionContext().getInt("fromId") + " to: "
        // + stepExecution.getExecutionContext().getInt("toId"));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // TODO Auto-generated method stub
        return null;
    }

}
