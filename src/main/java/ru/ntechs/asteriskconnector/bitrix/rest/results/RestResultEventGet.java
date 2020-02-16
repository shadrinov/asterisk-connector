package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Event;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultEventGet extends RestResult {
	private ArrayList<Event> result;
}
