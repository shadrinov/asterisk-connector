package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalCallRegister extends RestResult {
	private ExternalCall result;
}
