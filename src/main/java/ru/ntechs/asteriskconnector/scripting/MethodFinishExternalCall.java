package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallFinish;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodFinishExternalCall extends Method {

	public MethodFinishExternalCall(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		HashMap<String, String> data = evaluate(getEventDispatcher(), getEventChain(), getAction().getData());

		try {
			log.info("source: {}", (getAction().getData() != null) ? getAction().getData().toString() : "null");
			log.info("evaluated: {}", data.toString());

			String callId = null;
			Integer userId = null;

			ArrayList<User> users = getContext().get(User.class);
			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);

			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;
			if ((firstCall != null) && firstCall.isFinished()) {
				log.info("suppressed method call, call is finished: {}, {}", getAction().getMethod(), firstCall.toString());
				return;
			}

			if (data.containsKey("CALL_ID"))
				callId = data.get("CALL_ID");

			if (data.containsKey("USER_ID"))
				userId = validateInt(data, "USER_ID");

			if ((callId == null) && !calls.isEmpty() && (firstCall != null))
				callId = firstCall.getCallId();

			if ((userId == null) && !users.isEmpty()) {
				User firstUser = users.get(users.size() - 1);

				if (firstUser != null)
					userId = firstUser.getId();
			}

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (userId == null)
				throw new BitrixLocalException("Required parameter is not defined: USER_ID");

			RestRequestExternalCallFinish req = new RestRequestExternalCallFinish(getAuth(), callId, userId, 0);
			req.exec();

			getContext().remove(users);

			if (firstCall != null)
				firstCall.setFinished(true);
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}
}
