package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallHide;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodExternalCallHide extends Method {
	public static final String NAME = RestRequestExternalCallHide.METHOD;

	public MethodExternalCallHide(MessageChain eventChain, ConnectorAction action, MessageNode node) {
		super(eventChain, action, node);
	}

	@Override
	public void exec() {
		log.info("source on {}: {}, params: {}", getEventChain().getChannel(), NAME, getAction().getParams());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());

		log.info("evaluated on {}: {}, params: {}", getEventChain().getChannel(), NAME, params);

		try {
			String callId = null;
			ArrayList<Long> userIds = new ArrayList<>();

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
				userIds.add(params.get("USER_ID").asLong());

			if ((callId == null) && !calls.isEmpty() && (firstCall != null))
				callId = firstCall.getCallId();

			if (userIds.isEmpty() && !users.isEmpty())
				for (User entity : users)
					userIds.add(entity.getId());

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (userIds.isEmpty())
				throw new BitrixLocalException("Required parameter is not defined: USER_ID");

			RestRequestExternalCallHide req = new RestRequestExternalCallHide(getAuth(), callId, userIds);
			req.exec();

			getContext().remove(users);
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
