package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallRegister;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodRegisterExternalCall extends Method {

	public MethodRegisterExternalCall(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action) {
		super(scriptFactory, eventChain, action);
	}

	@Override
	public void exec() {
		HashMap<String, String> data = evaluate(getEventDispatcher(), getEventChain(), getAction().getData());

		try {
			RestRequestExternalCallRegister req;

			log.info("source: {}", (getAction().getData() != null) ? getAction().getData().toString() : "null");
			log.info("evaluated: {}", data.toString());

			String userPhoneInner = data.get("USER_PHONE_INNER");
			String userId = data.get("USER_ID");
			String phoneNumber = data.get("PHONE_NUMBER");
			String type = data.get("TYPE");

			if ((userPhoneInner == null) && (userId == null))
				throw new BitrixLocalException("Required parameter is not defined: USER_PHONE_INNER or USER_ID");

			if (phoneNumber == null)
				throw new BitrixLocalException("Required parameter is not defined: PHONE_NUMBER");

			if (type == null)
				throw new BitrixLocalException("Required parameter is not defined: TYPE");

			if (userId != null)
				req = new RestRequestExternalCallRegister(getAuth(), userId, phoneNumber, validateInt(data, "TYPE"));
			else
				req = new RestRequestExternalCallRegister(getAuth(), userPhoneInner, phoneNumber, validateInt(data, "TYPE"));

			if (data.containsKey("CALL_START_DATE"))
				req.setCallStartDate(validateDate(data, "CALL_START_DATE"));

			if (data.containsKey("CRM_CREATE"))
				req.setCrmCreate(validateInt(data, "CRM_CREATE"));

			if (data.containsKey("CRM_SOURCE"))
				req.setCrmSource(data.get("CRM_SOURCE"));

			if (data.containsKey("CRM_ENTITY_TYPE"))
				req.setCrmEntityType(data.get("CRM_ENTITY_TYPE"));

			if (data.containsKey("CRM_ENTITY_ID"))
				req.setCrmEntityId(validateInt(data, "CRM_ENTITY_ID"));

			if (data.containsKey("SHOW"))
				req.setShow(validateInt(data, "SHOW"));

			if (data.containsKey("CALL_LIST_ID"))
				req.setCallListId(validateInt(data, "CALL_LIST_ID"));

			if (data.containsKey("LINE_NUMBER"))
				req.setLineNumber(data.get("LINE_NUMBER"));

			getEventChain().putInContext(req.exec().getResult());
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}
}
