package ru.ntechs.asteriskconnector.config;

import java.util.HashMap;

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

	private String application;
	private String data;
	private String timeout;
	private String callerId;

	private HashMap<String, String> variable;

	private String account;
	private String earlyMedia;
	private String async;
	private String codecs;
}
