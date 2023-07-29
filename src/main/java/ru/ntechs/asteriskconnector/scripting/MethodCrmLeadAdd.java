package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmLeadAdd;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodCrmLeadAdd extends Method {
	public static final String NAME = RestRequestCrmLeadAdd.METHOD;

	public MethodCrmLeadAdd(MessageChain eventChain, ConnectorAction action, MessageNode node) {
		super(eventChain, action, node);
	}

	@Override
	public void exec() {
		log.info("source on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, getAction().getParams(), getAction().getFields());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());
		HashMap<String, Scalar> fields = evaluate(getAction().getFields());

		log.info("evaluated on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, params, fields);

		try {
			RestRequestCrmLeadAdd req = new RestRequestCrmLeadAdd(getAuth());

			if (params.containsKey("REGISTER_SONET_EVENT"))
				req.addParam("REGISTER_SONET_EVENT", params.get("REGISTER_SONET_EVENT").asString());

			fields.forEach((name, val) -> {
				if (name.equals("PHONE"))
					req.addFieldPhone(val.asString());
				else
					req.addField(name, val.asString());
			});

			req.exec();
		} catch (BitrixRestApiException e) {
			log.info(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
