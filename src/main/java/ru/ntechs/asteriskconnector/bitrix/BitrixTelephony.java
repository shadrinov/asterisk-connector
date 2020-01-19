package ru.ntechs.asteriskconnector.bitrix;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventBind;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineAdd;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestTemplate;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;

@Slf4j
@Component
public class BitrixTelephony {
	private ConnectorConfig config;
	private RestTemplate restTemplate;

	@Autowired
	private BitrixAuth bitrixAuth;

	public BitrixTelephony(ConnectorConfig config) {
		this.config = config;

		restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ResponseErrorHandler() {

			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				log.info(String.format("RestTemplate.hasError(): %d %s", response.getStatusCode().value(), response.getStatusText()));
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				log.info(String.format("RestTemplate.handleError(): %d %s", response.getStatusCode().value(), response.getStatusText()));
			}
		});
	}

	public void installAuth(BitrixEvent be) {
		bitrixAuth.installAuth(be);
	}

	public void registerCall() {
		String uri = config.getBitrix().getApi();
//		restTemplate.getForObject(url, responseType, uriVariables);
//		restTemplate.postForObject(url, request, responseType, uriVariables);
		String result = restTemplate.getForObject(uri, String.class);

	    log.info(result);
	}

	public void getExternalLine() {
		RestRequestTemplate req;
		String msg;

		req = new RestRequestExternalLineGet(bitrixAuth);
		log.info(String.format("Executing: %s", req.getMethod()));
		msg = restTemplate.postForObject(bitrixAuth.getMethodUri(req.getMethod()), req, String.class);
		log.info(msg);

		req = new RestRequestExternalLineAdd(bitrixAuth, "679606");
		log.info(String.format("Executing: %s", req.getMethod()));
		msg = restTemplate.postForObject(bitrixAuth.getMethodUri(req.getMethod()), req, String.class);
		log.info(msg);

		req = new RestRequestExternalLineAdd(bitrixAuth, "679618");
		log.info(String.format("Executing: %s", req.getMethod()));
		msg = restTemplate.postForObject(bitrixAuth.getMethodUri(req.getMethod()), req, String.class);
		log.info(msg);
	}

	public void bindEvent(BitrixEvent be) {
		RestRequestTemplate req;
		String msg;

		req = new RestRequestEventBind(bitrixAuth);
		log.info(String.format("Executing: %s", req.getMethod()));
		msg = restTemplate.postForObject(bitrixAuth.getMethodUri(req.getMethod()), req, String.class);
		log.info(msg);
	}
}
