package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TelephoneSearchCrmEntityResponsible {
	@JsonProperty("ID")
	private Long id;

	@JsonProperty("TIMEMAN_STATUS")
	private String timemanStatus;

	@JsonProperty("USER_PHONE_INNER")
	private String userPhoneInner;

	@JsonProperty("WORK_PHONE")
	private String workPhone;

	@JsonProperty("PERSONAL_PHONE")
	private String personalPhone;

	@JsonProperty("PERSONAL_MOBILE")
	private String personalMobile;
}
