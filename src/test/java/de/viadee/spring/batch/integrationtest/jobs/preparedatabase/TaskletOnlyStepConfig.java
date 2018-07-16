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

import org.apache.log4j.Logger;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import de.viadee.spring.batch.integrationtest.configuration.StandaloneInfrastructureConfiguration;

@Import(StandaloneInfrastructureConfiguration.class)
@Configuration
public class TaskletOnlyStepConfig {

    private static Logger LOG = Logger.getLogger(TaskletOnlyStepConfig.class);

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Bean
    public Tasklet prepareDatabaseTasklet() {
        final PrepareDatabaseTasklet tasklet = new PrepareDatabaseTasklet();
        tasklet.setTemplate(template);
        tasklet.setTestDataSize(80);
        tasklet.setMaxGrades(5);
        return tasklet;
    }

    @Bean
    public Tasklet delayTasklet() {
        return new DelayTasklet();
    }

    @Bean
    public Step delayTaskletStep() {
        return stepBuilders.get("DelayTaskletStep").tasklet(delayTasklet())
                .transactionManager(platformTransactionManager).build();
    }

    public Step getDelayTaskletStep() {
        return delayTaskletStep();
    }

    @Bean
    public Step taskletOnlyStep() {
        return stepBuilders.get("TaskletOnlyStep").tasklet(prepareDatabaseTasklet())
                .transactionManager(platformTransactionManager).build();
    }

    public Step getStep() {
        return taskletOnlyStep();
    }
}
