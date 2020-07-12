package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;
import java.util.function.BiConsumer;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmLeadAdd;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodCrmLeadAdd extends Method {
	public static final String NAME = "crm.lead.add";

	public MethodCrmLeadAdd(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams(), false);
		HashMap<String, Scalar> fields = evaluate(getAction().getFields(), false);

		log.info("source: {}, params: {}, fields: {}", NAME, getAction().getParams(), getAction().getFields());
		log.info("evaluated: {}, params: {}, fields: {}", NAME, params, fields);

		try {
			RestRequestCrmLeadAdd req = new RestRequestCrmLeadAdd(getAuth());

			if (params.containsKey("REGISTER_SONET_EVENT"))
				req.addParam("REGISTER_SONET_EVENT", params.get("REGISTER_SONET_EVENT").asString());

			fields.forEach(new BiConsumer<String, Scalar>() {
				@Override
				public void accept(String name, Scalar val) {
					if (name.equals("PHONE"))
						req.addFieldPhone(val.asString());
					else
						req.addField(name, val.asString());
				}
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
