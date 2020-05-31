package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Id {
	@JsonProperty("ID")
	private Long Id;
}
