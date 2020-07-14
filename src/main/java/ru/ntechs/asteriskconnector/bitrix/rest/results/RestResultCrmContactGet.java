package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestResultCrmContactGet extends RestResult {
	private HashMap<String, Object> result;
}
