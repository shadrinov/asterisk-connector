package ru.ntechs.asteriskconnector.bitrix;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.rest.data.TelephonyLine;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventBind;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineAdd;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineGet;
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

		restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
//		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//		interceptors.add(new LoggingRequestInterceptor());
//		restTemplate.setInterceptors(interceptors);
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

	public ArrayList<TelephonyLine> getExternalLine() throws BitrixRestApiException {
		return new RestRequestExternalLineGet(bitrixAuth).exec().getResult();
	}

	public void addExternalLine() throws BitrixRestApiException {
		new RestRequestExternalLineAdd(bitrixAuth, 679606, "Сетевые технологии").exec();
		new RestRequestExternalLineAdd(bitrixAuth, 679618, "ИнТехСнаб").exec();
	}

	public void bindEvent(String event, String handler) throws BitrixRestApiException {
		new RestRequestEventBind(bitrixAuth, event, handler).exec();
	}
}
