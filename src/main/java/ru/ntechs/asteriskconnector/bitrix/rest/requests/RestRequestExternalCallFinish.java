package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalCallFinish;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalCallFinish extends RestRequestTemplate {
	@JsonIgnore
	public static final String METHOD = "telephony.externalcall.finish";

	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("USER_ID")
	private Long userId;

	@JsonProperty("DURATION")
	private Integer duration;

	@JsonProperty("COST")
	private Double cost;

	@JsonProperty("COST_CURRENCY")
	private String costCurrency;

	@JsonProperty("STATUS_CODE")
	private Integer statusCode;

	@JsonProperty("FAILED_REASON")
	private String failedReason;

	@JsonProperty("RECORD_URL")
	private String recordURL;

	@JsonProperty("VOTE")
	private Integer vote;

	@JsonProperty("ADD_TO_CHAT")
	private Integer addToChat;


	public RestRequestExternalCallFinish(BitrixAuth auth, String callId, Long userId, Integer duration) {
		super(auth);

		this.callId = callId;
		this.userId = userId;
		this.duration = duration;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultExternalCallFinish exec() throws BitrixRestApiException {
		return exec(RestResultExternalCallFinish.class);
	}
}
