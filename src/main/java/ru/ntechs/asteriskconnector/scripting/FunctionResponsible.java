package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.BitrixTelephony;
import ru.ntechs.asteriskconnector.bitrix.rest.data.CrmDuplicateFindByComm;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmDuplicateFindByComm;

@Slf4j
public class FunctionResponsible extends Function {
	public static final String NAME    = "Responsible";
	public static final String LC_NAME = "responsible";

	private String phone;

	public FunctionResponsible(Expression expression, ArrayList<Scalar> params) throws BitrixLocalException {
		super(expression, params);

		if (params.size() != 1)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s(phone)",
					toString(), NAME));

		this.phone = params.get(0).asString();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		ScriptFactory scriptFactory = getScriptFactory();
		BitrixTelephony bitrixTelephony = scriptFactory.getBitrixTelephony();

		try {
			CrmDuplicateFindByComm entities = bitrixTelephony.findCrmDuplicateByComm(phone);

			if (entities == null)
				return new ScalarString("<entity>");

			if ((entities.getCompanies() != null) && !entities.getCompanies().isEmpty()) {
				if (entities.getCompanies().size() > 1)
					log.info("Phone number {} is assosiated with multiple companies: {}", phone, entities.getCompanies());

				for (Long companyId : entities.getCompanies()) {
					HashMap<String, Object> company = bitrixTelephony.getCrmCompany(companyId);
					Object respUserId = company.get("ASSIGNED_BY_ID");

					if (respUserId != null) {
						if (respUserId instanceof String)
							return new ScalarString("<userId>", (String)respUserId);
						else
							log.info("unexpected company data: {}, skipping...", respUserId);
					}
				}
			}

			if ((entities.getContacts() != null) && !entities.getContacts().isEmpty()) {
				if (entities.getContacts().size() > 1)
					log.info("Phone number {} is assosiated with multiple contacts: {}", phone, entities.getContacts());

				for (Long contactsId : entities.getContacts()) {
					HashMap<String, Object> contact = bitrixTelephony.getCrmContact(contactsId);
					Object respUserId = contact.get("ASSIGNED_BY_ID");

					if (respUserId != null) {
						if (respUserId instanceof String)
							return new ScalarString("<userId>", (String)respUserId);
						else
							log.info("unexpected contact data: {}, skipping...", respUserId);
					}
				}
			}

			if ((entities.getLeads() != null) && !entities.getLeads().isEmpty()) {
				if (entities.getLeads().size() > 1)
					log.info("Phone number {} is assosiated with multiple leads: {}", phone, entities.getLeads());

				for (Long leadId : entities.getLeads()) {
					HashMap<String, Object> lead = bitrixTelephony.getCrmLead(leadId);
					Object respUserId = lead.get("ASSIGNED_BY_ID");

					if (respUserId != null) {
						if (respUserId instanceof String)
							return new ScalarString("<userId>", (String)respUserId);
						else
							log.info("unexpected lead data: {}, skipping...", respUserId);
					}
				}
			}
		} catch (BitrixRestApiException e) {
			throw new BitrixLocalException(
					String.format("%s: failed method: %s(%s)",
							e.getMessage(), RestRequestCrmDuplicateFindByComm.METHOD, phone));
		}

		return new ScalarString("<userId>");
	}

	@Override
	public ArrayList<? extends Object> getIntermediateBeans() {
		// TODO Auto-generated method stub
		return null;
	}
}
