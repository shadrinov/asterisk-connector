package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalCallRegister;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalCallRegister extends RestRequestTemplate {
	final static public Integer CRM_CREATE_NEGATIVE = 0;
	final static public Integer CRM_CREATE_POSITIVE = 1;

	final static public String CRM_ENTITY_TYPE_CONTACT = "CONTACT";
	final static public String CRM_ENTITY_TYPE_COMPANY = "COMPANY";
	final static public String CRM_ENTITY_TYPE_LEAD = "LEAD";

	final static public Integer SHOW_NEGATIVE = 0;
	final static public Integer SHOW_POSITIVE = 1;

	final static public Integer TYPE_OUTGOING = 1;
	final static public Integer TYPE_INCOMING = 2;
	final static public Integer TYPE_INCOMING_FORWARDED = 3;
	final static public Integer TYPE_CALLBACK = 4;

	@JsonProperty("USER_PHONE_INNER")
	private String userPhoneInner;

	@JsonProperty("USER_ID")
	private Long userId;

	@JsonProperty("PHONE_NUMBER")
	private String phoneNumber;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	@JsonProperty("CALL_START_DATE")
	private Date callStartDate;

	@JsonProperty("CRM_CREATE")
	private Integer crmCreate;

	@JsonProperty("CRM_SOURCE")
	private String crmSource;

	@JsonProperty("CRM_ENTITY_TYPE")
	private String crmEntityType;

	@JsonProperty("CRM_ENTITY_ID")
	private Integer crmEntityId;

	@JsonProperty("SHOW")
	private Integer show;

	@JsonProperty("CALL_LIST_ID")
	private Integer callListId;

	@JsonProperty("LINE_NUMBER")
	private String lineNumber;

	@JsonProperty("TYPE")
	private Short type;

	public RestRequestExternalCallRegister(BitrixAuth auth, String userPhoneInner, String phoneNumber, Short type) {
		super(auth);

		this.userPhoneInner = userPhoneInner;
		this.phoneNumber = phoneNumber;
		this.type = type;
	}

	public RestRequestExternalCallRegister(BitrixAuth auth, Long userId, String phoneNumber, Short type) {
		super(auth);

		this.userId = userId;
		this.phoneNumber = phoneNumber;
		this.type = type;
	}

	@Override
	public String getMethod() {
		return "telephony.externalcall.register";
	}

	public RestResultExternalCallRegister exec() throws BitrixRestApiException {
		return exec(RestResultExternalCallRegister.class);
	}
}
