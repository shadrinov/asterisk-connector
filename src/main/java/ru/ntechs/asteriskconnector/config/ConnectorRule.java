package ru.ntechs.asteriskconnector.config;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ConnectorRule {
	private List<String> events;
	private ConnectorAction action;
	private Map<Object,Object> data;
}
