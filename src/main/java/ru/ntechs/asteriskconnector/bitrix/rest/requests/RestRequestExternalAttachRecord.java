package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalAttachRecord extends RestRequestTemplate {
	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("FILENAME")
	private String filename;

//	@JsonSerialize(using = BitrixBinarySerializer.class)
	@JsonProperty("FILE_CONTENT")
	private byte[] fileContent;

	@JsonProperty("RECORD_URL")
	private String recordURL;

	public RestRequestExternalAttachRecord(BitrixAuth auth, String callId, String filename) {
		super(auth);

		this.callId = callId;
		this.filename = filename;
	}

	@Override
	public String getMethod() {
		return "telephony.externalCall.attachRecord";
	}
}
