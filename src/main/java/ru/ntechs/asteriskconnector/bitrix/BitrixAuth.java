package ru.ntechs.asteriskconnector.bitrix;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.rest.data.DynamicData;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEvent;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResult;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultAuth;
import ru.ntechs.asteriskconnector.config.ConnectorBitrix;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;

@Slf4j
@Getter
@Component
public class BitrixAuth {
	private String applicationToken;
	private String accessToken;
	private String authToken;
	private String refreshToken;

	private Integer expires;
	private Integer expiresIn;

	private String authServer;
	private String clientServer;

	private RestTemplate restTemplate;

	private ConnectorConfig conf;
	private Thread tokenUpdater;

	@Autowired
	public BitrixAuth(ConnectorConfig conf) {
		this.conf = conf;

		if (restTemplate == null) {
			restTemplate = new RestTemplate();
			restTemplate.setErrorHandler(new ResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
//					log.info("Request status: {} {}", response.getStatusCode().value(), response.getStatusText());
					return false;
				}

				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
				}
			});
		}

		load();

		tokenUpdater = new Thread() {
			@Override
			public void run() {
				go();
			}
		};

		tokenUpdater.setName("token-updater");
		tokenUpdater.setDaemon(true);
		tokenUpdater.start();
	}

	public BitrixAuth(BitrixAuth parent, BitrixEvent event) {
		this.conf = parent.conf;
		this.restTemplate = parent.restTemplate;
		this.tokenUpdater = null;

		this.applicationToken = event.getAuthApplicationToken();
		this.accessToken = event.getAuthAccessToken();
		this.authToken = event.getAuthAccessToken();
		this.refreshToken = event.getAuthRefreshToken();

		this.expires = event.getAuthExpires();
		this.expiresIn = event.getAuthExpiresIn();

		this.authServer = event.getAuthServerEndpoint();
		this.clientServer = event.getAuthClientEndpoint();
	}

	private void go() {
		Instant.now().getEpochSecond();

		while (true) {
			try {
				if (refreshToken != null) {
					auth();
					save();
				}

				if (expiresIn == null)
					Thread.sleep(5000);
				else if (expiresIn < 180) {
					log.info("too short access token expiration period ({}), using 90", expiresIn);
					Thread.sleep(90000);
				}
				else
					Thread.sleep((expiresIn - 90) * 1000);
			} catch (InterruptedException e) {
				log.info("forced token renewal");
			}
		}
	}

	public BitrixAuth clone(BitrixEvent be) {
		return new BitrixAuth(this, be);
	}

	public void afterInstall(BitrixAuth auth) {
		this.applicationToken = auth.applicationToken;
		this.authToken = auth.authToken;
		this.refreshToken = auth.refreshToken;

		this.expires = auth.expires;
		this.expiresIn = auth.expiresIn;

		this.authServer = auth.authServer;
		this.clientServer = auth.clientServer;

		save();
	}

	public String getMethodUri(String method) {
		return clientServer + method;
	}

	private void auth() {
		ConnectorBitrix cb = conf.getBitrix();

		if (cb != null) {
			if (cb.getAuth() != null) {
				try {
					if (cb.getAuth() == null)
						throw new BitrixLocalException("auth server not specified: connector.bitrix.auth");

					if (cb.getClientId() == null)
						throw new BitrixLocalException("auth server not specified: connector.bitrix.clientid");

					if (cb.getClientKey() == null)
						throw new BitrixLocalException("auth server not specified: connector.bitrix.clientkey");

					HashMap<String, String> uriVars = new HashMap<>();

					uriVars.put("refresh_token", refreshToken);
					uriVars.put("client_id", cb.getClientId());
					uriVars.put("client_secret", cb.getClientKey());

					ResponseEntity<RestResultAuth> result = restTemplate.getForEntity(
							cb.getAuth() + "?grant_type=refresh_token&refresh_token={refresh_token}&client_id={client_id}&client_secret={client_secret}",
							RestResultAuth.class, uriVars);

					RestResultAuth tokens = result.getBody();

					if ((result.getStatusCode().value() == 200) && (tokens.getError() == null) && (tokens.getErrorDescription() == null)) {
						this.accessToken = tokens.getAccessToken();
						this.refreshToken = tokens.getRefreshToken();
						this.expires = tokens.getExpires();
						this.expiresIn = tokens.getExpiresIn();
						this.authServer = tokens.getServerEndpoint();
						this.clientServer = tokens.getClientEndpoint();
					}
					else if (result.getStatusCode().value() != 200) {
						log.info(formatErrorMessage(result.getStatusCode(), result.getBody()));

						if (tokens.getError().equalsIgnoreCase("invalid_grant")) {
							log.info("refresh_token expired or invalid, application reinstallation needed");

							this.accessToken = null;
							this.refreshToken = null;
							this.expires = null;
							this.expiresIn = null;
						}
						else {
							this.expires = null;
							this.expiresIn = null;
						}
					}
					else {
						log.info(formatErrorMessage(result.getStatusCode(), result.getBody()));

						this.accessToken = null;
						this.expires = null;
						this.expiresIn = null;
					}
				} catch (BitrixLocalException e) {
					log.info(e.getMessage());
				} catch (RestClientException e) {
					log.info("Failed to update access token, possible Bitrix24 cloud servers faulure: {}", e.getMessage());
				}
			}
		}
		else
			log.info("unconfigured connector.bitrix");
	}

	protected String formatErrorMessage(HttpStatus statusCode, RestResult body) {
		return String.format("authorization failed: %d %s (%s)", statusCode.value(), statusCode.getReasonPhrase(),
				(body != null) ?
						String.format("%s%s", body.getError(), (body.getErrorDescription() != null) ? (", " + body.getErrorDescription()) : "") :
							"No error description");
	}

	private void save() {
		if (refreshToken == null)
			return;

		DynamicData data = new DynamicData(refreshToken, applicationToken, authToken, authServer, clientServer);

		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

		try {
			writer.writeValue(new File("connector.json"), data);
		} catch (IOException e) {
			log.info("unable to write data: {}", e.getMessage());
		}
	}

	private void load() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			DynamicData data = mapper.readValue(new File("connector.json"), DynamicData.class);

			this.refreshToken = data.getRefreshToken();
			this.applicationToken = data.getApplicationToken();
			this.authToken = data.getAuthToken();

			this.authServer = data.getAuthServer();
			this.clientServer = data.getClientServer();
		} catch (IOException e) {
			log.info("unable to read data: {}", e.getMessage());
		}
	}

	public boolean validateAppToken(BitrixEvent event) {
		return applicationToken.equals(event.getAuthApplicationToken());
	}

	public boolean isInstalled() {
		return ((applicationToken != null) && (refreshToken != null));
	}
}
