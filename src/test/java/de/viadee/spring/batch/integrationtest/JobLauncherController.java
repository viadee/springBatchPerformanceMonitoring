package de.viadee.spring.batch.integrationtest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public class JobLauncherController {

    private static final String JOB_PARAM = "job";

    private JobLauncher jobLauncher;

    private JobRegistry jobRegistry;

    public JobLauncherController(final JobLauncher jobLauncher, final JobRegistry jobRegistry) {
        super();
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @RequestMapping(value = "joblauncher", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void launch(@RequestParam final String job, final HttpServletRequest request) throws Exception {
        final JobParametersBuilder builder = extractParameters(request);
        jobLauncher.run(jobRegistry.getJob(request.getParameter(JOB_PARAM)), builder.toJobParameters());
    }

    public void init() {
        System.out.println("#################################");
        System.out.println("#################################");
        System.out.println("########I AM YOUR SERVLET########");
        System.out.println("#################################");
        System.out.println("#################################");

    }

    public JobLauncherController() {
        System.out.println("#################################");
        System.out.println("#################################");
        System.out.println("########I AM YOUR SERVLET2########");
        System.out.println("#################################");
        System.out.println("#################################");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("Now starting the SpringBatchExamples");
    }

    private JobParametersBuilder extractParameters(final HttpServletRequest request) {
        return new JobParametersBuilder();
    }

}
