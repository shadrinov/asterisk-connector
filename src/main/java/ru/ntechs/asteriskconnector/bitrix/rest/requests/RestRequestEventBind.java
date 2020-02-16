package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResult;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestEventBind extends RestRequestTemplate {
	private String event;
	private String handler;

	@JsonProperty("auth_type")
	private String authType;

	@JsonProperty("event_type")
	private String eventType;

	public RestRequestEventBind(BitrixAuth auth, String event, String handler) {
		super(auth);

		this.event = event;
		this.handler = handler;
		this.eventType = "online";
	}

	@Override
	public String getMethod() {
		return "event.bind";
	}

	public RestResult exec() throws BitrixRestApiException {
		return exec(RestResult.class);
	}
}
