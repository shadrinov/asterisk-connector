package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalLineGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalLineGet extends RestRequestTemplate {

	public RestRequestExternalLineGet(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return "telephony.externalLine.get";
	}

	public RestResultExternalLineGet exec() throws BitrixRestApiException {
		return exec(RestResultExternalLineGet.class);
	}
}
