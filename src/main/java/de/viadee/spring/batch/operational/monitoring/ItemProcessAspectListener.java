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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.infrastructure.SBPMConfiguration;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.chronometer.Chronometer;
import de.viadee.spring.batch.persistence.SBPMItemQueue;
import de.viadee.spring.batch.persistence.types.SBPMItem;

/**
 * This class uses SpringAOP to measure any ItemProcessor on Item-Level.
 * 
 */
@Aspect
@Component
public class ItemProcessAspectListener<I> {

	@Autowired
	ChronoHelper chronoHelper;

	@Autowired
	SBPMItemQueue sPBMItemQueue;

	@Autowired
	private SBPMConfiguration sbpmConfig;
	
	private static final Logger LOGGER = LoggingWrapper.getLogger(ItemProcessAspectListener.class);

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Around("execution(* org.springframework.batch.item.ItemProcessor.*(..)) && args(item)")
	public Object aroundProcess(final ProceedingJoinPoint jp, final I item) throws Throwable {
		LOGGER.trace("ItemProcessor Around advice has been called");
		final ItemProcessor itemProcessor = (ItemProcessor) jp.getTarget();
		chronoHelper.setActiveAction(itemProcessor, 2, Thread.currentThread());

		// Start Chrono
		final Chronometer chronometer = new Chronometer();

		chronometer.startChronometer();
		// Proceed
		LOGGER.trace("ItemProcessor Around advice has sucessfully set up its environment");
		LOGGER.trace("ItemProcesser Around advice is now proceeding its joinpoint");
		final Object o = jp.proceed();
		// Stop Chrono
		chronometer.stop();
		if (!(itemProcessor instanceof CompositeItemProcessor)) {
			String itemReflection = "";
			String itemClassName = "";
			
			if(sbpmConfig.trackAnomaly()) {
				LOGGER.trace("Write ItemReflection to DB");
				itemClassName = item.getClass().getSimpleName();
				final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(item,
        				ToStringStyle.JSON_STYLE);
				itemReflection = reflectionToStringBuilder.toString();
			}
			final SBPMItem sPBMItem = new SBPMItem(
					chronoHelper.getActiveActionID(Thread.currentThread()), chronoHelper.getBatchChunkListener()
							.getSPBMChunkExecution(Thread.currentThread()).getChunkExecutionID(),
					(int) chronometer.getDuration(), 0, item.toString(), itemReflection, itemClassName);
			sPBMItemQueue.addItem(sPBMItem);
		}
		LOGGER.trace("ItemProcessor Around advice proceeded and has stopped its Chronometer");
		return o;
	}
}
