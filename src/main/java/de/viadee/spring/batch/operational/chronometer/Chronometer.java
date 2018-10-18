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
package de.viadee.spring.batch.operational.chronometer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to measure the elapsed time between two points in time.
 * 
 * It also has the ability, to interconnect several TimeLogger and Chronometer objects, which is necessary for
 * outputting an hierarchical overview of the measured times onto the console.
 * 
 */
public class Chronometer {

    private boolean isRunning = false;

    private final List<TimeLogger> childTimeLogger = new ArrayList<TimeLogger>();

    /**
     * This variable is supposed to hold the name for the Object, the Chronometer is bound to.
     */
    private String objectName;
    
    private String objectReflection;

    private String objectClass;
    
    private long startTimeMillis, endTimeMillis;

    public List<TimeLogger> getChildTimeLogger() {
        return childTimeLogger;
    }

    public void addChildTimeLogger(final TimeLogger timeLogger) {
        childTimeLogger.add(timeLogger);
    }

    public boolean getIsRunning() {
        return this.isRunning;
    }

    public void startChronometer() {
        if (!isRunning) {
            this.startTimeMillis = System.currentTimeMillis();
        }

        this.isRunning = true;
    }

    public void stop() {
        if (isRunning) {
            this.endTimeMillis = System.currentTimeMillis();
        }
        this.isRunning = false;
    }

    public long getDuration() {
        return this.endTimeMillis - this.startTimeMillis;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectReflection(final String objectReflection) {
        this.objectReflection = objectReflection;
    }
    
    public String getObjectReflection() {
        return objectReflection;
    }
    
    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }
    
    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(final String objectClass) {
        this.objectClass = objectClass;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

}
