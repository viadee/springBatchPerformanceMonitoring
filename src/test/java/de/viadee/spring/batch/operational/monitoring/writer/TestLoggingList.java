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
/**
 * 
 */
package de.viadee.spring.batch.operational.monitoring.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.chronometer.TimeLogger;
import de.viadee.spring.batch.operational.monitoring.BatchChunkListener;
import de.viadee.spring.batch.operational.monitoring.writer.LoggingList;
import de.viadee.spring.batch.persistence.SBPMItemDAO;
import de.viadee.spring.batch.persistence.types.SBPMChunkExecution;
import de.viadee.spring.batch.persistence.types.SBPMItem;

public class TestLoggingList {

    @Autowired
    SBPMItemDAO sPBMItemDAO;

    @Test
    public void listSizeTest() {

        // given
        final List<String> basicList = new ArrayList<String>();
        basicList.add("foo");
        basicList.add("bar");
        final LoggingList<String> list = new LoggingList<String>(basicList, "Bezeichnung");

        // when
        for (final String string : list.getList()) { // do nothing with all the strings
            System.out.println(string.toString());
            assertNotNull(string);
        }

        // then
        // Time should be logged
        assertEquals("Size ist not wrapped correctly", 2, list.size());

    }

    @Test
    public void basicAddDeleteTest() {

        // Given
        // A List which is wrapped inside a LoggingList
        final List<String> basicList = new ArrayList<String>();
        final LoggingList<String> list = new LoggingList<String>(basicList, "Bezeichnung");

        // When
        // Two Objects are added and the last one is removed
        list.add("foobar");
        list.add("barfoo");
        list.remove(list.size() - 1);

        // Then
        // Assume the List size is is one
        assertEquals("The basic Operations are not mapped correctly", list.size(), 1);
    }

    @Test
    public void truncateListTest() {

        // Given
        // A filled List which is wrapped inside a LoggingList
        final List<String> basicList = new ArrayList<String>();
        final LoggingList<String> list = new LoggingList<String>(basicList, "Bezeichnung");
        list.add("foo");
        list.add("bar");

        // When
        // The list is cleared
        list.clear();

        // Then
        // Assume the List is empty (to check isEmpty(), size is also checked)
        assertEquals("isEmpty() is not wrapped correctly", list.isEmpty(), true);
        assertEquals("The list is not empty", list.size(), 0);
    }

    @Test
    public void containingTest() {
        // Given
        // A List wrapped in a LoggingList containing multiple Items
        final List<String> basicList = new ArrayList<String>();
        final LoggingList<String> list = new LoggingList<String>(basicList, "Bezeichnung");
        list.add("foo");
        list.add("bar");

        // When
        // List containing foo

        // Then
        // Assume, foo is present in the list
        assertEquals("Contains is not wrapped correctly", list.contains("foo"), true);

    }
    // Iteration is timelogged

    @Ignore
    @Test
    public void timeLoggingTest() {
        // Given
        // A Mocked TempHelper and a LoggingList

        final TimeLogger timeLogger = new TimeLogger();
        final ChronoHelper chronoHelper = Mockito.mock(ChronoHelper.class);
        final BatchChunkListener batchChunkListener = Mockito.mock(BatchChunkListener.class);
        Mockito.when(chronoHelper.getBatchChunkListener()).thenReturn(batchChunkListener);
        Mockito.when(batchChunkListener.getWriter()).thenReturn(timeLogger);
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.setChronoHelper(chronoHelper);
        final SBPMItemDAO sPBMItemDAO = Mockito.mock(SBPMItemDAO.class);
        // Mockito.when(sPBMItemDAO.insert(null)).thenReturn(true);
        // TODO: Nächste zeile ist auskommentiert um compilen zu können
        // loggingList.setSPBMItemDAO(sPBMItemDAO);
        Mockito.when(chronoHelper.getActiveActionID()).thenReturn(0);
        final SBPMChunkExecution sPBMChunkExecution = Mockito.mock(SBPMChunkExecution.class);
        Mockito.when(sPBMChunkExecution.getChunkExecutionID()).thenReturn(1);
        Mockito.when(batchChunkListener.getSPBMChunkExecution(Thread.currentThread())).thenReturn(sPBMChunkExecution);
        Mockito.when(
                chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread()).getChunkExecutionID())
                .thenReturn(0);
        loggingList.add("fooTimeLog");
        loggingList.add("barTimeLog");

        // When
        // Iterating the logginglist
        final Iterator iterator = loggingList.iterator();

        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // Then
        // Assume there are Time Measure Objects
        System.out.println(timeLogger.getChildChronometerListSize());
        Mockito.verify(sPBMItemDAO, Mockito.times(2)).insert(Mockito.any(SBPMItem.class));
    }

    // MultipleIterationIsLoggedAdequately
    @Ignore
    @Test
    public void multipleIterationLoggingTest() {
        // Given
        // A Mocked TempHelper and a LoggingList

        final TimeLogger timeLogger = new TimeLogger();
        final ChronoHelper chronoHelper = Mockito.mock(ChronoHelper.class);
        final BatchChunkListener batchChunkListener = Mockito.mock(BatchChunkListener.class);
        Mockito.when(chronoHelper.getBatchChunkListener()).thenReturn(batchChunkListener);
        Mockito.when(batchChunkListener.getWriter()).thenReturn(timeLogger);
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.setChronoHelper(chronoHelper);
        final SBPMItemDAO sPBMItemDAO = Mockito.mock(SBPMItemDAO.class);
        // Mockito.when(sPBMItemDAO.insert(null)).thenReturn(true);
        // TODO: Nächste zeile ist auskommentiert um compilen zu können
        // loggingList.setSPBMItemDAO(sPBMItemDAO);
        Mockito.when(chronoHelper.getActiveActionID()).thenReturn(0);
        final SBPMChunkExecution sPBMChunkExecution = Mockito.mock(SBPMChunkExecution.class);
        Mockito.when(sPBMChunkExecution.getChunkExecutionID()).thenReturn(1);
        Mockito.when(batchChunkListener.getSPBMChunkExecution(Thread.currentThread())).thenReturn(sPBMChunkExecution);
        Mockito.when(
                chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread()).getChunkExecutionID())
                .thenReturn(0);
        loggingList.add("fooTimeLog");
        loggingList.add("barTimeLog");

        // When
        // Double iterating the logginglist
        Iterator iterator = loggingList.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        iterator = loggingList.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // Then
        // Assume there are Time Measure Objects
        System.out.println(timeLogger.getChildChronometerListSize());
        Mockito.verify(sPBMItemDAO, Mockito.times(4)).insert(Mockito.any(SBPMItem.class));

    }

    // MissingIterationIsNotLogged
    @Test
    public void missingIterationTest() {
        // Given
        // A Mocked TempHelper and a LoggingList

        final TimeLogger timeLogger = new TimeLogger();
        final ChronoHelper chronoHelper = Mockito.mock(ChronoHelper.class);
        final BatchChunkListener batchChunkListener = Mockito.mock(BatchChunkListener.class);
        Mockito.when(chronoHelper.getBatchChunkListener()).thenReturn(batchChunkListener);
        Mockito.when(batchChunkListener.getWriter()).thenReturn(timeLogger);
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.setChronoHelper(chronoHelper);
        loggingList.add("fooTimeLogNoLoggingIterator");
        loggingList.add("barTimeLogNoLoggingIterator");

        // When
        // Double iterating the logginglist
        final Iterator<?> iterator = loggingList.iterator();
        for (int i = 0; i < loggingList.size(); i++) {
            System.out.println(loggingList.get(i));
        }

        // Then
        // Assume there are Time Measure Objects
        System.out.println(timeLogger.getChildChronometerListSize());
        assertEquals("Iterating without help of the LogginIterator did create Chronometer objects",
                timeLogger.getChildChronometerListSize(), 0);
    }

    @Test
    public void addAtPositionTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");

        // When
        // A specific item is pushed at position 1
        loggingList.add(1, "foobar");

        // Then
        // Assume the specified item on the specified position
        assertEquals("Expected item is not at the expected position", loggingList.get(1), "foobar");
    }

    @Test
    public void addCollectionAtPositionTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        final List<String> list = new ArrayList<String>();
        list.add("foo1");
        list.add("bar1");
        loggingList.add("foo2");
        loggingList.add("bar2");

        // When
        // "list" is pushed into a specific position of the logginglist
        loggingList.addAll(1, list);

        // Then
        // Assume the specified item on the specified position
        assertEquals("Expected item is not at the expected position", loggingList.get(2), "bar1");
    }

    @Test
    public void addCollectionTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        final List<String> list = new ArrayList<String>();
        list.add("foo1");
        list.add("bar1");
        loggingList.add("foo2");
        loggingList.add("bar2");

        // When
        // "list" is pushed into a specific position of the logginglist
        loggingList.addAll(list);

        // Then
        // Assume the specified item on the specified position
        assertEquals("LoggingList is does not have the expected size", loggingList.size(), 4);
    }

    @Test
    public void setTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");

        // When
        // A specific position is overwritten
        loggingList.set(1, "foobar");

        // Then
        // Assume the specified item on the specified position
        assertEquals("Expected item is not at the expected position", loggingList.get(1), "foobar");
    }

    @Test
    public void subListTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        for (int i = 0; i < 20; i++) {
            loggingList.add("foobar" + i);
        }

        // When
        // Getting a sublist of the LoggingList
        final List<String> list = loggingList.subList(0, 9);
        System.out.println("#" + list.size());

        // Then
        // Assume the specified item on the specified position
        assertEquals("Expected item is not at the expected position", list.get(8), "foobar8");
        assertEquals("The sublist does not have the expected size of 9", list.size(), 9);
    }

    @Test
    public void containsAllTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        for (int i = 0; i < 20; i++) {
            loggingList.add("foobar" + i);
        }

        // When
        // Creating a second list to check the existence of every element inside the logginglist
        final List<String> list = new ArrayList<String>();
        list.add("foobar2");
        list.add("foobar13");
        list.add("foobar9");

        // Then
        // Assume all Items of list are inside of loggingList
        assertEquals("ContainsAll method is not wrapped properly", loggingList.containsAll(list), true);

    }

    @Test
    public void indexOfTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        for (int i = 0; i < 20; i++) {
            loggingList.add("foobar" + i);
        }

        // When
        // Getting the index of foobar8
        final int indexOfFoobar8 = loggingList.indexOf("foobar8");

        // Then
        // Assume that the index of "foobar8" is 8
        assertEquals("Element is not at its expected position", indexOfFoobar8, 8);

    }

    @Test
    public void lastIndexOfTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");
        loggingList.add("foo");
        loggingList.add("bar");

        // When
        // Getting the index of foobar8
        final int lastIndexOfFoo = loggingList.lastIndexOf("foo");

        // Then
        // Assume that the index of "foobar8" is 8
        assertEquals("Element is not at its expected position", lastIndexOfFoo, 2);

    }

    @Test
    public void removeTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");

        // When
        // Removing "foo"
        loggingList.remove("foo");

        // Then
        // Assume that "foo" is no more present
        assertEquals("Item that should have been removed is still present", loggingList.contains("foo"), false);

    }

    @Test
    public void removeAllTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");
        loggingList.add("foobar");
        final List<String> list = new ArrayList<String>();
        list.add("foo");
        list.add("bar");

        // When
        // Removing list from loggingList
        loggingList.removeAll(list);

        // Then
        // Assume that loggingList.size is 1
        assertEquals("Items that should have been removed is still present", loggingList.size(), 1);

    }

    @Test
    public void retainAllTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");
        loggingList.add("foobar");
        final List<String> list = new ArrayList<String>();
        list.add("foo");
        list.add("bar");

        // When
        // Removing from loggingList what isnt contained in list
        loggingList.retainAll(list);

        // Then
        // Assume that "foobar" is deleted
        assertEquals("Items that should have been removed is still present", loggingList.contains("foobar"), false);

    }

    @Test
    @Ignore
    public void listIteratorTimeLoggingTest() {
        // Given
        // A Mocked TempHelper and a LoggingList

        final TimeLogger timeLogger = new TimeLogger();
        final ChronoHelper chronoHelper = Mockito.mock(ChronoHelper.class);
        final BatchChunkListener batchChunkListener = Mockito.mock(BatchChunkListener.class);
        Mockito.when(chronoHelper.getBatchChunkListener()).thenReturn(batchChunkListener);
        Mockito.when(batchChunkListener.getWriter()).thenReturn(timeLogger);
        Mockito.when(chronoHelper.getActiveActionID()).thenReturn(0);
        Mockito.when(
                chronoHelper.getBatchChunkListener().getSPBMChunkExecution(Thread.currentThread()).getChunkExecutionID())
                .thenReturn(0);

        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.setChronoHelper(chronoHelper);
        loggingList.add("fooTimeLog");
        loggingList.add("barTimeLog");

        // When
        // Iterating the logginglist
        final Iterator iterator = loggingList.listIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // Then
        // Assume there are Time Measure Objects
        System.out.println(timeLogger.getChildChronometerListSize());
        assertEquals("Iterating the LoggingIterator did not create a proper amount of Chronometer objects",
                timeLogger.getChildChronometerListSize(), 2);
    }

    @Test
    @Ignore
    public void listIteratorPositionTimeLoggingTest() {
        // Given
        // A Mocked TempHelper and a LoggingList

        final TimeLogger timeLogger = new TimeLogger();
        final ChronoHelper chronoHelper = Mockito.mock(ChronoHelper.class);
        final BatchChunkListener batchChunkListener = Mockito.mock(BatchChunkListener.class);
        Mockito.when(chronoHelper.getBatchChunkListener()).thenReturn(batchChunkListener);
        Mockito.when(batchChunkListener.getWriter()).thenReturn(timeLogger);
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.setChronoHelper(chronoHelper);
        loggingList.add("fooTimeLog");
        loggingList.add("barTimeLog");

        // When
        // Iterating the logginglist
        final Iterator iterator = loggingList.listIterator(1);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // Then
        // Assume there are Time Measure Objects
        System.out.println(timeLogger.getChildChronometerListSize());
        assertEquals("Iterating the LoggingIterator did not create a proper amount of Chronometer objects",
                timeLogger.getChildChronometerListSize(), 1);
    }

    @Test
    public void toArrayTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");
        loggingList.add("foobar");

        // When
        // Removing from loggingList what isnt contained in list
        final Object[] toArray = loggingList.toArray();

        // Then
        // Assume that "foobar" is deleted
        assertEquals("List converted to array does not have the assumed size", toArray.length, 3);

    }

    @Test
    public void toArrayParameterTest() {
        // Given
        // IteratorList containing at least one item
        final LoggingList<String> loggingList = new LoggingList<String>(new ArrayList<String>(), "Bezeichnung");
        loggingList.add("foo");
        loggingList.add("bar");
        loggingList.add("foobar");
        final String[] parameterArray = new String[] { "bar", "foo", "foobar", "falseItem1", "falseItem2",
                "falseItem3" };

        // When
        // Removing from loggingList what isnt contained in list
        final Object[] toArray = loggingList.toArray(parameterArray);

        // Then
        // Assume that "foobar" is deleted
        assertEquals("List converted to array does not have the assumed size", toArray[loggingList.size()], null);

    }
}
