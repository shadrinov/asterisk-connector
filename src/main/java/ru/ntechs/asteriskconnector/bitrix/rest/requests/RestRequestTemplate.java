package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

@Getter
@Setter
@ToString
public abstract class RestRequestTemplate {
	@JsonProperty("access_token")
	private String accessToken;

	public RestRequestTemplate(BitrixAuth auth) {
		this.accessToken = auth.getAccessToken();
	}

	public abstract String getMethod();
}
