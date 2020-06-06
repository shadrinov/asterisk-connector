package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalCallAttachRecord;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalCallAttachRecord extends RestRequestTemplate {
	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("FILENAME")
	private String filename;

	@JsonProperty("FILE_CONTENT")
	private String fileContent;

	@JsonProperty("RECORD_URL")
	private String recordURL;

	public RestRequestExternalCallAttachRecord(BitrixAuth auth, String callId, String filename) {
		super(auth);

		this.callId = callId;
		this.filename = filename;
	}

	@Override
	public String getMethod() {
		return "telephony.externalCall.attachRecord";
	}

	public RestResultExternalCallAttachRecord exec() throws BitrixRestApiException {
		return exec(RestResultExternalCallAttachRecord.class);
	}
}
