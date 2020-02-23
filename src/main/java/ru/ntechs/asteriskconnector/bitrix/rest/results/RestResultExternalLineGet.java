package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalLine;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalLineGet extends RestResult {
	private ArrayList<ExternalLine> result;
}
