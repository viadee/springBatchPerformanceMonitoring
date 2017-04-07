package de.viadee.spring.batch.integrationtest.jobs.preparedatabase;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DelayTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // TODO Auto-generated method stub
        Thread.sleep(2000);
        return RepeatStatus.FINISHED;
    }

}
