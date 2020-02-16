package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResult;

@Slf4j
@Getter
@Setter
@ToString
public abstract class RestRequestTemplate {
	@JsonProperty("access_token")
	private String accessToken;

	@JsonIgnore
	private BitrixAuth auth;

	@JsonIgnore
	protected static RestTemplate restTemplate;

	public RestRequestTemplate(BitrixAuth auth) {
		this.auth = auth;
		this.accessToken = auth.getAccessToken();

		if (restTemplate == null) {
			restTemplate = new RestTemplate();
			restTemplate.setErrorHandler(new ResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
//					log.info(String.format("RestTemplate.hasError(): %d %s", response.getStatusCode().value(), response.getStatusText()));
					return false;
				}

				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
//					log.info(String.format("RestTemplate.handleError(): %d %s", response.getStatusCode().value(), response.getStatusText()));
				}
			});

//			restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
//			List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//			interceptors.add(new LoggingRequestInterceptor());
//			restTemplate.setInterceptors(interceptors);
		}
	}

	public abstract String getMethod();

	protected <T extends RestResult> T exec(Class<T> result) throws BitrixRestApiException {
		ResponseEntity<T> response = restTemplate.postForEntity(auth.getMethodUri(getMethod()), this, result);

		String msg = formatErrorMessage(response.getStatusCode(), response.getBody());
		log.debug(msg);

		if (response.getStatusCode().isError())
			throw new BitrixRestApiException(msg);

		return response.getBody();
	}

	public String exec_debug() throws BitrixRestApiException {
		String result = restTemplate.postForObject(auth.getMethodUri(getMethod()), this, String.class);
		log.info(result);
		return result;
	}

	protected String formatErrorMessage(HttpStatus statusCode, RestResult body) {
		return String.format("method %s: %d %s (%s)", getMethod(), statusCode.value(), statusCode.getReasonPhrase(),
				(body != null) ?
						String.format("%s (%s)", body.getErrorDescription(), body.getError()) :
							"No error description");
	}
}
