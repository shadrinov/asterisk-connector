package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResult;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalCallHide extends RestRequestTemplate {
	@JsonIgnore
	public static final String METHOD = "telephony.externalcall.hide";

	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("USER_ID")
	private ArrayList<Long> userIds;


	public RestRequestExternalCallHide(BitrixAuth auth, String callId, ArrayList<Long> userIds) {
		super(auth);

		this.callId = callId;
		this.userIds = userIds;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResult exec() throws BitrixRestApiException {
		return exec(RestResult.class);
	}
}
