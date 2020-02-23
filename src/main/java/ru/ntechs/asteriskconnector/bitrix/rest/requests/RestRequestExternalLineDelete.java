package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalLine;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalLineDelete;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalLineDelete extends RestRequestTemplate {
	@JsonProperty("NUMBER")
	private Integer number;

	@JsonProperty("NAME")
	private String name;

	public RestRequestExternalLineDelete(BitrixAuth auth, ExternalLine telephonyLine) {
		super(auth);

		this.number = telephonyLine.getNumber();
		this.name = telephonyLine.getName();
	}

	@Override
	public String getMethod() {
		return "telephony.externalLine.delete";
	}

	public RestResultExternalLineDelete exec() throws BitrixRestApiException {
		return exec(RestResultExternalLineDelete.class);
	}
}
