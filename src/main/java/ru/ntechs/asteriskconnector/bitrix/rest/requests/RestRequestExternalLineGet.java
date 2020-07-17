package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalLineGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalLineGet extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "telephony.externalLine.get";

	public RestRequestExternalLineGet(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultExternalLineGet exec() throws BitrixRestApiException {
		return exec(RestResultExternalLineGet.class);
	}
}
