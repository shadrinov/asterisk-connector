package ru.ntechs.asteriskconnector.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "connector")
public class ConnectorConfig {
	private List<ConnectorEvent> rules;

	public List<ConnectorEvent> getRules() {
		return rules;
	}

	public void setRules(List<ConnectorEvent> rules) {
		this.rules = rules;
	}
}
