package ru.ntechs.asteriskconnector.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConnectorAmi {
	private String hostname;
	private Integer port;
	private String username;
	private String password;
	private boolean debug;
}
