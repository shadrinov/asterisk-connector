package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalCallHide extends RestRequestTemplate {
	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("USER_ID")
	private ArrayList<Integer> userIds;


	public RestRequestExternalCallHide(BitrixAuth auth, String callId, ArrayList<Integer> userIds) {
		super(auth);

		this.callId = callId;
		this.userIds = userIds;
	}

	@Override
	public String getMethod() {
		return "telephony.externalcall.hide";
	}
}
