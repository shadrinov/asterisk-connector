package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Event;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultEventUnbind;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestEventUnbind extends RestRequestTemplate {
	private String event;
	private String handler;

	@JsonProperty("auth_type")
	private Integer authType;

	private Integer offline;

	public RestRequestEventUnbind(BitrixAuth auth, Event event) {
		super(auth);

		this.event = event.getEvent();
		this.handler = event.getHandler();
		this.authType = event.getAuthType();
		this.offline = event.getOffline();
	}

	@Override
	public String getMethod() {
		return "event.unbind";
	}

	public RestResultEventUnbind exec() throws BitrixRestApiException {
		return exec(RestResultEventUnbind.class);
	}
}
