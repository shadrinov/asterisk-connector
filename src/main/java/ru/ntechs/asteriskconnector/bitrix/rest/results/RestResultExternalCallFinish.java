package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.CallStatistics;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalCallFinish extends RestResult {
	private CallStatistics result;
}
