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
package de.viadee.spring.batch.persistence.types;

/**
 * This is the Database representation of a Step.
 * 
 *
 */
public class SBPMStep {

	private final int stepID;

	private final int jobID;

	private final String stepName;

	private int stepTime;

	private long stepStart;

	private long stepEnd;

	public SBPMStep(final int stepID, final int jobID, final String stepName, final int stepTime) {
		super();
		this.stepID = stepID;
		this.jobID = jobID;
		this.stepName = stepName;
		this.stepTime = stepTime;
	}

	public int getStepID() {
		return stepID;
	}

	public int getJobID() {
		return jobID;
	}

	public String getStepName() {
		return stepName;
	}

	public int getStepTime() {
		return stepTime;
	}

	public void setStepTime(final int stepTime) {
		this.stepTime = stepTime;
	}

	public long getStepStart() {
		return stepStart;
	}

	public void setStepStart(long stepStart) {
		this.stepStart = stepStart;
	}

	public long getStepEnd() {
		return stepEnd;
	}

	public void setStepEnd(long stepEnd) {
		this.stepEnd = stepEnd;
	}

}
