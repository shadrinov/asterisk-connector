package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultEventGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestEventGet extends RestRequestTemplate {
	@JsonIgnore
	public static final String METHOD = "event.get";

	public RestRequestEventGet(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultEventGet exec() throws BitrixRestApiException {
		return exec(RestResultEventGet.class);
	}
}
