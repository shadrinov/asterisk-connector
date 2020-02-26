package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
public class RestOAuth {
	@JsonIgnore
	protected static RestTemplate restTemplate;

	public RestOAuth() {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
			restTemplate.setErrorHandler(new ResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
					log.info("Request status: {} {}", response.getStatusCode().value(), response.getStatusText());
					return false;
				}

				@Override
				public void handleError(ClientHttpResponse response) throws IOException {
				}
			});
		}
	}

	public void auth() {

	}
}
