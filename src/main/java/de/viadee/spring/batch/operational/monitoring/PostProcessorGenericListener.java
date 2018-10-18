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
package de.viadee.spring.batch.operational.monitoring;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.step.AbstractStep;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.infrastructure.ActivityNotifier;
import de.viadee.spring.batch.infrastructure.JobEndQueueCleaner;
import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.persistence.SBPMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.SBPMJobDAO;
import de.viadee.spring.batch.persistence.SBPMStepDAO;
import de.viadee.spring.batch.persistence.types.SBPMJob;

/**
 * This class implements the BeanPostProcessor. Each Bean initialization
 * (regarding the Spring Batch Process to be monitored) will trigger the
 * "postProcessAfterInitialization" method below. It handles the initialization
 * of the Job-, Step-, and Chunk-Listeners and assigns them to those Spring
 * Batch Objects.
 * 
 */
@Component
public class PostProcessorGenericListener implements BeanPostProcessor {

	// Used for creating the ID of the Step in the DB
	private static int lastStepID = 1;

	// Used for creating the ID for the Job in the DB
	private static int lastJobID = 1;

	@Autowired
	JobEndQueueCleaner asyncTest;

	@Autowired
	ChronoHelper chronoHelper;

	@Autowired
	private SBPMJobDAO sPBMJobDAO;

	@Autowired
	private SBPMStepDAO sPBMStepDAO;

	@Autowired
	private SBPMChunkExecutionQueue sPBMChunkExecutionQueue;

	@Autowired
	private ActivityNotifier notificationHolder;

	private final Map<Object, String> invokes = new HashMap<Object, String>();

	private static final Logger LOGGER = LoggingWrapper.getLogger(PostProcessorGenericListener.class);

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
		return bean;
	}

	/**
	 * Postprocess to initialize the job- and steplistener into the running the
	 * job You need to know on what abstract level you want to use the listener
	 */
	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) {

		if (bean instanceof AbstractJob) {
			LOGGER.debug("PostProcessorGenericListener noticed the initialization of an AbstractJob Bean");
			final BatchJobListener batchJobListener = new BatchJobListener();
			batchJobListener.setSPBMJob(new SBPMJob(lastJobID++, beanName, 0));
			batchJobListener.setSPBMJobDAO(sPBMJobDAO);
			batchJobListener.setChronoHelper(chronoHelper);
			batchJobListener.setNotificationHolder(notificationHolder);
			batchJobListener.setAsyncTest(asyncTest);
			((AbstractJob) bean).registerJobExecutionListener(batchJobListener);
			invokes.put(bean, "Abstract Job " + beanName);
			LOGGER.trace("Adding " + beanName + " to the invokes list");
		}
		if (bean instanceof AbstractStep) {
			LOGGER.debug("PostProcessorGenericListener noticed the initialization of an AbstractStep Bean");
			final BatchStepListener batchStepListener = new BatchStepListener();
			// batchStepListener.setID(lastStepID++);
			batchStepListener.setChronoHelper(chronoHelper);
			batchStepListener.setSPBMStepDAO(sPBMStepDAO);
			((AbstractStep) bean).registerStepExecutionListener(batchStepListener);
			invokes.put(bean, "Abstract Step " + beanName);
			LOGGER.trace("Adding " + beanName + " to the invokes list");
		}
		if (bean instanceof TaskletStep) {
			LOGGER.debug("PostProcessorGenericListener noticed the initialization of a TaskletStep Bean");
			final BatchChunkListener batchChunkListener = new BatchChunkListener();
			((TaskletStep) bean).registerChunkListener(batchChunkListener);
			batchChunkListener.setChronoHelper(chronoHelper);
			batchChunkListener.setSPBMChunkExecutionQueue(sPBMChunkExecutionQueue);
			invokes.put(bean, "Chunk Tasklet " + beanName);
			LOGGER.trace("Adding " + beanName + " to the invokes list");
		}

		return bean;
	}

	/**
	 * For JUnit testing
	 * 
	 * @return information for testing purposes
	 */
	public Map<Object, String> getInvokes() {
		return this.invokes;
	}
}
