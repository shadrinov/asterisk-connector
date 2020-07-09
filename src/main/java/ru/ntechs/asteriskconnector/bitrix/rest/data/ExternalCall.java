package ru.ntechs.asteriskconnector.bitrix.rest.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	private Long crmCreatedLead;

	@JsonProperty("CRM_CREATED_ENTITIES")
	private ArrayList<CRMCreatedEntity> crmCreatedEntites;

	@JsonProperty("CRM_ENTITY_ID")
	private Long crmEntityId;

	@JsonProperty("CRM_ENTITY_TYPE")
	private String crmEntityType;

	@JsonIgnore
	private boolean isFinished;
}
