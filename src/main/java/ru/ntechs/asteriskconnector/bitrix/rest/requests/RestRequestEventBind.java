package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestEventBind extends RestRequestTemplate {
	private String handler;

	@JsonProperty("auth_type")
	private String authType;

	@JsonProperty("event_type")
	private String eventType;

	public RestRequestEventBind(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return "event.bind";
	}
}
