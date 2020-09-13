package ru.ntechs.asteriskconnector.bitrix.rest.results;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.TelephoneSearchCrmEntity;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalCallSearchCrmEntities extends RestResult {
	private ArrayList<TelephoneSearchCrmEntity> result;
}
