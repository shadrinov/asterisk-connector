package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultEventGet;

public class RestRequestEventGet extends RestRequestTemplate {

	public RestRequestEventGet(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return "event.get";
	}

	public RestResultEventGet exec() throws BitrixRestApiException {
		return exec(RestResultEventGet.class);
	}
}
