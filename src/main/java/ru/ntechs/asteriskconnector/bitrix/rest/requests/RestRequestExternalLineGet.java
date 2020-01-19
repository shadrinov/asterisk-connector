package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

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
}
