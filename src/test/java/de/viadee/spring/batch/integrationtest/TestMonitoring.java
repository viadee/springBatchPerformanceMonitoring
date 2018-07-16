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
package de.viadee.spring.batch.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.viadee.spring.batch.infrastructure.DataSourceHolder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { de.viadee.spring.batch.integrationtest.configuration.ApplicationConfiguration.class })
public class TestMonitoring {

    @Autowired
    private DataSourceHolder dsHolder;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void before() throws Exception {
        MainApplication.main(null);
        dsHolder.getDataSource().getConnection().close();

        jdbcTemplate = new JdbcTemplate(dsHolder.getDataSource());
    }

    @Test
    public void testJobAnzahl() {
        String SQL;

        /*
         * Allgemeine Prüfungen der Befüllung der Datenbank
         */

        // Prüfe, ob 4 Jobs vorhanden sind
        SQL = "select count(*) from \"PUBLIC\".\"Job\"";
        assertEquals(Integer.valueOf(4), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob 9 Steps vorhanden sind
        SQL = "select count(*) from \"PUBLIC\".\"Step\"";
        assertEquals(Integer.valueOf(9), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob 16 ChunkExecutions vorhanden sind
        SQL = "select count(*) from \"PUBLIC\".\"ChunkExecution\";";
        assertEquals(Integer.valueOf(16), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob 760 Itemverarbeitungen gemonitored wurden
        SQL = "select count(*) from \"PUBLIC\".\"Item\";";
        assertEquals(Integer.valueOf(760), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob 14 Actions gemonitored wurden
        SQL = "select count(*) from \"PUBLIC\".\"Action\";";
        assertEquals(Integer.valueOf(14), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, StepAction Tabelle
        SQL = "select count(*) from \"PUBLIC\".\"StepAction\";";
        assertEquals(Integer.valueOf(22), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob jeder Step einem Job zugeordnet ist
        SQL = "select count(*) from \"Step\" WHERE \"Step\".\"JobID\" NOT IN (select \"JobID\" from \"Job\");";
        assertEquals(Integer.valueOf(0), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob jede ChunkExecution einem Step zugeordnet ist
        SQL = "SELECT count(*) FROM \"ChunkExecution\" where \"ChunkExecution\".\"StepID\" NOT IN (SELECT \"Step\".\"StepID\" FROM \"Step\");";
        assertEquals(Integer.valueOf(0), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob jede ItemVerarbeitung einer Action zugeordnet ist
        SQL = "SELECT count(*) FROM \"Item\" WHERE \"Item\".\"ActionID\" NOT IN (SELECT \"ActionID\" FROM \"Action\");";
        assertEquals(Integer.valueOf(0), jdbcTemplate.queryForObject(SQL, Integer.class));

        // Prüfe, ob das Tasklet gemonitored wurde
        SQL = "select count(*) from \"Step\" where \"Step\".\"StepName\" LIKE '%Tasklet%';";
        assertEquals(Integer.valueOf(2), jdbcTemplate.queryForObject(SQL, Integer.class));

        /*
         * Prüfung, ob das Partitioning sauber funktioniert
         */
        // StepID des Partitioning Steps
        SQL = "select \"StepID\" from \"Step\" where \"Step\".\"StepName\" = 'partitionStep';";
        final Integer partitionStepID = jdbcTemplate.queryForObject(SQL, Integer.class);
        // JobID des PartitioningJobs
        SQL = "select \"JobID\" from \"Step\" where \"Step\".\"StepID\" = " + partitionStepID + ";";
        final Integer partitionJobID = jdbcTemplate.queryForObject(SQL, Integer.class);
        // Laufzeit des PartitionSteps
        SQL = "select \"StepTime\" from \"Step\" where \"Step\".\"StepID\" =" + partitionStepID + ";";
        final Integer partitionStepRuntime = jdbcTemplate.queryForObject(SQL, Integer.class);
        // Summe der Laufzeiten aller einzelnen Partitions
        SQL = "select sum(\"StepTime\") from \"Step\" where \"JobID\" = " + partitionJobID
                + " AND \"StepName\" != 'partitionStep';";
        final Integer paritionStepSumOfPartitionsRuntime = jdbcTemplate.queryForObject(SQL, Integer.class);
        // Anzahl der Partitions
        SQL = "select count(*) from \"Step\" where \"JobID\" = " + partitionJobID
                + " AND \"StepName\" != 'partitionStep';";
        final Integer numPartitions = jdbcTemplate.queryForObject(SQL, Integer.class);
        // Dauer der längsten Partitions
        SQL = "select max(\"StepTime\") from \"Step\" where \"JobID\" = " + partitionJobID
                + " AND \"StepName\" != 'partitionStep';";
        final Integer maxPartitionTime = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Sind 4 Partitions gelaufen?
        assertEquals(Integer.valueOf(4), numPartitions);

        // Sind die Partitionen parallel gelaufen?

        assertTrue(paritionStepSumOfPartitionsRuntime > partitionStepRuntime);

        // Ist der Partition Step korrekt gemonitored? (Länger als die längste Partition)
        assertTrue(partitionStepRuntime > maxPartitionTime);

        /*
         * Prüfung der Jobabläufe
         */

        // ID des calculateTransactionTotalJob
        SQL = "select \"JobID\" from \"Job\" where \"Job\".\"JobName\" = 'calculateTransactionTotalJob'";
        final Integer calculateTransactionTotalJobID = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Die ID des zugehörigen Steps
        SQL = "select \"StepID\" from \"Step\" where \"Step\".\"JobID\" = " + calculateTransactionTotalJobID + ";";
        final Integer calculateTransactionTotalStepID = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Die Anzahl der ChunkIterationen des Steps
        SQL = "select count(*) from \"ChunkExecution\" where \"ChunkExecution\".\"StepID\" = "
                + calculateTransactionTotalStepID + ";";
        final Integer calculateTransactionTotalChunkIterations = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Die Anzahl der Itemverarbeitungen im calculateTransactionTotalJob
        SQL = "select count(*) from \"ChunkExecution\" "
                + "JOIN \"Item\" ON \"Item\".\"ChunkExecutionID\" = \"ChunkExecution\".\"ChunkExecutionID\" "
                + "where \"ChunkExecution\".\"StepID\" = " + calculateTransactionTotalStepID + ";";
        final Integer calculateTransactionTotalItemAmount = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Die Anzahl der angefallenen ItemReads
        SQL = "select count(*) from \"ChunkExecution\" "
                + "JOIN \"Item\" ON \"Item\".\"ChunkExecutionID\" = \"ChunkExecution\".\"ChunkExecutionID\" "
                + "JOIN \"Action\" ON \"Item\".\"ActionID\" = \"Action\".\"ActionID\" "
                + "where \"ChunkExecution\".\"StepID\" = " + calculateTransactionTotalStepID
                + " AND \"Action\".\"ActionType\" = 1;";
        final Integer calculateTransactionTotalItemReads = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Die Anzahl der angefallenen ItemProcesses
        SQL = "select count(*) from \"ChunkExecution\" "
                + "JOIN \"Item\" ON \"Item\".\"ChunkExecutionID\" = \"ChunkExecution\".\"ChunkExecutionID\" "
                + "JOIN \"Action\" ON \"Item\".\"ActionID\" = \"Action\".\"ActionID\" "
                + "where \"ChunkExecution\".\"StepID\" = " + calculateTransactionTotalStepID
                + " AND \"Action\".\"ActionType\" = 2;";
        final Integer calculateTransactionTotalItemProcesses = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Die Anzahl der angefallenen ItemWrites
        SQL = "select count(*) from \"ChunkExecution\" "
                + "JOIN \"Item\" ON \"Item\".\"ChunkExecutionID\" = \"ChunkExecution\".\"ChunkExecutionID\" "
                + "JOIN \"Action\" ON \"Item\".\"ActionID\" = \"Action\".\"ActionID\" "
                + "where \"ChunkExecution\".\"StepID\" = " + calculateTransactionTotalStepID
                + " AND \"Action\".\"ActionType\" = 3;";
        final Integer calculateTransactionTotalItemWrites = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Verbindung zwischen Step und Action des calculateTransactionTotalSteps
        SQL = "select count(*) from \"StepAction\" WHERE \"StepID\" = " + calculateTransactionTotalStepID + ";";
        final Integer calculateTransactionTotalStepAction = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Prüfen, ob 4 Chunks gelaufen sind (+1 empty read)
        assertEquals(Integer.valueOf(5), calculateTransactionTotalChunkIterations);

        // Prüfen, ob 240 Itemverarbeitungen angefallen sind
        assertEquals(Integer.valueOf(240), calculateTransactionTotalItemAmount);

        // Prüfen, ob 80 Reads angefallen sind
        assertEquals(Integer.valueOf(80), calculateTransactionTotalItemReads);

        // Prüfen, ob 80 Processes angefallen sind
        assertEquals(Integer.valueOf(80), calculateTransactionTotalItemProcesses);

        // Prüfen, ob 80 Writes angefallen sind
        assertEquals(Integer.valueOf(80), calculateTransactionTotalItemWrites);

        // Prüfen, ob alle Actions in der StepAction Tabelle registriert sind
        assertEquals(Integer.valueOf(3), calculateTransactionTotalStepAction);

        /*
         * Prüfung des FormatNamesJobs - Prüft, ob CompositeItemhandling korrekt erfasst wird
         */

        // ID des formatNamesJobs
        SQL = "select \"JobID\" from \"Job\" WHERE \"JobName\" = 'formatNamesJob';";
        final Integer formatNamesJobID = jdbcTemplate.queryForObject(SQL, Integer.class);

        // ID des formatNamesSteps
        SQL = "select \"StepID\" from \"Step\" WHERE \"Step\".\"JobID\" = " + formatNamesJobID + ";";
        final Integer formatNamesStepID = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Anzahl der StepAction Verbindungen
        SQL = " select count(*) from \"StepAction\" WHERE \"StepID\" = " + formatNamesStepID + ";";
        final Integer formatNamesStepAction = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Anzahl der ChunkExecutions für den formatNamesStep
        SQL = "select count(*) from \"ChunkExecution\" WHERE \"ChunkExecution\".\"StepID\" = " + formatNamesStepID
                + ";";
        final Integer formatNamesChunkExecutions = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Anzahl der ItemVerarbeitungen für den formatNamesStep
        SQL = "select count(*) from \"Item\" "
                + "JOIN \"ChunkExecution\" ON \"ChunkExecution\".\"ChunkExecutionID\" = \"Item\".\"ChunkExecutionID\" "
                + "WHERE \"ChunkExecution\".\"StepID\" = " + formatNamesStepID + ";";
        final Integer formatNamesItemAmount = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Anzahl der ItemReads für den formatNamesStep
        SQL = "select count(*) from \"Item\" "
                + "JOIN \"ChunkExecution\" ON \"ChunkExecution\".\"ChunkExecutionID\" = \"Item\".\"ChunkExecutionID\" "
                + "JOIN \"Action\" ON \"Action\".\"ActionID\" = \"Item\".\"ActionID\" "
                + "WHERE \"ChunkExecution\".\"StepID\" = " + formatNamesStepID + " AND \"Action\".\"ActionType\" = 1;";
        final Integer formatNamesItemReads = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Anzahl der ItemProcesses für den formatNamesStep
        SQL = "select count(*) from \"Item\" "
                + "JOIN \"ChunkExecution\" ON \"ChunkExecution\".\"ChunkExecutionID\" = \"Item\".\"ChunkExecutionID\" "
                + "JOIN \"Action\" ON \"Action\".\"ActionID\" = \"Item\".\"ActionID\" "
                + "WHERE \"ChunkExecution\".\"StepID\" = " + formatNamesStepID + " AND \"Action\".\"ActionType\" = 2;";
        final Integer formatNamesItemProcesses = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Anzahl der ItemWrites für den formatNamesStep
        SQL = "select count(*) from \"Item\" "
                + "JOIN \"ChunkExecution\" ON \"ChunkExecution\".\"ChunkExecutionID\" = \"Item\".\"ChunkExecutionID\" "
                + "JOIN \"Action\" ON \"Action\".\"ActionID\" = \"Item\".\"ActionID\" "
                + "WHERE \"ChunkExecution\".\"StepID\" = " + formatNamesStepID + " AND \"Action\".\"ActionType\" = 3;";
        final Integer formatNamesItemWrites = jdbcTemplate.queryForObject(SQL, Integer.class);

        // Prüfen, ob StepAction Verbindungen bestehen
        assertEquals(Integer.valueOf(7), formatNamesStepAction);

        // Prüfen, ob die Anzahl der ChunkExecutions passt - 4 Volle + 1 EmptyRead
        assertEquals(Integer.valueOf(5), formatNamesChunkExecutions);

        // Prüfen, ob die Anzahl der ItemVerarbeitungen passt
        assertEquals(Integer.valueOf(400), formatNamesItemAmount);

        // Prüfen, ob die Anzahl der ItemReads passt
        assertEquals(Integer.valueOf(80), formatNamesItemReads);

        // Prüfen, ob die Anzahl der ItemVerarbeitungen passt
        assertEquals(Integer.valueOf(160), formatNamesItemProcesses);

        // Prüfen, ob die Anzahl der ItemWrites passt
        assertEquals(Integer.valueOf(160), formatNamesItemWrites);

    }
}
