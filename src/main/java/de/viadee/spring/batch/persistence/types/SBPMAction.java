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
 * This is the Database representation of an action. An action is defined as either a reader, (Composite-)ItemProcessor
 * or an (Composite-) ItemWriter. The different types are distinguished by the "actionType" attribute, whereas a one
 * stands for an ItemReader, a two for an (Composite-) ItemProcessor and a three for an (Composite-) ItemWriter.
 * 
 * This is an immutable class.
 */
public class SBPMAction {

    private final int actionID;

    private final String actionName;

    private final int actionType;

    private final int actionFather;

    private final int actionTime;

    public SBPMAction(final int actionID, final String actionName, final int actionType, final int actionFather,
            final int actionTime) {
        super();
        this.actionID = actionID;
        this.actionName = actionName;
        this.actionType = actionType;
        this.actionFather = actionFather;
        this.actionTime = actionTime;
    }

    public int getActionID() {
        return actionID;
    }

    public String getActionName() {
        return actionName;
    }

    public int getActionType() {
        return actionType;
    }

    public int getActionFather() {
        return actionFather;
    }

    public int getActionTime() {
        return actionTime;
    }

}
