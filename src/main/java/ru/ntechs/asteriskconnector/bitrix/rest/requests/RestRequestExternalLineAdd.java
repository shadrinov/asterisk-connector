package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalLineAdd;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalLineAdd extends RestRequestTemplate {
	@JsonIgnore
	public static final String METHOD = "telephony.externalLine.add";

	@JsonProperty("NUMBER")
	private String number;

	@JsonProperty("NAME")
	private String name;

	public RestRequestExternalLineAdd(BitrixAuth auth, String number, String name) {
		super(auth);

		this.number = number;
		this.name = name;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultExternalLineAdd exec() throws BitrixRestApiException {
		return exec(RestResultExternalLineAdd.class);
	}
}
