package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallShow;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodExternalCallShow extends Method {
	public static final String NAME = RestRequestExternalCallShow.METHOD;

	public MethodExternalCallShow(MessageChain eventChain, ConnectorAction action, MessageNode node) {
		super(eventChain, action, node);
	}

	@Override
	public void exec() {
		log.info("source on {}: {}, params: {}", getEventChain().getChannel(), NAME, getAction().getParams());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());

		log.info("evaluated on {}: {}, params: {}", getEventChain().getChannel(), NAME, params);

		try {
			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);

			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;
			if ((firstCall != null) && firstCall.isFinished()) {
				log.info("suppressed method call, call is finished: {}, {}", getAction().getMethod(), firstCall.toString());
				return;
			}

			String callId = null;
			ArrayList<Integer> userIds = new ArrayList<>();

			if (params.containsKey("CALL_ID"))
				callId = params.get("CALL_ID").asString();

			if (params.containsKey("USER_ID"))
				userIds.add(params.get("USER_ID").asInteger());

			if ((callId == null) && !calls.isEmpty() && (firstCall != null))
				callId = firstCall.getCallId();

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (userIds.isEmpty())
				throw new BitrixLocalException("Required parameter is not defined: USER_ID");

			RestRequestExternalCallShow req = new RestRequestExternalCallShow(getAuth(), callId, userIds);
			req.exec();

			for (User user : findIntermediateBeans(User.class))
				getContext().put(user);
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
