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
package de.viadee.spring.batch.persistence;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.viadee.spring.batch.infrastructure.JdbcTemplateHolder;
import de.viadee.spring.batch.infrastructure.SBPMConfiguration;
import de.viadee.spring.batch.persistence.types.SBPMJob;

/**
 * DAO Object for the Job Object. See SpbmJob Class for further Details.
 * 
 * 
 */
@Repository
public class SBPMJobDAOImpl implements SBPMJobDAO {

	@Autowired
	private JdbcTemplateHolder jdbcTemplateHolder;

	@Autowired
	private SBPMConfiguration sbpmConfig;

	private final String INSERTSQL = "INSERT INTO \"Job\" (\"JobID\",\"JobName\",\"JobStart\",\"JobEnd\",\"Duration\") VALUES (:jobID,:jobName,:jobStart,:jobEnd,:duration);";

	private final String INSERTMETASQL = "INSERT INTO \"BatchRuns\"(\"JobID\", \"StepID\", \"ActionType\", \"JobName\", \"StepName\", \"StepStart\", \"StepEnd\", \"ActionName\",  \"TotalTime\", \"ProcessedItems\", \"MeanTimePerItem\") SELECT  \"OV\".*,  (\"OV\".\"Total\"/ \"OV\".\"ProcessedItems\") AS \"MeanTimePerItem\" FROM \"Overview\" AS \"OV\" WHERE \"OV\".\"JobID\" = :jobID;";

	@Override
	public void insert(final SBPMJob job) {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("jobID", "" + job.getJobID());
		params.put("jobName", job.getJobName());
		params.put("jobStart", String.valueOf(job.getJobStart()));
		params.put("jobEnd", String.valueOf(job.getJobEnd()));
		params.put("duration", "" + job.getDuration());
		jdbcTemplateHolder.getJdbcTemplate().update(INSERTSQL, params);
		if (sbpmConfig.trackAnomaly()) {
			insertMeta(job);
		}
	}

	@Override
	public void insertMeta(final SBPMJob job) {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("jobID", "" + job.getJobID());
		jdbcTemplateHolder.getJdbcTemplate().update(INSERTMETASQL, params);
	}

}
