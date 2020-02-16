package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.AffectedRows;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultEventUnbind extends RestResult {
	private AffectedRows result;
}
