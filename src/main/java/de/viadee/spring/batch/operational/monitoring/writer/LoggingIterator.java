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
package de.viadee.spring.batch.operational.monitoring.writer;

import java.util.Iterator;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.infrastructure.SBPMConfiguration;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.chronometer.Chronometer;
import de.viadee.spring.batch.persistence.SBPMItemQueue;
import de.viadee.spring.batch.persistence.types.SBPMItem;

/**
 * This class represents a decorator Pattern of the LoggingIterator provided by
 * java. It enriches the default LoggingIterator by logging time-information
 * based on the use of the "next()" and "hasNext()" method.
 * 
 * @param <T>
 *            Type of the items in the monitored interator
 */
public class LoggingIterator<T> implements Iterator<T> {

	private final int position = 0;

	ChronoHelper chronoHelper;

	private static final Logger LOG = LoggingWrapper.getLogger(LoggingIterator.class);

	private final Iterator<T> iterator;

	private SBPMItemQueue sPBMItemQueue;

	private SBPMConfiguration sbpmConfig;

	private final String hashCode;

	private Chronometer iteratorChronometer = null;

	public void setSPBMItemQueue(final SBPMItemQueue sPBMItemQueue) {
		this.sPBMItemQueue = sPBMItemQueue;
	}

	public void setChronoHelper(final ChronoHelper chronoHelper) {
		this.chronoHelper = chronoHelper;
	}

	public LoggingIterator(final Iterator<T> iterator, final String hashCode) {
		this.iterator = iterator;
		this.hashCode = hashCode;
	}

	@Override
	public boolean hasNext() {
		final boolean hasNext = iterator.hasNext();
		if (!hasNext) {
			iteratorChronometer.stop();

			final SBPMItem sPBMItem = new SBPMItem(chronoHelper.getActiveActionID(Thread.currentThread()),
					chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread())
							.getChunkExecutionID(),
					(int) iteratorChronometer.getDuration(), 0, iteratorChronometer.getObjectName(),
					iteratorChronometer.getObjectReflection(), iteratorChronometer.getObjectClass());

			sPBMItemQueue.addItem(sPBMItem);
		}
		return hasNext;
	}

	@Override
	public T next() {
		if (iteratorChronometer != null) {
			iteratorChronometer.stop();
			final SBPMItem sPBMItem = new SBPMItem(chronoHelper.getActiveActionID(Thread.currentThread()),
					chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread())
							.getChunkExecutionID(),
					(int) iteratorChronometer.getDuration(), 0, iteratorChronometer.getObjectName(),
					iteratorChronometer.getObjectReflection(), iteratorChronometer.getObjectClass());
			sPBMItemQueue.addItem(sPBMItem);
		}
		final T next = iterator.next();
		iteratorChronometer = new Chronometer();
		iteratorChronometer.setObjectName(next.toString());

		String itemReflection = "";
		String itemClassName = "";
		if (sbpmConfig.trackAnomaly()) {
			itemClassName = next.getClass().getSimpleName();
			final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(next,
					ToStringStyle.JSON_STYLE);
			itemReflection = reflectionToStringBuilder.toString();
		}
		iteratorChronometer.setObjectClass(itemClassName);
		iteratorChronometer.setObjectReflection(itemReflection);
		iteratorChronometer.startChronometer();
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

	public void setSPBMConfig(SBPMConfiguration sbpmConfig) {
		this.sbpmConfig = sbpmConfig;
	}
}
