package ru.ntechs.asteriskconnector.bitrix.rest.results;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Timing;

@Getter
@Setter
public class RestResult {
	private String error;

	@JsonProperty("error_description")
	private String errorDescription;

	private Timing time;
}
