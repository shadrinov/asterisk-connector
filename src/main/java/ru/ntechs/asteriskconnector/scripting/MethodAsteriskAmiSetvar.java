package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.actions.Setvar;
import ru.ntechs.ami.responses.Response;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodAsteriskAmiSetvar extends Method {
	public static final String NAME = "asterisk.ami.setvar";

	public MethodAsteriskAmiSetvar(ScriptFactory scriptFactory, MessageChain eventChain, ConnectorAction action,
			MessageNode node) {
		super(scriptFactory, eventChain, action, node);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams(), false);
		HashMap<String, Scalar> fields = evaluate(getAction().getFields(), false);

		log.info("source: {}, params: {}, fields: {}", NAME, getAction().getParams(), getAction().getFields());
		log.info("evaluated: {}, params: {}, fields: {}", NAME, params, fields);

		Setvar setVar = new Setvar(getMessage().getAMI());
		setVar.setChannel(getEventChain().getChannel());

		if (fields.containsKey("Variable"))
			setVar.setVariable(fields.get("Variable").asString());

		if (fields.containsKey("Value"))
			setVar.setValue(fields.get("Value").asString());

		setVar.submit();
		Response response = setVar.waitForResponse(15000);

		log.info("{} result: {}", NAME, (response != null) ? response.getMessage() : null);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
