package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmLeadAdd;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodCrmLeadAdd extends Method {

	public MethodCrmLeadAdd(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams());

		try {
			RestRequestCrmLeadAdd req = new RestRequestCrmLeadAdd(getAuth());

			req.exec();
			throw new BitrixLocalException("not implemented");
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

}
