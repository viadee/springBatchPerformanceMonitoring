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
package de.viadee.spring.batch.operational.monitoring.writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.infrastructure.SBPMConfiguration;
import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.persistence.SBPMItemQueue;

/**
 * This class is used to generate a LoggingList which behaves like a common
 * List, except it replaces the ListIterator with a LoggingIterator (see
 * LoggingIterator class).
 * 
 * The LoggingList can be used like a common List.
 * 
 * @param <T>
 *            Type of the items in the monitored List
 */
public class LoggingList<T> implements List<T> {

	private static final Logger LOG = LoggingWrapper.getLogger(LoggingList.class);

	ChronoHelper chronoHelper;

	private SBPMItemQueue sPBMItemQueue;

	private final List<T> list;

	private final String hashCode;

	private SBPMConfiguration sbpmConfig;

	public LoggingList(final List<T> list, final String hashCode) {

		this.list = list;
		this.hashCode = hashCode;
	}

	public void setSPBMItemQueue(final SBPMItemQueue sPBMItemQueue) {
		this.sPBMItemQueue = sPBMItemQueue;
	}

	public void setChronoHelper(final ChronoHelper chronoHelper) {
		this.chronoHelper = chronoHelper;
	}

	public List<T> getList() {
		return list;
	}

	@Override
	public boolean add(final T e) {
		return list.add(e);
	}

	@Override
	public void add(final int index, final T element) {
		list.add(index, element);
	}

	@Override
	public boolean addAll(final Collection c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(final int index, final Collection c) {
		return list.addAll(index, c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(final Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(final Collection c) {
		return list.containsAll(c);
	}

	@Override
	public T get(final int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(final Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator iterator() {
		final LoggingIterator<T> loggingIterator = new LoggingIterator<T>(list.iterator(), hashCode);
		loggingIterator.setChronoHelper(chronoHelper);
		loggingIterator.setSPBMItemQueue(sPBMItemQueue);
		loggingIterator.setSPBMConfig(sbpmConfig);
		return loggingIterator;
	}

	@Override
	public int lastIndexOf(final Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator listIterator() {
		LOG.warn("Warning - ListIterator used. This may cause unforseen behaviour");
		return list.listIterator();
	}

	@Override
	public ListIterator listIterator(final int index) {
		return list.listIterator(index);
	}

	@Override
	public boolean remove(final Object o) {
		return list.remove(o);
	}

	@Override
	public T remove(final int index) {
		return list.remove(index);
	}

	@Override
	public boolean removeAll(final Collection c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection c) {
		return list.retainAll(c);
	}

	@Override
	public T set(final int index, final T element) {
		return list.set(index, element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List subList(final int fromIndex, final int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public Object[] toArray(final Object[] a) {
		return list.toArray(a);
	}

	public void setSPBMConfig(SBPMConfiguration sbpmConfig) {
		this.sbpmConfig = sbpmConfig;
	}
}
