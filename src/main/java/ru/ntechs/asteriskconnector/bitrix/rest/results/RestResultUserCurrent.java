package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultUserCurrent extends RestResult {
	private User result;
}
