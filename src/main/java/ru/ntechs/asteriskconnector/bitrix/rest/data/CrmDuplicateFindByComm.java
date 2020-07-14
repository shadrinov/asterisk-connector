package ru.ntechs.asteriskconnector.bitrix.rest.data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CrmDuplicateFindByComm {
	@JsonProperty("LEAD")
	private ArrayList<Long> leads;

	@JsonProperty("CONTACT")
	private ArrayList<Long> contacts;

	@JsonProperty("COMPANY")
	private ArrayList<Long> companies;
}
