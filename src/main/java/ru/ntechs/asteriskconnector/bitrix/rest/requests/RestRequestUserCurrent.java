package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultUserCurrent;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestUserCurrent extends RestRequestTemplate {

	public RestRequestUserCurrent(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return "user.current";
	}

	public RestResultUserCurrent exec() throws BitrixRestApiException {
		return exec(RestResultUserCurrent.class);
	}
}
