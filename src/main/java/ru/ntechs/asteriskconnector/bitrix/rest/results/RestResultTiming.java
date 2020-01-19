package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestResultTiming {
	private Float start;
	private Float finish;
	private Float duration;
	private Float processing;

	@JsonProperty("date_start")
	private Date dateStart;

	@JsonProperty("date_finish")
	private Date dateFinish;
}
