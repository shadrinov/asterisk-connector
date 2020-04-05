package ru.ntechs.asteriskconnector.bitrix.rest.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExternalCall {
	@JsonProperty("CALL_ID")
	private String callId;

	@JsonProperty("CRM_CREATED_LEAD")
	private Integer crmCreatedLead;

	@JsonProperty("CRM_CREATED_ENTITIES")
	private ArrayList<String> crmCreatedEntites;

	@JsonProperty("CRM_ENTITY_ID")
	private Integer crmEntityId;

	@JsonProperty("CRM_ENTITY_TYPE")
	private String crmEntityType;
}
