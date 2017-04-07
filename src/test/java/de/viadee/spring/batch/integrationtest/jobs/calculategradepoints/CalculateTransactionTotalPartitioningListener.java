package de.viadee.spring.batch.integrationtest.jobs.calculategradepoints;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CalculateTransactionTotalPartitioningListener implements StepExecutionListener {

    private CalculateTransactionTotalPartitioningStepConfig step;

    public void setStep(CalculateTransactionTotalPartitioningStepConfig step) {
        this.step = step;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("########## Name: " + stepExecution.getExecutionContext().getString("name") + " From: "
                + stepExecution.getExecutionContext().getInt("fromId") + " to: "
                + stepExecution.getExecutionContext().getInt("toId"));
        step.sayHello();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // TODO Auto-generated method stub
        return null;
    }

}
