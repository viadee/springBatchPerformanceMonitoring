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

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import de.viadee.spring.batch.operational.chronometer.ChronoHelper;

/**
 * The Monitoring-Tool stores its Data in an own Database. Since this needs a DataSource and a JdbcTemplate, it might
 * lead to problems when the monitored Batch Project is using Autowire on "DataSource" or "JdbcTemplate". To prevent
 * possible errors on context creation (Autowiring identifies two possible candidates for Autowiring), both the
 * DataSource and the JdbcTemplate used by the Monitoring-Tool are encapsulated into these "Holder" classes.
 * 
 * Accessing the Monitoring-Database only works by these classes.
 * 
 */
public final class JdbcTemplateHolder {

    private ChronoHelper chronoHelper;

    private final Logger LOG = LoggingWrapper.getLogger(JdbcTemplateHolder.class);

    private DataSourceHolder dataSourceHolder;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcTemplateHolder() {
        LOG.debug("JdbcTemplateHolder buildt");
    }

    public void setChronoHelper(final ChronoHelper chronoHelper) {
        this.chronoHelper = chronoHelper;
    }

    public void setDataSourceHolder(final DataSourceHolder dataSourceHolder) {
        this.dataSourceHolder = dataSourceHolder;
    }

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.namedParameterJdbcTemplate;
    }

    public void setNamedParameterJdbcTemplate() {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSourceHolder.getDataSource());
        final JdbcTemplate tempTemplate = new JdbcTemplate();
        tempTemplate.setDataSource(dataSourceHolder.getDataSource());
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        LOG.debug("Want to destroy JdbcTemplateHolder - checking, if Daemon is still active");
        if (chronoHelper.getDaemonRunning()) {
            LOG.debug("Should stop for flushing the itemqueue");

            while (chronoHelper.getDaemonRunning()) {
                LOG.debug("Stopped");
                Thread.sleep(5);
            }
            LOG.debug("Done stopping");

        } else {
            LOG.debug("No need to wait");
        }
    }
}
