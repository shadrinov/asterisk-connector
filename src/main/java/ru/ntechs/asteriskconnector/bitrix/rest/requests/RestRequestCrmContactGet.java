package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultCrmContactGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestCrmContactGet extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "crm.contact.get";

	private Long id;

	public RestRequestCrmContactGet(BitrixAuth auth) {
		super(auth);
	}

	public RestRequestCrmContactGet(BitrixAuth auth, Long id) {
		super(auth);
		this.id = id;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultCrmContactGet exec() throws BitrixRestApiException {
		return exec(RestResultCrmContactGet.class);
	}
}
