package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CrmCreatedEntity {
    @JsonProperty("ENTITY_TYPE")
	private String entityType;

	@JsonProperty("ENTITY_ID")
	private Long entityId;
}
