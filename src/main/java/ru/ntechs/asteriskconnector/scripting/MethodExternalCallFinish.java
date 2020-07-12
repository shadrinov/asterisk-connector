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
public class MethodExternalCallFinish extends Method {
	public static final String NAME = RestRequestExternalCallFinish.METHOD;

	public MethodExternalCallFinish(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams());

		try {
			String callId = null;
			Long userId = null;

			ArrayList<User> users = getContext().get(User.class);
			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);

			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;
			if ((firstCall != null) && firstCall.isFinished()) {
				log.info("suppressed method call, call is finished: {}, {}", getAction().getMethod(), firstCall.toString());
				return;
			}

			if (params.containsKey("CALL_ID"))
				callId = params.get("CALL_ID").asString();

			if (params.containsKey("USER_ID"))
				userId = params.get("USER_ID").asLong();

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

			if (params.containsKey("DURATION"))
				req.setDuration(params.get("DURATION").asInteger());

			if (params.containsKey("COST"))
				req.setCost(params.get("COST").asDouble());

			if (params.containsKey("COST_CURRENCY"))
				req.setCostCurrency(params.get("COST_CURRENCY").asString());

			if (params.containsKey("STATUS_CODE"))
				req.setStatusCode(params.get("STATUS_CODE").asInteger());

			if (params.containsKey("FAILED_REASON"))
				req.setFailedReason(params.get("FAILED_REASON").asString());

			if (params.containsKey("RECORD_URL"))
				req.setRecordURL(params.get("RECORD_URL").asString());

			if (params.containsKey("VOTE"))
				req.setVote(params.get("VOTE").asInteger());

			if (params.containsKey("ADD_TO_CHAT"))
				req.setAddToChat(params.get("ADD_TO_CHAT").asInteger());

			req.exec();
			getContext().remove(users);

			if (firstCall != null)
				firstCall.setFinished(true);
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
