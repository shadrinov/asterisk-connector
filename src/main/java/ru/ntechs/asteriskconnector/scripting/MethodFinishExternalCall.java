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
		HashMap<String, Scalar> data = evaluateActionData();

		try {
			log.info("source: {}", (getAction().getData() != null) ? getAction().getData().toString() : "null");
			log.info("evaluated: {}", data.toString());

			String callId = null;
			Long userId = null;

			ArrayList<User> users = getContext().get(User.class);
			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);

			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;
			if ((firstCall != null) && firstCall.isFinished()) {
				log.info("suppressed method call, call is finished: {}, {}", getAction().getMethod(), firstCall.toString());
				return;
			}

			if (data.containsKey("CALL_ID"))
				callId = data.get("CALL_ID").asString();

			if (data.containsKey("USER_ID"))
				userId = data.get("USER_ID").asLong();

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

			if (data.containsKey("DURATION"))
				req.setDuration(data.get("DURATION").asInteger());

			if (data.containsKey("COST"))
				req.setCost(data.get("COST").asDouble());

			if (data.containsKey("COST_CURRENCY"))
				req.setCostCurrency(data.get("COST_CURRENCY").asString());

			if (data.containsKey("STATUS_CODE"))
				req.setStatusCode(data.get("STATUS_CODE").asInteger());

			if (data.containsKey("FAILED_REASON"))
				req.setFailedReason(data.get("FAILED_REASON").asString());

			if (data.containsKey("RECORD_URL"))
				req.setRecordURL(data.get("RECORD_URL").asString());

			if (data.containsKey("VOTE"))
				req.setVote(data.get("VOTE").asInteger());

			if (data.containsKey("ADD_TO_CHAT"))
				req.setAddToChat(data.get("ADD_TO_CHAT").asInteger());

			req.exec();
			getContext().remove(users);

			if (firstCall != null)
				firstCall.setFinished(true);
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}
}
