package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultCrmCompanyGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestCrmCompanyGet extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "crm.company.get";

	private Long id;

	public RestRequestCrmCompanyGet(BitrixAuth auth) {
		super(auth);
	}

	public RestRequestCrmCompanyGet(BitrixAuth auth, Long id) {
		super(auth);
		this.id = id;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultCrmCompanyGet exec() throws BitrixRestApiException {
		return exec(RestResultCrmCompanyGet.class);
	}
}
