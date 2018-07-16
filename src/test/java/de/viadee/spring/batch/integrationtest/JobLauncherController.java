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
