package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultCrmLeadUpdate extends RestResult {
	private Boolean result;
}
