package de.viadee.spring.batch.integrationtest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

//@Controller
public class JobLauncherController {

	private static final String JOB_PARAM = "job";

	private JobLauncher jobLauncher;

	private JobRegistry jobRegistry;

	public JobLauncherController(JobLauncher jobLauncher, JobRegistry jobRegistry) {
		super();
		this.jobLauncher = jobLauncher;
		this.jobRegistry = jobRegistry;
	}

	@RequestMapping(value = "joblauncher", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void launch(@RequestParam String job, HttpServletRequest request) throws Exception {
		JobParametersBuilder builder = extractParameters(request);
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
		try {
			MainApplication.main(null);
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JobParametersBuilder extractParameters(HttpServletRequest request) {
		return new JobParametersBuilder();
	}

}
