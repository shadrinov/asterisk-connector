package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultUserCurrent;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestUserCurrent extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "user.current";

	public RestRequestUserCurrent(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultUserCurrent exec() throws BitrixRestApiException {
		return exec(RestResultUserCurrent.class);
	}
}
