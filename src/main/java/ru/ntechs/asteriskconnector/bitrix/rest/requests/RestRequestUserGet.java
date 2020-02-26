package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultUserGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestUserGet extends RestRequestTemplate {
	private Integer id;

	public RestRequestUserGet(BitrixAuth auth, Integer id) {
		super(auth);

		this.id = id;
	}

	@Override
	public String getMethod() {
		return "user.get";
	}

	public RestResultUserGet exec() throws BitrixRestApiException {
		return exec(RestResultUserGet.class);
	}
}
