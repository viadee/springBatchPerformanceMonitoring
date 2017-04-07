/**
 * Copyright � 2016, viadee Unternehmensberatung GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the viadee Unternehmensberatung GmbH.
 * 4. Neither the name of the viadee Unternehmensberatung GmbH nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.spring.batch.operational.monitoring.writer;

import java.util.Iterator;

import org.apache.log4j.Logger;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.operational.chronometer.Chronometer;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.persistence.SPBMItemQueue;
import de.viadee.spring.batch.persistence.types.SPBMItem;

/**
 * This class represents a decorator Pattern of the LoggingIterator provided by java. It enriches the default
 * LoggingIterator by logging time-information based on the use of the "next()" and "hasNext()" method.
 * 
 * @param <T>
 * 
 */
public class LoggingIterator<T> implements Iterator<T> {

    private final int position = 0;

    ChronoHelper chronoHelper;

    private static final Logger LOG = LoggingWrapper.getLogger(LoggingIterator.class);

    private final Iterator<T> iterator;

    private SPBMItemQueue sPBMItemQueue;

    private final String hashCode;

    private Chronometer iteratorChronometer = null;

    public void setSPBMItemQueue(final SPBMItemQueue sPBMItemQueue) {
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
            final SPBMItem sPBMItem = new SPBMItem(chronoHelper.getActiveActionID(Thread.currentThread()),
                    chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread())
                            .getChunkExecutionID(),
                    (int) iteratorChronometer.getDuration(), 0, iteratorChronometer.getObjectName());

            sPBMItemQueue.addItem(sPBMItem);
        }
        return hasNext;
    }

    @Override
    public T next() {
        if (iteratorChronometer != null) {
            iteratorChronometer.stop();
            final SPBMItem sPBMItem = new SPBMItem(chronoHelper.getActiveActionID(Thread.currentThread()),
                    chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread())
                            .getChunkExecutionID(),
                    (int) iteratorChronometer.getDuration(), 0, iteratorChronometer.getObjectName());
            sPBMItemQueue.addItem(sPBMItem);
        }
        final T next = iterator.next();
        iteratorChronometer = new Chronometer();
        iteratorChronometer.setObjectName(next.toString());
        iteratorChronometer.startChronometer();
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
