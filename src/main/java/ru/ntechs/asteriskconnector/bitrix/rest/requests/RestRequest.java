package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;

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
public abstract class RestRequest {
	@JsonProperty("access_token")
	private String accessToken;

	@JsonIgnore
	private BitrixAuth auth;

	@JsonIgnore
	private static RestTemplate restTemplate;

	public RestRequest(BitrixAuth auth) {
		this.auth = auth;
		this.accessToken = auth.getAccessToken();

		if (restTemplate == null) {
			restTemplate = new RestTemplate();

			for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
				if (converter instanceof MappingJackson2HttpMessageConverter) {
					((MappingJackson2HttpMessageConverter) converter).getObjectMapper()
						.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
				}
			}

			restTemplate.setErrorHandler(new ResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
//					log.info("Request status: {} {}", response.getStatusCode().value(), response.getStatusText());
					return (response.getStatusCode().compareTo(HttpStatus.UNAUTHORIZED) == 0);
				}

				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
					log.info("Request error: {} {}", response.getStatusCode().value(), response.getStatusText());

					StringBuilder inputStringBuilder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
					String line = bufferedReader.readLine();

					while (line != null) {
						inputStringBuilder.append(line);
						inputStringBuilder.append('\n');
						line = bufferedReader.readLine();
					}

					log.info("Headers      : {}", response.getHeaders());
					log.info("Response body: {}", inputStringBuilder.toString());
				}
			});

//			restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
//			ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//			interceptors.add(new LoggingRequestInterceptor());
//			restTemplate.setInterceptors(interceptors);
		}
	}

	@JsonIgnore
	public abstract String getMethod();

	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	protected <T extends RestResult> T exec(Class<T> result) throws BitrixRestApiException {
		ResponseEntity<T> response = restTemplate.postForEntity(auth.getMethodUri(getMethod()), this, result);
		RestResult body = response.getBody();

		String msg = formatErrorMessage(response.getStatusCode(), body);
		log.debug(msg);

		if (response.getStatusCode().isError() || (body.getError() != null) || (body.getErrorDescription() != null))
			throw new BitrixRestApiException(msg);

		return response.getBody();
	}

	public String exec_debug() throws BitrixRestApiException {
		String result = restTemplate.postForObject(auth.getMethodUri(getMethod()), this, String.class);
		log.info("exec_debug(): {}", result);
		return result;
	}

	protected String formatErrorMessage(HttpStatus statusCode, RestResult body) {
		return String.format("method %s: %d %s (%s)", getMethod(), statusCode.value(), statusCode.getReasonPhrase(),
				(body != null) ?
						String.format("%s (%s)", body.getErrorDescription(), body.getError()) :
							"No error description");
	}
}
