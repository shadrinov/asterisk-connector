package ru.ntechs.asteriskconnector.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "connector")
public class ConnectorConfig {
	private String address;
	private ConnectorBitrix bitrix;
	private List<ConnectorRule> rules = new ArrayList<>();  // NPE happens otherwise
}
