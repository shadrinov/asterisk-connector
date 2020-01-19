package ru.ntechs.asteriskconnector.config;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectorRule {
	private List<String> events;
	private ConnectorAction action;
	private Map<Object,Object> data;
}
