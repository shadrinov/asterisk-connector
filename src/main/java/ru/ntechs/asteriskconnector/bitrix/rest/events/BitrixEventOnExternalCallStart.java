package ru.ntechs.asteriskconnector.bitrix.rest.events;

import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BitrixEventOnExternalCallStart extends BitrixEvent {

//	data[PHONE_NUMBER]
	private String dataPhoneNumber;		// [34]

//	data[PHONE_NUMBER_INTERNATIONAL]
	private String dataPhoneNumberInternational;	// [34]

//	data[EXTENSION]
	private String dataExtension;			// []

//	data[USER_ID]
	private Integer dataUserId;				// [7]

//	data[CALL_LIST_ID]
	private Integer dataCallListId;			// [0]

//	data[LINE_NUMBER]
	private String dataLineNumber;			// [679606]

//	data[IS_MOBILE]
	private Integer dataIsMobile;			// [0]

//	data[CALL_ID]
	private String dataCallId;				// [externalCall.4c5e2d96ded95ecd628e2cfc325d7cdf.1581860751]

//	data[CRM_ENTITY_TYPE]
	private String dataCrmEntityType;		// [LEAD]

//	data[CRM_ENTITY_ID]
	private Integer dataCrmEntityId;		// [29]

	public BitrixEventOnExternalCallStart(MultiValueMap<String, String> data) {
		super(data);

		this.dataPhoneNumber = getString(data, "data[PHONE_NUMBER]");
		this.dataPhoneNumberInternational = getString(data, "data[PHONE_NUMBER_INTERNATIONAL]");
		this.dataExtension = getString(data, "data[EXTENSION]");
		this.dataUserId = getInteger(data, "data[USER_ID]");
		this.dataCallListId = getInteger(data, "data[CALL_LIST_ID]");
		this.dataLineNumber = getString(data, "data[LINE_NUMBER]");
		this.dataIsMobile = getInteger(data, "data[IS_MOBILE]");
		this.dataCallId = getString(data, "data[CALL_ID]");
		this.dataCrmEntityType = getString(data, "data[CRM_ENTITY_TYPE]");
		this.dataCrmEntityId = getInteger(data, "data[CRM_ENTITY_ID]");
	}
}
