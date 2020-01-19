package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalLineAdd extends RestResultTemplate {
	private ArrayList<String> result;
}
