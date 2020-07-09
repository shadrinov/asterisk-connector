package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CRMCreatedEntity {
    @JsonProperty("ENTITY_TYPE")
	private String entityType;

	@JsonProperty("ENTITY_ID")
	private Long entityId;
}
