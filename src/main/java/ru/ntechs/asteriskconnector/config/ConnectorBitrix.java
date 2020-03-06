package ru.ntechs.asteriskconnector.config;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ConnectorBitrix {
	private String auth;
	private String clientId;
	private String clientKey;
	private ArrayList<ConnectorExternalLine> externalLines;
}
