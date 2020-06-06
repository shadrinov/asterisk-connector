package ru.ntechs.asteriskconnector.bitrix.rest.results;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.rest.data.FileId;

@Getter
@Setter
@ToString(callSuper = true)
public class RestResultExternalCallAttachRecord extends RestResult {
	private FileId result;
}
