package de.viadee.spring.batch.integrationtest.jobs.formatnames;

import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(de.viadee.spring.batch.integrationtest.jobs.formatnames.FormatNamesStep.class)
@Configuration
public class FormatNamesJobConfig {

    private static Logger LOG = Logger.getLogger(FormatNamesJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private FormatNamesStep formatNamesStep;

    @Bean
    public Job formatNamesJob() {
        return jobBuilders.get("formatNamesJob").start(formatNamesStep.getFormatNamesStep()).build();
    }

    public Job getFormatNamesJob() {
        return formatNamesJob();
    }
}
