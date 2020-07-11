package ru.ntechs.asteriskconnector.config;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConnectorAction {
	private String method;
	private Map<String, String> params;
	private Map<String, String> fields;
}
