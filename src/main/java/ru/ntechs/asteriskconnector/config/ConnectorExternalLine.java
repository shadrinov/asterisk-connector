package ru.ntechs.asteriskconnector.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConnectorExternalLine {
	private String number;
	private String name;

	private String channel;
	private String context;
	private String exten;
	private String priority;
}
