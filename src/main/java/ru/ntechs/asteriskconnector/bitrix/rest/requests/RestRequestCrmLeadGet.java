package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultCrmLeadGet;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestCrmLeadGet extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "crm.lead.get";

	private Long id;

	public RestRequestCrmLeadGet(BitrixAuth auth) {
		super(auth);
	}

	public RestRequestCrmLeadGet(BitrixAuth auth, Long id) {
		super(auth);
		this.id = id;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultCrmLeadGet exec() throws BitrixRestApiException {
		return exec(RestResultCrmLeadGet.class);
	}
}
