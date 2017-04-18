/**
 * Copyright � 2016, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.spring.batch.persistence.types;

/**
 * This is the Database representation of an Item-Based Performance-Measurement. It stores the information, how long an
 * item has been active in a particular action (Read / Process / Write).
 * 
 * IMPORTANT: This class makes use of the "toString()" Method of the processed Item in order to empower the Developer to
 * identify the particular Item that has been processed by the dataset inside the Monitoring-Database. Please do use a
 * StringBuffer rather than String concatination in the "toString()" Method of your Items in order to keep the
 * monitoring overhead minimal.
 * 
 * This is an immutable class.
 *
 */
public class SPBMItem {

    private final int actionID;

    private final int chunkExecutionID;

    private final int timeInMS;

    private final String itemName;

    private final int error;

    public SPBMItem(final int actionID, final int chunkExecutionID, final int timeInMS, final int error,
            String itemName) {
        super();
        this.actionID = actionID;
        this.chunkExecutionID = chunkExecutionID;
        this.timeInMS = timeInMS;
        this.error = error;
        if (itemName.length() >= 300) {
            itemName = itemName.substring(0, 300);
        }
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public int getActionID() {
        return actionID;
    }

    public int getChunkExecutionID() {
        return chunkExecutionID;
    }

    public int getTimeInMS() {
        return timeInMS;
    }

    public int isError() {
        return error;
    }

}
