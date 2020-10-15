package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.cache.Cache;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalCallSearchCrmEntities;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalCallSearchCrmEntities extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "telephony.externalCall.searchCrmEntities";

	@JsonIgnore
	private static Cache<RestRequestExternalCallSearchCrmEntities, RestResultExternalCallSearchCrmEntities> cache = new Cache<>(300);

	@JsonProperty("PHONE_NUMBER")
	private String phoneNumber;

	public RestRequestExternalCallSearchCrmEntities(BitrixAuth auth) {
		super(auth);
	}

	public RestRequestExternalCallSearchCrmEntities(BitrixAuth auth, String phoneNumber) {
		super(auth);
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultExternalCallSearchCrmEntities exec() throws BitrixRestApiException {
		RestResultExternalCallSearchCrmEntities result = cache.get(this);

		if (result == null)
			result = cache.put(this, exec(RestResultExternalCallSearchCrmEntities.class));

		return result;
	}
}
