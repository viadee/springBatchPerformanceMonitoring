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

import org.joda.time.Instant;

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
public class SBPMItem {

    private final int actionID;

    private final int chunkExecutionID;

    private final int timeInMS;

    private final String itemName;

    private final String itemReflection;
    
    private final String itemClass;
    
    private final long timestamp;
    
    private final int error;
    
    public SBPMItem(final int actionID, final int chunkExecutionID, final int timeInMS, final int error,
            String itemName, String itemReflection, String itemClass) {
        super();
        this.actionID = actionID;
        this.chunkExecutionID = chunkExecutionID;
        this.timeInMS = timeInMS;
        this.timestamp = Instant.now().getMillis();
        this.error = error;
        if (itemName.length() >= 1000) {
            itemName = itemName.substring(0, 1000);
        }
        this.itemName = itemName;
        this.itemReflection = itemReflection;
        this.itemClass = itemClass;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemClass() {
        return itemClass;
    }
    
    public String getItemReflection() {
        return itemReflection;
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

    public long getTimestamp() {
        return timestamp;
    }
    
    public int isError() {
        return error;
    }

}
