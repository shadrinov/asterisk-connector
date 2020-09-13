package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TelephoneSearchCrmEntity {
	@JsonProperty("CRM_ENTITY_TYPE")
	private String type;

	@JsonProperty("CRM_ENTITY_ID")
	private Long id;

	@JsonProperty("ASSIGNED_BY_ID")
	private Long responsibleId;

	@JsonProperty("ASSIGNED_BY")
	private TelephoneSearchCrmEntityResponsible responsible;
}
