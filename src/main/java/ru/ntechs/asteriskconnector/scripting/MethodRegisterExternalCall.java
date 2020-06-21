package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallRegister;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodRegisterExternalCall extends Method {

	public MethodRegisterExternalCall(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> data = evaluateActionData();

		try {
			RestRequestExternalCallRegister req;

			log.info("source: {}", (getAction().getData() != null) ? getAction().getData().toString() : "null");
			log.info("evaluated: {}", data.toString());

			String userPhoneInner = (data.containsKey("USER_PHONE_INNER")) ? data.get("USER_PHONE_INNER").asString() : null;
			Long userId = (data.containsKey("USER_ID")) ? data.get("USER_ID").asLong() : null;
			String phoneNumber = (data.containsKey("PHONE_NUMBER")) ? data.get("PHONE_NUMBER").asString() : null;
			String type = (data.containsKey("TYPE")) ? data.get("TYPE").asString() : null;

			if ((userPhoneInner == null) && (userId == null))
				throw new BitrixLocalException("Required parameter is not defined: USER_PHONE_INNER or USER_ID");

			if (phoneNumber == null)
				throw new BitrixLocalException("Required parameter is not defined: PHONE_NUMBER");

			if (type == null)
				throw new BitrixLocalException("Required parameter is not defined: TYPE");

			if (userId != null)
				req = new RestRequestExternalCallRegister(getAuth(), userId, phoneNumber, data.get("TYPE").asShort());
			else
				req = new RestRequestExternalCallRegister(getAuth(), userPhoneInner, phoneNumber, data.get("TYPE").asShort());

			if (data.containsKey("CALL_START_DATE"))
				req.setCallStartDate(data.get("CALL_START_DATE").asDate());

			if (data.containsKey("CRM_CREATE"))
				req.setCrmCreate(data.get("CRM_CREATE").asInteger());

			if (data.containsKey("CRM_SOURCE"))
				req.setCrmSource(data.get("CRM_SOURCE").asString());

			if (data.containsKey("CRM_ENTITY_TYPE"))
				req.setCrmEntityType(data.get("CRM_ENTITY_TYPE").asString());

			if (data.containsKey("CRM_ENTITY_ID"))
				req.setCrmEntityId(data.get("CRM_ENTITY_ID").asInteger());

			if (data.containsKey("SHOW"))
				req.setShow(data.get("SHOW").asInteger());

			if (data.containsKey("CALL_LIST_ID"))
				req.setCallListId(data.get("CALL_LIST_ID").asInteger());

			if (data.containsKey("LINE_NUMBER"))
				req.setLineNumber(data.get("LINE_NUMBER").asString());

			if (req.getCallStartDate() == null)
				req.setCallStartDate(Calendar.getInstance().getTime());

			ExternalCall call = req.exec().getResult();
			getContext().put(call);

			if ((req.getShow() == null) || req.getShow() != 0) {
				ArrayList<User> users = findIntermediateBeans(User.class);

				for (User entry : users)
					getContext().put(entry);
			}
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}
}
