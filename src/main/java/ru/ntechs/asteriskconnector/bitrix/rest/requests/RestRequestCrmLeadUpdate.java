package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultCrmLeadUpdate;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestCrmLeadUpdate extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "crm.lead.update";

	private Long id;
	private HashMap<String, Object> fields;
	private HashMap<String, String> params;

	public RestRequestCrmLeadUpdate(BitrixAuth auth) {
		super(auth);

		this.fields = new HashMap<>();
		this.params = new HashMap<>();
	}

	public void addField(String name, String val) {
		fields.put(name, val);
	}

	public void removeField(String name) {
		fields.remove(name);
	}

	public void addParam(String name, String val) {
		params.put(name, val);
	}

	public void removeParam(String name) {
		params.remove(name);
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultCrmLeadUpdate exec() throws BitrixRestApiException {
		return exec(RestResultCrmLeadUpdate.class);
	}
}
