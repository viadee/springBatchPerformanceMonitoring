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
package de.viadee.spring.batch.infrastructure;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.persistence.SPBMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.SPBMItemQueue;

/**
 * The SchedulingHolder class is used to create an isolated TaskScheduler environment, which doesn't affect / isn't
 * affected by any possible existing code which might be located in the Batch Project that is to be monitored.
 * 
 * This class takes care of triggering the DatabaseScheduledWriter on an interval basis. It also registers the spawned
 * Thread as a Daemon-Thread.
 * 
 * However, the Daemon-Thread is semi-blocking. See DatabaseScheduledWriter class for further Details.
 *
 */
public class SchedulingHolder {

    private static final Logger LOG = LoggingWrapper.getLogger(SchedulingHolder.class);

    private final ThreadPoolTaskScheduler heldTaskScheduler;

    public SchedulingHolder(final SPBMItemQueue sPBMItemQueue, final SPBMChunkExecutionQueue sPBMChunkExecutionQueue,
            final JdbcTemplateHolder jdbcTemplateHolder, final ChronoHelper chronoHelper)
                    throws IllegalStateException, InterruptedException {

        LOG.debug("SchedulingHolder instantiated");
        heldTaskScheduler = new ThreadPoolTaskScheduler();
        heldTaskScheduler.setPoolSize(1);
        heldTaskScheduler.setDaemon(true);

        heldTaskScheduler.afterPropertiesSet();
        final DatabaseScheduledWriter dbScheduledWriter = new DatabaseScheduledWriter();
        dbScheduledWriter.setSPBMItemQueue(sPBMItemQueue);
        dbScheduledWriter.setSPBMChunkExecutionQueue(sPBMChunkExecutionQueue);
        dbScheduledWriter.setJdbcTemplateHolder(jdbcTemplateHolder);
        dbScheduledWriter.setChronoHelper(chronoHelper);
        heldTaskScheduler.scheduleAtFixedRate(dbScheduledWriter, 100);
    }
}
