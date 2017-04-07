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
package de.viadee.spring.batch.operational.monitoring;

import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.monitoring.writer.LoggingList;
import de.viadee.spring.batch.persistence.SPBMItemQueue;

/**
 * This class uses SpringAOP to measure any ItemWriter on Item-Level. Since an ItemWriter accepts a List of Items, the
 * ".write(List<T> items)" will only be called once for each chunk. To measure each item separately, we make use of the
 * LoggingList to be able to gather performance information for each item. See LoggingList class for further
 * information.
 * 
 */
@Aspect
@Component
public class ItemWriteAspectListener {

    @Autowired
    private ChronoHelper chronoHelper;

    @Autowired
    private SPBMItemQueue sPBMItemQueue;

    /**
     * Variable to hold the number of the current writer (since we cannot get its object name)
     */
    int currentWriterNumber;

    private static final Logger LOGGER = LoggingWrapper.getLogger(ItemWriteAspectListener.class);

    /**
     * This method wraps a list passed to an itemWriter into a LoggingList which contains an individual implementation
     * for the iterator so that item based logging inside the writer is possible.
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Around("execution(* org.springframework.batch.item.ItemWriter.write(..)) && args(items)")
    public void getItemPerformance(final ProceedingJoinPoint jp, final List items) throws Throwable {
        LOGGER.trace("ItemWriter Around advice has been called");
        final ItemWriter itemWriter = (ItemWriter) jp.getTarget();
        final String callingObjectName = (itemWriter.toString().split("\\.")[(itemWriter.toString().split("\\.").length)
                - 1]).split("@")[0].split("@")[0];
        chronoHelper.setActiveAction(itemWriter, 3, Thread.currentThread());
        // "Items" als parameter sind die eigentichen Nutzdaten. Diese werden hier in unsere eigene Liste überführt.
        // Unsere Liste hat Die Logik für die Stopwatch.

        chronoHelper.getBatchChunkListener().getWriter().getOwnChronometer().setObjectName(callingObjectName);

        final LoggingList list = new LoggingList(items, "Writer" + itemWriter.hashCode());
        list.setChronoHelper(chronoHelper);
        list.setSPBMItemQueue(sPBMItemQueue);

        if (chronoHelper.getBatchChunkListener().getProcessor().getOwnChronometer().getIsRunning()) {
            chronoHelper.getBatchChunkListener().getProcessor().getOwnChronometer().stop();
        }

        final Object[] params = new Object[1];
        if (itemWriter instanceof CompositeItemWriter) {
            params[0] = items;
        } else {
            params[0] = list;
        }

        LOGGER.trace("ItemWriter Around advice has sucessfully set up its environment");
        LOGGER.trace("ItemWriter Around advice is not proceeding its joinpoint");

        chronoHelper.getBatchChunkListener().getWriter().getOwnChronometer().startChronometer();

        jp.proceed(params);
        LOGGER.trace("ItemWriter Around advice proceeded and has stopped its Chronometer");
    }
}