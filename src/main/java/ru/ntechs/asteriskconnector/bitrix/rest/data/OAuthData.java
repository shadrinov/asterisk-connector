package ru.ntechs.asteriskconnector.bitrix.rest.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OAuthData {
	private String refreshToken;
	private String applicationToken;
	private String authToken;

	private String authServer;
	private String clientServer;

	public OAuthData() {
		super();
	}

	public OAuthData(String refreshToken, String applicationToken, String authToken, String authServer, String clientServer) {
		this.refreshToken = refreshToken;
		this.applicationToken = applicationToken;
		this.authToken = authToken;

		this.authServer = authServer;
		this.clientServer = clientServer;
	}
}
