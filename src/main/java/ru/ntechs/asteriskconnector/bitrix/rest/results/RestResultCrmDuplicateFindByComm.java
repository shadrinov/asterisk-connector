package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.CrmDuplicateFindByComm;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultCrmDuplicateFindByComm extends RestResult {
	private CrmDuplicateFindByComm result;
}
