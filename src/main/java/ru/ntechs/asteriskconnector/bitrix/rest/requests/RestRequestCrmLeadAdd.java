package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.ArrayList;
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
	private HashMap<String, Object> fields;
	private HashMap<String, String> params;

	public RestRequestCrmLeadAdd(BitrixAuth auth) {
		super(auth);

		this.fields = new HashMap<>();
		this.params = new HashMap<>();
	}

	public void addField(String name, String val) {
		fields.put(name, val);
	}

	public void addFieldPhone(String phone) {
		addFieldPhone(phone, null);
	}

	public void addFieldPhone(String phone, String type) {
		if (phone == null)
			return;

		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> phones = (ArrayList<HashMap<String, String>>) fields.get("PHONE");

		if (phones == null) {
			phones = new ArrayList<>();
			fields.put("PHONE", phones);
		}

		for (HashMap<String, String> entry : phones) {
			String value = entry.get("VALUE");

			if ((value != null) && (value.equals(phones)))
				return;
		}

		HashMap<String, String> newEntry = new HashMap<>();
		newEntry.put("VALUE", phone);

		if (type != null)
			newEntry.put("VALUE_TYPE", type);

		phones.add(newEntry);
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
