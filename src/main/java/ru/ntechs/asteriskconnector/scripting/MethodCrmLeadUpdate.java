package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmLeadUpdate;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodCrmLeadUpdate extends Method {
	public static final String NAME = RestRequestCrmLeadUpdate.METHOD;

	public MethodCrmLeadUpdate(ScriptFactory scriptFactory, MessageChain eventChain, ConnectorAction action, MessageNode node) {
		super(scriptFactory, eventChain, action, node);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void exec() {
		log.info("source on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, getAction().getParams(), getAction().getFields());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());
		HashMap<String, Scalar> fields = evaluate(getAction().getFields());

		log.info("evaluated on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, params, fields);

		try {
			RestRequestCrmLeadUpdate req = new RestRequestCrmLeadUpdate(getAuth());

			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);
			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;

			if (firstCall != null) {
				Long leadId = firstCall.getCrmCreatedLead();

				if (leadId == null) {
					log.info("suppressed method call, no created lead detected through context: {}", firstCall);
					return;
				}

				req.setId(leadId);
			}

			fields.forEach((name, val) -> {
				if ((val != null) && (!val.isEmpty())) {
					req.addField(name, val.asString());
				}
			});

			if (params.containsKey("REGISTER_SONET_EVENT"))
				req.addParam("REGISTER_SONET_EVENT", params.get("REGISTER_SONET_EVENT").asString());

			if (!req.getFields().isEmpty())
				req.exec();
		} catch (BitrixRestApiException e) {
			log.info(e.getMessage());
		}
	}
}
