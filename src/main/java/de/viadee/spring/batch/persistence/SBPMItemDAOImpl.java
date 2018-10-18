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
import java.util.List;
import java.util.Map;

import de.viadee.spring.batch.infrastructure.JdbcTemplateHolder;
import de.viadee.spring.batch.persistence.types.SBPMItem;

/**
 * DAO for the item object. See SpbmItem class for further details.
 * 
 */
//@Repository
public class SBPMItemDAOImpl implements SBPMItemDAO {

    
    private JdbcTemplateHolder jdbcTemplateHolder;
    
    private final String ITEMINSERTSQL = "INSERT INTO \"Item\" (\"ActionID\",\"ChunkExecutionID\",\"ItemName\", \"ItemClassName\", \"ItemReflection\", \"TimeInMS\",\"Timestamp\", \"Error\") VALUES (:actionID,:chunkExecutionID,:itemName,:className,:itemJson,:timeInMS,:timestamp,:error);";
    
    @Override
    public void insert(final SBPMItem sPBMItem) { 
        final Map<String, String> params = getParams(sPBMItem);
        jdbcTemplateHolder.getJdbcTemplate().update(ITEMINSERTSQL, params);   
    }

    @Override
    public void insertBatch(final List<SBPMItem> itemList) {
        final Map<String, String>[] parameters = new Map[itemList.size()];
        Map<String, String> params;
        int counter = 0;
        for (final SBPMItem sPBMItem : itemList) {
        	params = getParams(sPBMItem);
            parameters[counter++] = params;
        }
        this.jdbcTemplateHolder.getJdbcTemplate().batchUpdate(ITEMINSERTSQL, parameters);
    }
    
    private Map<String, String> getParams(final SBPMItem sPBMItem) {
    	final Map<String, String> params = new HashMap<String, String>();
    	params.put("actionID", "" + sPBMItem.getActionID());
        params.put("chunkExecutionID", "" + sPBMItem.getChunkExecutionID());
        params.put("itemName", "" + sPBMItem.getItemName());
    	params.put("className", sPBMItem.getItemClass());
    	params.put("itemJson", "" + sPBMItem.getItemReflection()); 
        params.put("timeInMS", "" + sPBMItem.getTimeInMS());
        params.put("timestamp", "" + sPBMItem.getTimestamp());
        params.put("error", "" + sPBMItem.isError());
        return params;
    }

	@Override
	public void setJdbcTemplateHolder(JdbcTemplateHolder jdbcTemplateHolder) {
		this.jdbcTemplateHolder = jdbcTemplateHolder;		
	}
    
}
