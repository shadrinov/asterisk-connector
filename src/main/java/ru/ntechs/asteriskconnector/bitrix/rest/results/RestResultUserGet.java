package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultUserGet extends RestResult {
	private ArrayList<User> result;
}
