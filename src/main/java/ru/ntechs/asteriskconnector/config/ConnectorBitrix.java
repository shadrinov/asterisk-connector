package ru.ntechs.asteriskconnector.config;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalLine;

@Getter
@Setter
@ToString(callSuper = true)
public class ConnectorBitrix {
	private String api;
	private String clientId;
	private String clientKey;
	private ArrayList<ExternalLine> externalLines;
}
