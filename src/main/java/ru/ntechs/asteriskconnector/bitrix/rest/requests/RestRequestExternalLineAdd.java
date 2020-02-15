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
	private Integer number;

	@JsonProperty("NAME")
	private String name;

	public RestRequestExternalLineAdd(BitrixAuth auth, Integer number, String name) {
		super(auth);

		this.number = number;
		this.name = name;
	}

	@Override
	public String getMethod() {
		return "telephony.externalLine.add";
	}
}
