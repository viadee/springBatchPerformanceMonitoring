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

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.viadee.spring.batch.operational.chronometer.ChronoHelper;
import de.viadee.spring.batch.operational.setupverification.AspectTestClass;
import de.viadee.spring.batch.operational.setupverification.ContainerTest;
import de.viadee.spring.batch.operational.setupverification.TestAspect;
import de.viadee.spring.batch.persistence.SBPMChunkExecutionQueue;
import de.viadee.spring.batch.persistence.SBPMItemQueue;

/**
 * 
 * Main Configuration class of the Monitoring Tool. This class sets up the
 * Environment which the Monitoring Tool needs to be operational. This includes:
 * Package based Component scanning (excluding the SetupVerification package),
 * enabling AspectJAutoProxy, creating necessary Beans for DB Access...
 * 
 */
@Configuration
@ComponentScan(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = { ContainerTest.class,
		AspectTestClass.class, TestAspect.class }), basePackages = { "de.viadee.spring.batch" })
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Configurator {

	@Autowired
	private SBPMItemQueue spbmItemQueue;

	@Autowired
	private SBPMChunkExecutionQueue spbmChunkExecutionQueue;

	@Autowired
	private SBPMConfiguration sbpmConfig;

	@Autowired
	private ChronoHelper chronoHelper;

	@Bean
	public JdbcTemplateHolder getJdbcTemplateHolder() throws SQLException {
		final JdbcTemplateHolder jdbcTemplateHolder = new JdbcTemplateHolder();
		jdbcTemplateHolder.setDataSourceHolder(getDataSourceHolder());
		jdbcTemplateHolder.setNamedParameterJdbcTemplate();
		jdbcTemplateHolder.setChronoHelper(chronoHelper);
		return jdbcTemplateHolder;
	}

	@Bean
	public SchedulingHolder schedulingHolder() throws SQLException, IllegalStateException, InterruptedException {
		final SchedulingHolder schedulingHolder = new SchedulingHolder(spbmItemQueue, spbmChunkExecutionQueue,
				getJdbcTemplateHolder(), chronoHelper);
		return schedulingHolder;
	}

	@Bean
	public JobEndQueueCleaner jobEndQueueCleaner() throws SQLException {
		final JobEndQueueCleaner jobEndQueueCleaner = new JobEndQueueCleaner(spbmItemQueue, spbmChunkExecutionQueue,
				getJdbcTemplateHolder());
		return jobEndQueueCleaner;
	}

	private static final Logger LOGGER = LoggingWrapper.getLogger(Configurator.class);

	/**
	 * Note: You MUST see this message. Otherwise the In-Memory-Monitoring-DB may be
	 * killed before completely syncing with the File-Based one (which leads to Loss
	 * of monitoring data).
	 */
	@PreDestroy
	public void stopNotifier() {
		LOGGER.info("Spring Batch Monitoring Tool has sucsessfully been unloaded.");
	}

	@PostConstruct
	public void notifyInitializaion() {
		LOGGER.info("Spring batch Monitoring Tool has been successfully loaded.");
	}

	@Bean
	public DataSourceHolder getDataSourceHolder() {
		return new DataSourceHolder(sbpmConfig);
	}

}