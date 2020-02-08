package ru.ntechs.asteriskconnector.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connector")
public class ConnectorConfig {
	private ConnectorBitrix bitrix;
	private List<ConnectorRule> rules = new ArrayList<ConnectorRule>();  // NPE happens otherwise
}
