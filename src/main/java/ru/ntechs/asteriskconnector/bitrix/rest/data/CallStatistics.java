package ru.ntechs.asteriskconnector.bitrix.rest.data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixDateDeserializer;

@Getter
@Setter
@ToString
public class CallStatistics {
	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("ID")
	private Long id;

	@JsonProperty("CALL_TYPE")
	private Short callType;

	@JsonProperty("CALL_VOTE")
	private Short callVote;

	@JsonProperty("COMMENT")
	private String comment;

	@JsonProperty("PORTAL_USER_ID")
	private Long portalUserId;

	@JsonProperty("PORTAL_NUMBER")
	private String portalNumber;

	@JsonProperty("PHONE_NUMBER")
	private String phoneNumber;

	@JsonProperty("CALL_DURATION")
	private Integer callDuration;

	@JsonDeserialize(using = BitrixDateDeserializer.class)
	@JsonFormat(shape = JsonFormat.Shape.OBJECT, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	@JsonProperty("CALL_START_DATE")
	private Date callStartDate;

	@JsonProperty("COST")
	private Double cost;

	@JsonProperty("COST_CURRENCY")
	private String costCurrency;

	@JsonProperty("CALL_FAILED_CODE")
	private String callFailedCode;

	@JsonProperty("CALL_FAILED_REASON")
	private String callFailedReason;

	@JsonProperty("CRM_ACTIVITY_ID")
	private Long crmActivityId;

	@JsonProperty("CRM_ENTITY_ID")
	private Long crmEntityId;

	@JsonProperty("CRM_ENTITY_TYPE")
	private String crmEntityType;

	@JsonProperty("REST_APP_ID")
	private Long restAppId;

	@JsonProperty("REST_APP_NAME")
	private String restAppName;

	@JsonProperty("REDIAL_ATTEMPT")
	private Integer redialAttempt;

	@JsonProperty("SESSION_ID")
	private Long sessionId;

	@JsonProperty("TRANSCRIPT_ID")
	private Long transcriptId;

	@JsonProperty("TRANSCRIPT_PENDING")
	private String transcriptPending;

	@JsonProperty("RECORD_FILE_ID")
	private Long recordFileId;
}
