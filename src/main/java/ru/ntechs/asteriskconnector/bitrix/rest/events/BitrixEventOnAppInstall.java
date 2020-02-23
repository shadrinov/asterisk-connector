package ru.ntechs.asteriskconnector.bitrix.rest.events;

import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BitrixEventOnAppInstall extends BitrixEvent {

//	data[VERSION]
	private Integer dataVersion;            // [1]

//	data[LANGUAGE_ID]
	private String dataLanguageId;          // [ru]

	public BitrixEventOnAppInstall(MultiValueMap<String, String> data) {
		super(data);

		// Event ONAPPINSTALL
		this.dataVersion = getInteger(data, "data[VERSION]");
		this.dataLanguageId = getString(data, "data[LANGUAGE_ID]");
	}

}
