package ru.ntechs.asteriskconnector.bitrix.rest.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExternalLine {
	@JsonProperty("NUMBER")
	private String number;

	@JsonProperty("NAME")
	private String name;
}
