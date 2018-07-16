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
package de.viadee.spring.batch.infrastructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * The monitoring tool stores its data in its own database. Since this requires DataSource and a JdbcTemplate, it will
 * lead to problems when the monitored batch Project is using Autowire on "DataSource" or "JdbcTemplate". To prevent
 * possible errors on context creation (Autowiring identifies two possible candidates for Autowiring), both the
 * DataSource and the JdbcTemplate are encapsulated into these "Holder" classes.
 * 
 * This class knowingly violates the dependency injection principle by instantiating dependencies - preventing
 * interference with the client project is more important.
 */
public final class DataSourceHolder {

    private static final Logger LOG = LoggingWrapper.getLogger(DataSourceHolder.class);

    private final DataSource datasource = createDataSource();

    protected String username, password, url, driver;

    DataSourceHolder() {
        LOG.debug("DataSourceHolder buildt");
    }

    public DataSource getDataSource() {
        return datasource;
    }

    // TODO: Fix the Exception Handling
    // Accessing the Files was initially using direct file access. Then it was changed to using the Resource Object.
    // The Exception handling is set to handle direct file access exceptions. It has to be changed to comply
    // with the Resource Object. WWhat you see is a HotFixed.

    /**
     * Try to read the specified SpringBatchMonitoring.properties file. If an error occurs, use fallback defaults
     */
    protected void setProperties() {
        InputStream input = null;
        try {

            final Resource resource = new ClassPathResource("SpringBatchMonitoring.properties");
            input = resource.getInputStream();
            setDataSourceProperties(input);

        } catch (final NullPointerException e) {
            LOG.warn(
                    "SpringBatchMonitoring.properties file not found. Falling back to default values (file inside JAR)!");
            LOG.warn(e);
            try {
                final Resource resource = new ClassPathResource("springBatchMonitoringDefault.properties");
                input = resource.getInputStream();
                setDataSourceProperties(input);
            } catch (final FileNotFoundException e1) {
                LOG.warn("SpringBatchMonitoringDefault.properties file not found.");
                LOG.warn(e1);
            } catch (final IOException e1) {
                LOG.warn("Opening springBatchMonitoringDefault.properties threw an IO Exception.");
                LOG.warn(e1);
            } catch (final NullPointerException e1) {
                LOG.warn(
                        "Using the Fallback Default values (springbatchmonitoringDefault.properties) threw an NullPointerException!.");
                LOG.warn(e1);
            }

        } catch (final IOException e) {
            LOG.warn(
                    "SpringBatchMonitoring.properties file is not accessible / malformed. Falling back to default values");
            LOG.warn(e);
            final Resource backupResource = new ClassPathResource("springBatchMonitoringDefault.properties");

            try {
                input = backupResource.getInputStream();
                setDataSourceProperties(input);
                input.close();
            } catch (final IOException e1) {
                LOG.warn("springBatchMonitoringDefault.properties file is not accessible / malformed. Exiting");
                LOG.warn(e1);
            }
        }
    }

    protected void setDataSourceProperties(final InputStream input) throws IOException {
        final Properties properties = new Properties();
        properties.load(input);
        username = properties.getProperty("db.username");
        password = properties.getProperty("db.password");
        driver = properties.getProperty("db.driver");
        url = properties.getProperty("db.url");
    }

    protected DataSource createDataSource() {
        setProperties();

        LOG.trace("CreateDataSource was called");
        final SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setUrl(url);
        simpleDriverDataSource.setUsername(username);
        simpleDriverDataSource.setPassword(password);
        try {
            simpleDriverDataSource.setDriverClass((Class<? extends Driver>) Class.forName(driver));
        } catch (final ClassNotFoundException e) {
            LOG.warn(e);
        }
        // Initialize DB
        final DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(simpleDriverDataSource);
        final ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("SQL/create-tables.sql"));
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        dataSourceInitializer.afterPropertiesSet();

        return simpleDriverDataSource;
    }

}
