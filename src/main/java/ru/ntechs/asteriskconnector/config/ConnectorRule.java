package ru.ntechs.asteriskconnector.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ConnectorRule {
	private List<ConnectorEvent> events;
	private List<ConnectorAction> action;
}
