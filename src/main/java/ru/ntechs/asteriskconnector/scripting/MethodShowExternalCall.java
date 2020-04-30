package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.catalina.User;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallShow;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodShowExternalCall extends Method {

	public MethodShowExternalCall(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action) {
		super(scriptFactory, eventChain, action);
	}

	@Override
	public void exec() {
		HashMap<String, String> data = evaluate(getEventDispatcher(), getEventChain(), getAction().getData());

		try {
			RestRequestExternalCallShow req;

			log.info("source: {}", (getAction().getData() != null) ? getAction().getData().toString() : "null");
			log.info("evaluated: {}", data.toString());

			ArrayList<User> user = getEventChain().getFromContext(User.class);
			ArrayList<ExternalCall> calls = getEventChain().getFromContext(ExternalCall.class);

			String callId = null;
			ArrayList<Integer> userIds = new ArrayList<>();

			if (data.containsKey("CALL_ID"))
				callId = data.get("CALL_ID");

			if (data.containsKey("USER_ID"))
				userIds.add(validateInt(data, "USER_ID"));

			if ((callId == null) && !calls.isEmpty()) {
				ExternalCall firstCall = calls.get(0);

				if (firstCall != null)
					callId = firstCall.getCallId();
			}

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (userIds.isEmpty())
				throw new BitrixLocalException("Required parameter is not defined: USER_ID");

			req = new RestRequestExternalCallShow(getAuth(), callId, userIds);
			req.exec();
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}
}
