package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultCrmLeadAdd;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestCrmLeadAdd extends RestRequestTemplate {
	private HashMap<String, String> fields;
	private HashMap<String, String> params;

	public RestRequestCrmLeadAdd(BitrixAuth auth) {
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
		return "crm.lead.add";
	}

	public RestResultCrmLeadAdd exec() throws BitrixRestApiException {
		return exec(RestResultCrmLeadAdd.class);
	}
}
