package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

public class RestRequestExternalCallShow extends RestRequestTemplate {
	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("USER_ID")
	private ArrayList<Integer> userIds;


	public RestRequestExternalCallShow(BitrixAuth auth, String callId, ArrayList<Integer> userIds) {
		super(auth);

		this.callId = callId;
		this.userIds = userIds;
	}

	@Override
	public String getMethod() {
		return "telephony.externalcall.show";
	}

}
