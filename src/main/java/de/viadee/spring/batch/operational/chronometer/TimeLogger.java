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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;

/**
 * This class is used to manage Chronometer object belonging to Spring Batch
 * Domain-Objects such as Jobs, Steps, Chunk-Executions ...
 * 
 */
public class TimeLogger {

	private static Map<Integer, TimeLogger> allTimeLoggers = new HashMap<Integer, TimeLogger>();

	private ChronometerType type;

	private static final String OFFSETDELIMITER = "----|";

	private static final Logger LOGGER = LoggingWrapper.getLogger(TimeLogger.class);

	private final List<TimeLogger> childTimeLogger = new ArrayList<TimeLogger>();

	private Chronometer parent;

	/**
	 * This list is usually only used for the item-level in Reader / Processor /
	 * Writer
	 */
	private final List<Chronometer> childChronometer = new ArrayList<Chronometer>();

	/**
	 * This particular Chronometer measures the Runtime of the Obejct, this
	 * TimeLogger exists for.
	 */
	private final Chronometer ownChronometer = new Chronometer();

	/**
	 * Holding reference to the last created Chronometer object. This is needed
	 * for the LoggingIterator to stop the Chronometer object of the last Item
	 * in a LoggingList.
	 */
	private Chronometer lastCreatedChronometer;

	private String name;

	public TimeLogger(final ChronometerType type) {
		this.type = type;
	}

	public TimeLogger() {
		setType(ChronometerType.UNDEF);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<Chronometer> getChildChronometer() {
		return this.childChronometer;
	}

	// Might be used in the future for CompositeItemHandling
	public void addChildChronometer(final Chronometer chronometer) {
		this.lastCreatedChronometer = chronometer;
		this.childChronometer.add(chronometer);
	}

	// Might be used in the future for CompositeItemHandling
	public Chronometer getParent() {
		return this.parent;
	}

	public Chronometer getOwnChronometer() {
		return ownChronometer;
	}

	public void setParent(final Chronometer parent) {
		this.parent = parent;
		parent.addChildTimeLogger(this);
	}

	/**
	 * Returns the TimeLogger Object for a given Object o. If no TimeLogger
	 * exists for o, a new one will be created.
	 * 
	 * @param o
	 *            Object for which the TimeLogger will be returned
	 * @return a {@link TimeLogger}
	 */
	public static TimeLogger getTimeLoggerFor(final Object o) {
		final int hashCode = o.hashCode();
		if (allTimeLoggers.containsKey(hashCode)) {
			return allTimeLoggers.get(hashCode);
		} else {
			final TimeLogger timeLogger = new TimeLogger();
			timeLogger.setType(ChronometerType.UNDEF);
			allTimeLoggers.put(o.hashCode(), timeLogger);
			return timeLogger;
		}
	}

	public ChronometerType getType() {
		return type;
	}

	public void setType(final ChronometerType type) {
		this.type = type;
	}

	public Chronometer getLastCreatedChronometer() {
		return lastCreatedChronometer;
	}

	public void addChildTimeLogger(final TimeLogger timeLogger) {
		childTimeLogger.add(timeLogger);
	}

	public List<TimeLogger> getChildTimeLogger() {
		return childTimeLogger;
	}

	public int getChildChronometerListSize() {
		return childChronometer.size();
	}

	/**
	 * Helper method to create a Chronometer object which can't be named by the
	 * time of its creation. Used by the Reader Aspect. Before an item is read,
	 * the name of the Chronometer cant be set but a Chronometer must exist in
	 * order to measure the time. No longer in use
	 * 
	 * @return a {@link Chronometer}
	 */
	public Chronometer createUnspecifiedChronometer() {
		final Chronometer chronometer = new Chronometer();
		chronometer.setObjectName("Unspecified");
		addChildChronometer(chronometer);
		return chronometer;
	}

	/**
	 * Recursively Prints information of all Chronometer and TimeLogger objects
	 * that are connected to this TimeLogger object.
	 * 
	 * @param offset
	 *            Offset-String for the Log
	 */
	public void printWholeList(final String offset) {
		LOGGER.trace("Printing whole list");
		LOGGER.info(offset + this.getName());
		LOGGER.info(offset + this.getOwnChronometer().getObjectName() + " " + this.getOwnChronometer().getDuration()
				+ " ms");

		for (final TimeLogger timeLogger : this.ownChronometer.getChildTimeLogger()) {
			timeLogger.printWholeList(offset + OFFSETDELIMITER);
		}
		for (final Chronometer chronometer : this.getChildChronometer()) {
			LOGGER.info(offset + OFFSETDELIMITER + chronometer.getObjectName() + " \t \t" + chronometer.getDuration()
					+ " ms (Start: " + chronometer.getStartTimeMillis() + " - End: " + chronometer.getEndTimeMillis()
					+ ")");
		}
	}

}
