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
package de.viadee.spring.batch.operational.setupverification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener;

/**
 * This class is used to ensure that assumptions according to the environment,
 * this tool shall run in, apply. It tests, if SpringAOP is functional and if
 * the PostProcessor has noticed initialization of the Spring Batch Beans.
 * 
 * The ContainerTest cannot(!) be run on its own. It needs to be piggybacked by
 * a particular Spring Batch job. Since the "/src/test" won't be delivered by a
 * possible exported .jar file, this Class is placed right where you just found
 * it.
 * 
 * To run the ContainerTest, you need to create an own and empty TestClass
 * (inside your Spring Batch Project) providing your Application configuration.
 * Your TestClass needs to extend this class. Example:
 * 
 * import org.springframework.test.context.ContextConfiguration;
 *
 * import your.package.ApplicationConfiguration;
 * 
 * (at)ContextConfiguration(classes = { ApplicationConfiguration.class })
 * 
 *                               public class ContainerTest extends
 *                               de.viadee.spring.batch.operational.
 *                               setupverification.ContainerTest {
 * 
 *                               }
 * 
 */
@ContextConfiguration(classes = { AspectTestClass.class, TestAspect.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ContainerTest {

	@Autowired
	ApplicationContext context;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	SimpleJob[] simpleJobs;

	@Autowired
	AspectTestClass aspectTestClass;

	@Autowired
	TestAspect testAspect;

	@Test
	public void getApplicationContextTest() {
		assertNotNull(
				"Batch monitoring depends on a loaded spring context, which is not properly configured in this case.",
				context);
	}

	@Test
	public void getJobLauncherTest() {

		assertNotNull("In order to run this test, a initialized JobLauncher is needed which couldn't be found.",
				jobLauncher);
	}

	@Test
	public void getSimpleJobTest() {
		assertNotNull("There has to be at least one Job", simpleJobs);
	}

	@Test
	public void checkIfPostProcessorExistsTest() {
		assertNotNull(
				"The PostProcessorGenericListener class couldn't be found. Assuming that the monitoring tool HAS NOT been loaded.",
				context.getBean(PostProcessorGenericListener.class));
	}

	@Test
	public void checkIfPostProcessorBeforeInitialisationIsCalledTest() {
		// Given
		// A job having a PostProcessor registred and been run
		final de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener listener = context
				.getBean(de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener.class);

		// Times, the PostProcessorGenericListener.class was invoked and has
		// registered a listener to a bean
		final Map<Object, String> map = listener.getInvokes();

		// When
		// Check if the amount of invokes is greater than 0
		assertEquals(
				"There has been no invocation of the PostProcessorGenericListener. Either no job / step has run or the PostProcessorGenericListener hasn't noticed the bean initialization.",
				map.isEmpty(), false);
	}

	@Test
	public void checkIfTaskletWasInvokedByBeanPostProcessorTest() {
		// Given
		// "Invoked"-Map of the PostProcessorGenericListener class
		final de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener listener = context
				.getBean(de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener.class);
		final Map<Object, String> map = listener.getInvokes();
		boolean invoked = false;

		// When
		// At least one Tasklet Element has been added in the invoked map
		for (final Map.Entry<Object, String> entry : map.entrySet()) {
			if (entry.getKey() instanceof org.springframework.batch.core.step.tasklet.TaskletStep) {
				invoked = true;
			}
		}

		// Then
		// Success
		assertEquals("PostProcessorGenericListener has not invoked at least one tasklet bean.", invoked, true);
	}

	@Test
	public void checkIfJobWasInvokedByBeanPostProcessorTest() {
		// Given
		// "Invoked"-Map of the PostProcessorGenericListener class
		final de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener listener = context
				.getBean(de.viadee.spring.batch.operational.monitoring.PostProcessorGenericListener.class);
		final Map<Object, String> map = listener.getInvokes();
		boolean invoked = false;

		// When
		// At least one Job Element has been added in the invoked map
		for (final Map.Entry<Object, String> entry : map.entrySet()) {
			if (entry.getKey() instanceof org.springframework.batch.core.job.SimpleJob) {
				invoked = true;
			}
		}

		// Then
		// Success
		assertEquals("PostProcessorGenericListener has not invoked at least one job bean.", invoked, true);
	}

	@Test
	public void checkIfAspectsWorkTest() {
		// Given
		// A test class containing a method to be modified

		// An Aspect Class

		// The actual instances

		final int value = aspectTestClass.returnVal(10);
		assertEquals(12, value);
	}

}
