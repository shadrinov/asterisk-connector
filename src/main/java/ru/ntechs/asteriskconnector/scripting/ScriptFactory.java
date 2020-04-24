package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallRegister;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallShow;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
@Component
public class ScriptFactory {
	private static final DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	@Autowired
	private BitrixAuth auth;

	public void buildScript(EventDispatcher eventDispatcher, EventChain eventChain, ConnectorRule rule) {
		if (rule != null) {
			List<ConnectorAction> actions = rule.getAction();

			if (actions != null) {
				for (ConnectorAction action : actions) {
					if (action.getMethod() != null) {
						switch (action.getMethod().toLowerCase()) {
							case ("telephony.externalcall.register"): registerExternalCall(eventDispatcher, eventChain, action); break;
							case ("telephony.externalcall.show"): showExternalCall(eventDispatcher, eventChain, action); break;
							case ("telephony.externalcall.hide"): hideExternalCall(eventDispatcher, eventChain, action); break;

							default:
								log.info("unsupported method: {}", action.getMethod());
								break;
						}
					}
					else
						log.info("unspecified rule.action.method for event sequence: {}", rule.getEvents());
				}
			}
			else
				log.info("undefined rule.action for event seqeunce: {}", rule.getEvents());
		}
	}

	private void registerExternalCall(EventDispatcher eventDispatcher, EventChain eventChain, ConnectorAction action) {
		HashMap<String, String> data = evaluateParameters(eventDispatcher, eventChain, action.getData());

		try {
			RestRequestExternalCallRegister req;

			log.info(action.getData().toString());
			log.info(data.toString());

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
				req = new RestRequestExternalCallRegister(auth, userId, phoneNumber, validateInt(data, "TYPE"));
			else
				req = new RestRequestExternalCallRegister(auth, userPhoneInner, phoneNumber, validateInt(data, "TYPE"));

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

			eventChain.putInContext(req.exec().getResult());
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	private void showExternalCall(EventDispatcher eventDispatcher, EventChain eventChain, ConnectorAction action) {
		HashMap<String, String> data = evaluateParameters(eventDispatcher, eventChain, action.getData());

		try {
			RestRequestExternalCallShow req;

			log.info(action.getData().toString());
			log.info(data.toString());

			ArrayList<ExternalCall> calls = eventChain.getFromContext(ExternalCall.class);

			String callId = data.get("CALL_ID");
			String userId = data.get("USER_ID");

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (userId == null)
				throw new BitrixLocalException("Required parameter is not defined: USER_ID");

			req = new RestRequestExternalCallShow(auth, userId, null);
			req.exec();
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	private void hideExternalCall(EventDispatcher eventDispatcher, EventChain eventChain, ConnectorAction action) {
		// TODO Auto-generated method stub

	}

	private HashMap<String, String> evaluateParameters(EventDispatcher eventDispatcher, EventChain eventChain, Map<String, String> template) {
		HashMap<String, String> params = new HashMap<>();

		try {
			for (String key : template.keySet()) {
				Expression test = new Expression(eventDispatcher, eventChain, template.get(key));
				params.put(key, test.eval());
			}
		} catch (IOException | BitrixLocalException e) {
			log.warn("Eror while evaluating expression: {}", e.getMessage());
		}

		return params;
	}

	private Integer validateInt(Map<String, String> data, String key) throws BitrixLocalException {
		String value = data.get(key);
		Integer result = null;

		if (value != null) {
			try {
				result = Integer.valueOf(value);
			} catch (NumberFormatException e) {
				throw new BitrixLocalException(String.format("Failed to validate '%s', %s: %s", key, e.getMessage(), value));
			}
		}

		return result;
	}

	private Date validateDate(Map<String, String> data, String key) throws BitrixLocalException {
		String value = data.get(key);
		Date result = null;

		if (value != null) {
			try {
				result = iso8601DateFormat.parse(value);
			} catch (ParseException e) {
				throw new BitrixLocalException(String.format("Failed to validate '%s', %s: %s", key, e.getMessage(), value));
			}
		}

		return result;
	}
}
