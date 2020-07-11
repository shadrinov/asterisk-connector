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
		HashMap<String, Scalar> params = evaluate(getAction().getParams());

		try {
			RestRequestExternalCallRegister req;

			String userPhoneInner = (params.containsKey("USER_PHONE_INNER")) ? params.get("USER_PHONE_INNER").asString() : null;
			Long userId = (params.containsKey("USER_ID")) ? params.get("USER_ID").asLong() : null;
			String phoneNumber = (params.containsKey("PHONE_NUMBER")) ? params.get("PHONE_NUMBER").asString() : null;
			String type = (params.containsKey("TYPE")) ? params.get("TYPE").asString() : null;

			if ((userPhoneInner == null) && (userId == null))
				throw new BitrixLocalException("Required parameter is not defined: USER_PHONE_INNER or USER_ID");

			if (phoneNumber == null)
				throw new BitrixLocalException("Required parameter is not defined: PHONE_NUMBER");

			if (type == null)
				throw new BitrixLocalException("Required parameter is not defined: TYPE");

			if (userId != null)
				req = new RestRequestExternalCallRegister(getAuth(), userId, phoneNumber, params.get("TYPE").asShort());
			else
				req = new RestRequestExternalCallRegister(getAuth(), userPhoneInner, phoneNumber, params.get("TYPE").asShort());

			if (params.containsKey("CALL_START_DATE"))
				req.setCallStartDate(params.get("CALL_START_DATE").asDate());

			if (params.containsKey("CRM_CREATE"))
				req.setCrmCreate(params.get("CRM_CREATE").asInteger());

			if (params.containsKey("CRM_SOURCE"))
				req.setCrmSource(params.get("CRM_SOURCE").asString());

			if (params.containsKey("CRM_ENTITY_TYPE"))
				req.setCrmEntityType(params.get("CRM_ENTITY_TYPE").asString());

			if (params.containsKey("CRM_ENTITY_ID"))
				req.setCrmEntityId(params.get("CRM_ENTITY_ID").asInteger());

			if (params.containsKey("SHOW"))
				req.setShow(params.get("SHOW").asInteger());

			if (params.containsKey("CALL_LIST_ID"))
				req.setCallListId(params.get("CALL_LIST_ID").asInteger());

			if (params.containsKey("LINE_NUMBER"))
				req.setLineNumber(params.get("LINE_NUMBER").asString());

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
