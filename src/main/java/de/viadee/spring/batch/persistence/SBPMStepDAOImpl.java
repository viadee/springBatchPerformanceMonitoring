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
import de.viadee.spring.batch.persistence.types.SBPMStep;

/**
 * DAO Object for the Step Object. See SpbmStep Class for further Details.
 * 
 *
 */
@Repository
public class SBPMStepDAOImpl implements SBPMStepDAO {

    @Autowired
    private JdbcTemplateHolder jdbcTemplateHolder;

    private final String INSERTSQL = "INSERT INTO \"Step\" (\"StepID\",\"JobID\",\"StepName\",\"StepStart\",\"StepEnd\",\"StepTime\") VALUES (:stepID, :jobID, :stepName, :stepStart, :stepEnd, :stepTime);";

    @Override
    public void insert(final SBPMStep sPBMStep) {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("stepID", "" + sPBMStep.getStepID());
        params.put("jobID", "" + sPBMStep.getJobID());
        params.put("stepName", sPBMStep.getStepName());
		params.put("stepStart", String.valueOf(sPBMStep.getStepStart()));
		params.put("stepEnd", String.valueOf(sPBMStep.getStepEnd()));
        params.put("stepTime", "" + sPBMStep.getStepTime());
        jdbcTemplateHolder.getJdbcTemplate().update(INSERTSQL, params);
    }
}
