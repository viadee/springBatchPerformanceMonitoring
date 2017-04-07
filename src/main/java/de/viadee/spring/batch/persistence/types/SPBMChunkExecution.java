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
package de.viadee.spring.batch.persistence.types;

/**
 * This is the Database representation of a ChunkExecution. Each ChunkExecution creates an own dataset inside the
 * Database.
 * 
 * Example Scenario: A Step processing 40 Items having a Chunksize of 30 Items.
 * 
 * In this Scenario, the Monitoring-Tool will create two separate ChunkExecution Elements for the particular Step.
 * 
 *
 * 
 */
public class SPBMChunkExecution {

    private int chunkExecutionID;

    private int stepID;

    private String stepName;

    private int iteration;

    private int chunkTime;

    public SPBMChunkExecution(final int chunkExecutionID, final int stepID, final String stepName, final int iteration,
            final int chunkTime) {
        super();
        this.chunkExecutionID = chunkExecutionID;
        this.stepID = stepID;
        this.stepName = stepName;
        this.iteration = iteration;
        this.chunkTime = chunkTime;
    }

    public int getChunkExecutionID() {
        return chunkExecutionID;
    }

    public void setChunkExecutionID(final int chunkExecutionID) {
        this.chunkExecutionID = chunkExecutionID;
    }

    public int getStepID() {
        return stepID;
    }

    public void setStepID(final int stepID) {
        this.stepID = stepID;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setStepName(final String stepName) {
        this.stepName = stepName;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(final int iteration) {
        this.iteration = iteration;
    }

    public int getChunkTime() {
        return chunkTime;
    }

    public void setChunkTime(final int chunkTime) {
        this.chunkTime = chunkTime;
    }

}
