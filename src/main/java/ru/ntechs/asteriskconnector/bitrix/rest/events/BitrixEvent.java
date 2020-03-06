package ru.ntechs.asteriskconnector.bitrix.rest.events;

import java.util.List;

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

//	auth[access_token]
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
		this.event = getString(data, "event");
		this.ts = getInteger(data, "ts");

		// Common
		this.authAccessToken = getString(data, "auth[access_token]");
		this.authExpires = getInteger(data, "auth[expires]");
		this.authExpiresIn = getInteger(data, "auth[expires_in]");
		this.authScope = getString(data, "auth[scope]");
		this.authDomain = getString(data, "auth[domain]");
		this.authServerEndpoint = getString(data, "auth[server_endpoint]");
		this.authStatus = getString(data, "auth[status]");
		this.authClientEndpoint = getString(data, "auth[client_endpoint]");
		this.authMemberId = getString(data, "auth[member_id]");
		this.authUserId = getInteger(data, "auth[user_id]");
		this.authRefreshToken = getString(data, "auth[refresh_token]");
		this.authApplicationToken = getString(data, "auth[application_token]");
	}

	public static String getEvent(MultiValueMap<String, String> data) {
		return getString(data, "event").toUpperCase();
	}

	protected static String getString(MultiValueMap<String, String> data, String attr) {
		List<String> vals = data.get(attr);
		return ((vals != null) && (vals.size() > 0)) ? vals.get(0) : null;
	}

	protected static Integer getInteger(MultiValueMap<String, String> data, String attr) {
		List<String> vals = data.get(attr);

		if ((vals != null) && (vals.size() > 0)) {
			String value = vals.get(0);

			return (!value.isEmpty()) ? Integer.valueOf(value) : null;
		}
		else
			return null;
	}
}
