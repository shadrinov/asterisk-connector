package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {
	private String event;
	private String handler;

	@JsonProperty("auth_type")
	private Integer authType;

	private Integer offline;
}
