package ru.ntechs.asteriskconnector.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConnectorAction {
	private String type;
	private String url;
	private String method;
}
