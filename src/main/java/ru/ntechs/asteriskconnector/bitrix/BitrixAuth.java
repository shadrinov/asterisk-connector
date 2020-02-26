package ru.ntechs.asteriskconnector.bitrix;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEvent;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;

@Getter
@Component
public class BitrixAuth extends Thread {
	private String applicationToken;
	private String accessToken;
	private String authToken;
	private String refreshToken;

	private Integer expires;
	private Integer expiresIn;

	private String authServer;
	private String clientServer;

	@Autowired
	private ConnectorConfig conf;

	public BitrixAuth() {
		setName("oauth-token-updater");
		setDaemon(true);

		start();
	}

	@Override
	public void run() {
		Instant.now().getEpochSecond();

		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void installAuth(BitrixEvent be) {
		this.applicationToken = be.getAuthApplicationToken();
		this.accessToken = be.getAuthAccessToken();
		this.authToken = be.getAuthAccessToken();
		this.refreshToken = be.getAuthRefreshToken();
		this.expires = be.getAuthExpires();
		this.expiresIn = be.getAuthExpiresIn();
		this.authServer = be.getAuthServerEndpoint();
		this.clientServer = be.getAuthClientEndpoint();
	}

	public String getMethodUri(String method) {
		return clientServer + method;
	}

	private void auth() {

	}
}
