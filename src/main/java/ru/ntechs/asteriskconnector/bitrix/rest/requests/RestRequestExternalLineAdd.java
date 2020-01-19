package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalLineAdd extends RestRequestTemplate {
	@JsonProperty("NUMBER")
	private String number;

	public RestRequestExternalLineAdd(BitrixAuth auth, String number) {
		super(auth);

		this.number = number;
	}

	@Override
	public String getMethod() {
		return "telephony.externalLine.add";
	}
}
