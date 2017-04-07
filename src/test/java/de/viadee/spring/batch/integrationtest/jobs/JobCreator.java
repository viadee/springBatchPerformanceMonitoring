package de.viadee.spring.batch.integrationtest.jobs;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.integrationtest.jobs.calculategradepoints.CalculateTransactionTotalJobConfig;
import de.viadee.spring.batch.integrationtest.jobs.formatnames.FormatNamesJobConfig;
import de.viadee.spring.batch.integrationtest.jobs.preparedatabase.PrepareDatabaseJobConfig;

@Import({ PrepareDatabaseJobConfig.class, CalculateTransactionTotalJobConfig.class, FormatNamesJobConfig.class })
@Component
public class JobCreator {

    // private static Logger LOG = Logger.getLogger(JobCreator.class);
    //
    // @Autowired
    // private PrepareDatabaseJob prepareDatabaseJob;
    //
    // @Autowired
    // private CalculateGradePointsAverageJob calculateGradePointsAverageJob;
    //
    // @Autowired
    // private FormatNamesJob formatNamesJob;

    // public Job getPrepareDatabaseJob() {
    // System.out.println("#############getPrepareDB");
    // return prepareDatabaseJob.getPrepareDatabaseJob();
    // }
    //
    // public Job getCalculateGradePointsAverageJob() {
    // return calculateGradePointsAverageJob.getCalculateGradePointsAverageJob();
    // }
    //
    // public Job getFormatNamesJob() {
    // return formatNamesJob.getFormatNamesJob();
    // }
}
