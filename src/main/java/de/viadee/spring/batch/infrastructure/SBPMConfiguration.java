package de.viadee.spring.batch.infrastructure;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySources({ @PropertySource("classpath:springBatchMonitoringDefault.properties"),
		@PropertySource(value = "classpath:springBatchMonitoring.properties", ignoreResourceNotFound = true) })
public class SBPMConfiguration {

	private static final Logger LOG = LoggingWrapper.getLogger(SBPMConfiguration.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Value("${db.username}")
	private String username;

	@Value("${db.password}")
	private String password;

	@Value("${db.url}")
	private String url;

	@Value("${db.driver}")
	private String driver;

	@Value("${db.anomalydetection}")
	private String trackanomaly;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getDriver() {
		return driver;
	}

	public boolean trackAnomaly() {
		return Boolean.parseBoolean(trackanomaly);
	}

	@PostConstruct
	protected void getProperties() {
		LOG.debug("Property trackanomaly was set to: " + trackanomaly);
	}
}
