package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.BitrixTelephony;
import ru.ntechs.asteriskconnector.bitrix.rest.data.TelephoneSearchCrmEntity;
import ru.ntechs.asteriskconnector.bitrix.rest.data.TelephoneSearchCrmEntityResponsible;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallSearchCrmEntities;

@Slf4j
public class FunctionResponsible extends Function {
	public static final String NAME    = "Responsible";
	public static final String LC_NAME = "responsible";

	private String phone;
	private ArrayList<String> fields;
	private ArrayList<String> order;

	public FunctionResponsible(Expression expression, ArrayList<Scalar> params) throws BitrixLocalException {
		super(expression, params);

		if ((params.size() < 1) || (params.size() > 4))
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s(phone[, field[, lead|company|contact[, lead|company|contact[, lead|company|contact]]]])",
					toString(), NAME));

		this.phone  = params.get(0).asString();
		this.fields = new ArrayList<>();

		if (params.size() >= 2)
			for (String f : params.get(1).asString().toLowerCase().split("\\s*\\|\\s*", 0))
				this.fields.add(f);
		else
			this.fields.add("assigned_by_id");

		this.order = new ArrayList<>();

		if (params.size() >= 3) {
			order.add(params.get(2).asString().toLowerCase());

			if (params.size() >= 4) {
				order.add(params.get(3).asString().toLowerCase());

				if (params.size() >= 5)
					order.add(params.get(4).asString().toLowerCase());
			}

			for (String entityType : order)
				checkEntityType(entityType);
		}
	}

	private void checkEntityType(String entityType) throws BitrixLocalException {
		if ((entityType != null) && !entityType.equals("lead") && !entityType.equals("contact") && !entityType.equals("company"))
			throw new BitrixLocalException(String.format("Unsupported entity type: %s", entityType));
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
			ArrayList<TelephoneSearchCrmEntity> entities = bitrixTelephony.searchCrmEntity(phone);
			log.info(entities.toString());

			if (entities.isEmpty())
				return new ScalarString("<userId>");

			if (!order.isEmpty()) {
				ArrayList<TelephoneSearchCrmEntity> leads = new ArrayList<>();
				ArrayList<TelephoneSearchCrmEntity> contacts = new ArrayList<>();
				ArrayList<TelephoneSearchCrmEntity> companies = new ArrayList<>();

				for (TelephoneSearchCrmEntity entry : entities)
					switch (entry.getType()) {
						case ("LEAD"): leads.add(entry); break;
						case ("CONTACT"): contacts.add(entry); break;
						case ("COMPANY"): companies.add(entry); break;
					}

				for (String entity : order) {
					Scalar result = null;

					switch (entity) {
						case ("lead"): result = scan(leads); break;
						case ("contact"): result = scan(contacts); break;
						case ("company"): result = scan(companies); break;
					}

					if ((result != null) && !result.isEmpty())
						return result;
				}
			}
			else {
				Scalar result = scan(entities);

				if ((result != null) && !result.isEmpty())
					return result;
			}
		} catch (BitrixRestApiException e) {
			throw new BitrixLocalException(
					String.format("%s: failed method: %s(%s)",
							e.getMessage(), RestRequestExternalCallSearchCrmEntities.METHOD, phone));
		}

		return new ScalarString("<userId>");
	}

	private Scalar scan(ArrayList<TelephoneSearchCrmEntity> entities) {
		if (!entities.isEmpty()) {
			if (entities.size() > 1)
				log.info("Phone number {} is assosiated with multiple entites of type {}: {}",
						phone, entities.get(0).getType(), entities);

			for (TelephoneSearchCrmEntity entity : entities)
				for (String f : fields) {
					Scalar attr = getAttribute(entity, f);

					if ((attr != null) && !attr.isEmpty())
						return attr;
				}
		}

		return null;
	}

	private Scalar getAttribute(TelephoneSearchCrmEntity entity, String attr) {
		if (entity != null) {
			switch (attr) {
				case ("crm_entity_type"): return new ScalarString("<crm_entity_type>", entity.getType());
				case ("crm_entity_id"): return new ScalarInteger("<crm_entity_id>", entity.getId());
				case ("assigned_by_id"): return new ScalarInteger("<assigned_by_id>", entity.getResponsibleId());
			}

			TelephoneSearchCrmEntityResponsible resp = entity.getResponsible();

			if (resp != null)
				switch (attr) {
					case ("timeman_status"): return new ScalarString("<timeman_status>", resp.getTimemanStatus());
					case ("user_phone_inner"): return new ScalarString("<user_phone_inner>", resp.getUserPhoneInner());
					case ("work_phone"): return new ScalarString("<work_phone>", resp.getWorkPhone());
					case ("personal_phone"): return new ScalarString("<personal_phone>", resp.getPersonalPhone());
					case ("personal_mobile"): return new ScalarString("<personal_mobile>", resp.getPersonalMobile());
				}
		}

		return new ScalarInteger("<null>");
	}

	@Override
	public ArrayList<? extends Object> getIntermediateBeans() {
		// TODO Auto-generated method stub
		return null;
	}
}
