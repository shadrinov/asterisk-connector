package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Id;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalLineAdd extends RestResult {
	private Id result;
}
