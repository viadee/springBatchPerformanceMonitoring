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
package de.viadee.spring.batch.infrastructure;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import de.viadee.spring.batch.persistence.SBPMChunkExecutionDAOImpl;
import de.viadee.spring.batch.persistence.SBPMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.SBPMItemDAOImpl;
import de.viadee.spring.batch.persistence.SBPMItemQueue;

/**
 * TODO: Check if we even need this Doesn't seem like it at this time
 * 
 */
@EnableAsync
public class JobEndQueueCleaner {

	private final DatabaseScheduledWriter dbScheduledWriter;

	private static final Logger LOG = LoggingWrapper.getLogger(JobEndQueueCleaner.class);

	public JobEndQueueCleaner(final SBPMItemQueue sPBMItemQueue, final SBPMChunkExecutionQueue sPBMChunkExecutionQueue,
			final JdbcTemplateHolder templateHolder) {
		this.dbScheduledWriter = new DatabaseScheduledWriter();
		dbScheduledWriter.setSPBMItemDAO(new SBPMItemDAOImpl());
		dbScheduledWriter.setSPBMChunkExecutionDAO(new SBPMChunkExecutionDAOImpl());
		dbScheduledWriter.setJdbcTemplateHolder(templateHolder);
		dbScheduledWriter.setSPBMItemQueue(sPBMItemQueue);
		dbScheduledWriter.setSPBMChunkExecutionQueue(sPBMChunkExecutionQueue);
	}

	@Async
	public void asyncTest(final String jobName) throws InterruptedException {
		LOG.debug("Cleaning up for Job " + jobName);
		dbScheduledWriter.run();
		LOG.debug("Cleaned up for job " + jobName);
	}
}
