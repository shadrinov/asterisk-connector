package ru.ntechs.asteriskconnector.bitrix;

import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// params are {
//	event=[ONAPPINSTALL],
//	data[VERSION]=[1],
//	data[LANGUAGE_ID]=[ru],
//	ts=[1579686153],
//	auth[access_token]=[1927285e0043d4ca0042213e000000070000037c0754d2995eec0feff429359baa5d53],
//	auth[expires]=[1579689753],
//	auth[expires_in]=[3600],
//	auth[scope]=[crm,telephony,call],
//	auth[domain]=[ntechs.bitrix24.ru],
//	auth[server_endpoint]=[https://oauth.bitrix.info/rest/],
//	auth[status]=[L],
//	auth[client_endpoint]=[https://ntechs.bitrix24.ru/rest/],
//	auth[member_id]=[78727e6e7334a5b7f57357a6a7e63480],
//	auth[user_id]=[7],
//	auth[refresh_token]=[09a64f5e0043d4ca0042213e000000070000031f2ccba0dc66d3742428b432eb11ec75],
//	auth[application_token]=[9f871c9abe689a943110e60ebd7337de]
// }

@Getter
@Setter
@ToString
public class BitrixEvent {

//	event
	private String event;                   // [ONAPPINSTALL]

//	ts
	private Integer ts;                     // [1579686153]

//	data[VERSION]
	private Integer dataVersion;            // [1]

//	data[LANGUAGE_ID]
	private String dataLanguageId;          // [ru]

//	@Json("auth[access_token]")
	private String authAccessToken;         // [1927285e0043d4ca0042213e000000070000037c0754d2995eec0feff429359baa5d53]

//	auth[expires]
	private Integer authExpires;            // [1579689753]

//	auth[expires_in]
	private Integer authExpiresIn;          // [3600]

//	auth[scope]
	private String authScope;               // [crm,telephony,call]

//	auth[domain]
	private String authDomain;              // [ntechs.bitrix24.ru]

//	auth[server_endpoint]
	private String authServerEndpoint;      // [https://oauth.bitrix.info/rest/]

//	auth[status]
	private String authStatus;              // [L]

//	auth[client_endpoint]
	private String authClientEndpoint;      // [https://ntechs.bitrix24.ru/rest/]

//	auth[member_id]
	private String authMemberId;            // [78727e6e7334a5b7f57357a6a7e63480]

//	auth[user_id]
	private Integer authUserId;             // [7]

//	auth[refresh_token]
	private String authRefreshToken;        // [09a64f5e0043d4ca0042213e000000070000031f2ccba0dc66d3742428b432eb11ec75]

//	auth[application_token]
	private String authApplicationToken;    // [9f871c9abe689a943110e60ebd7337de]

	public BitrixEvent(MultiValueMap<String, String> data) {
		this.event = getValue(data, "event");
		this.ts = (getValue(data, "ts") != null) ? Integer.valueOf(getValue(data, "ts")) : null;

		this.dataVersion = (getValue(data, "data[VERSION]") != null) ? Integer.valueOf(getValue(data, "data[VERSION]")) : null;
		this.dataLanguageId = getValue(data, "data[LANGUAGE_ID]");

		this.authAccessToken = getValue(data, "auth[access_token]");
		this.authExpires = (getValue(data, "auth[expires]") != null) ? Integer.valueOf(getValue(data, "auth[expires]")) : null;
		this.authExpiresIn = (getValue(data, "auth[expires_in]") != null) ? Integer.valueOf(getValue(data, "auth[expires_in]")) : null;
		this.authScope = getValue(data, "auth[scope]");
		this.authDomain = getValue(data, "auth[domain]");
		this.authServerEndpoint = getValue(data, "auth[server_endpoint]");
		this.authStatus = getValue(data, "auth[status]");
		this.authClientEndpoint = getValue(data, "auth[client_endpoint]");
		this.authMemberId = getValue(data, "auth[member_id]");
		this.authUserId = (getValue(data, "auth[user_id]") != null) ? Integer.valueOf(getValue(data, "auth[user_id]")) : null;
		this.authRefreshToken = getValue(data, "auth[refresh_token]");
		this.authApplicationToken = getValue(data, "auth[application_token]");
	}

	private String getValue(MultiValueMap<String, String> data, String attr) {
		return ((data.get(attr) != null) && (data.get(attr).size() > 0)) ? data.get(attr).get(0) : null;
	}
}
