package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallRegister;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
@Component
public class ScriptFactory {
	@Autowired
	private BitrixAuth auth;

	public void buildScript(EventDispatcher eventDispatcher, EventChain eventChain, ConnectorRule rule) {
		if (rule != null) {
			if (rule.getAction() != null) {
				ConnectorAction action = rule.getAction();

				if (action.getMethod() != null) {
					switch (action.getMethod().toLowerCase()) {
						case ("telephony.externalcall.register"): registerExternalCall(eventDispatcher, eventChain, rule); break;
						default:
							log.info("unsupported method: {}", action.getMethod());
							break;
					}
				}
				else
					log.info("unspecified rule.action.method for event sequence: {}", rule.getEvents());
			}
			else
				log.info("undefined rule.action for event seqeunce: {}", rule.getEvents());
		}
	}

	private void registerExternalCall(EventDispatcher eventDispatcher, EventChain eventChain, ConnectorRule rule) {
		log.info("building script for method: {}", rule.getAction().getMethod());

		HashMap<String, String> data = transformTemplate(eventDispatcher, eventChain, rule.getData());

		try {
			log.info(rule.getData().toString());
			log.info(data.toString());

			String userPhoneInner = data.get("USER_PHONE_INNER");
			String phoneNumber = data.get("PHONE_NUMBER");
			String type = data.get("TYPE");

			if (userPhoneInner == null)
				throw new BitrixLocalException("Required parameter is not defined: USER_PHONE_INNER");

			if (phoneNumber == null)
				throw new BitrixLocalException("Required parameter is not defined: PHONE_NUMBER");

			if (type == null)
				throw new BitrixLocalException("Required parameter is not defined: TYPE");

			ExternalCall call = new RestRequestExternalCallRegister(auth, userPhoneInner, phoneNumber, Integer.valueOf(type)).exec().getResult();
//			String call = new RestRequestExternalCallRegister(auth, "11", "89605332222", RestRequestExternalCallRegister.TYPE_INCOMING).exec_debug();
			log.info(call.toString());
		} catch (BitrixRestApiException | BitrixLocalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private HashMap<String, String> transformTemplate(EventDispatcher eventDispatcher, EventChain eventChain, Map<String, String> template) {
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
}
