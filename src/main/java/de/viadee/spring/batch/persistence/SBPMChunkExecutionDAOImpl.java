/**
 * Copyright � 2016, viadee Unternehmensberatung GmbH
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
import java.util.List;
import java.util.Map;

import de.viadee.spring.batch.infrastructure.JdbcTemplateHolder;
import de.viadee.spring.batch.persistence.types.SBPMChunkExecution;

/**
 * DAO Object for the Action ChunkExecution. See SpbmChunkExecution Class for
 * further Details.
 * 
 */
public class SBPMChunkExecutionDAOImpl implements SBPMChunkExecutionDAO {

	private JdbcTemplateHolder jdbcTemplateHolder;

	private final String CHUNKEXECUTIONINSERTSQL = "INSERT INTO \"ChunkExecution\" (\"ChunkExecutionID\", \"StepID\", \"StepName\", \"Iteration\",\"ChunkTime\") VALUES (:chunkExecutionID,:stepID,:stepName,:iteration,:chunkTime);";

	@Override
	public void insert(final SBPMChunkExecution sPBMChunkExecution) {
		final Map<String, String> params = getParams(sPBMChunkExecution);
		jdbcTemplateHolder.getJdbcTemplate().update(CHUNKEXECUTIONINSERTSQL, params);
	}

	@Override
	public void insertBatch(final List<SBPMChunkExecution> chunkExecutionList) {
		final Map<String, String>[] parameters = new Map[chunkExecutionList.size()];
		Map<String, String> params;
		int counter = 0;
		for (final SBPMChunkExecution sPBMChunkExecution : chunkExecutionList) {
			params = getParams(sPBMChunkExecution);
			parameters[counter++] = params;
		}
		this.jdbcTemplateHolder.getJdbcTemplate().batchUpdate(CHUNKEXECUTIONINSERTSQL, parameters);
	}

	private Map<String, String> getParams(final SBPMChunkExecution sPBMChunkExecution) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("chunkExecutionID", "" + sPBMChunkExecution.getChunkExecutionID());
		params.put("stepID", "" + sPBMChunkExecution.getStepID());
		params.put("stepName", sPBMChunkExecution.getStepName());
		params.put("iteration", "" + sPBMChunkExecution.getIteration());
		params.put("chunkTime", "" + sPBMChunkExecution.getChunkTime());
		return params;
	}

	@Override
	public void setJdbcTemplateHolder(JdbcTemplateHolder jdbcTemplateHolder) {
		this.jdbcTemplateHolder = jdbcTemplateHolder;
	}
}
