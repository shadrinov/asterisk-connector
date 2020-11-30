package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.actions.DBDelTree;
import ru.ntechs.ami.responses.Response;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodAsteriskAmiDBDelTree extends Method {
	public static final String NAME = "asterisk.ami.dbdeltree";

	public MethodAsteriskAmiDBDelTree(ScriptFactory scriptFactory, MessageChain eventChain, ConnectorAction action,
			MessageNode node) {
		super(scriptFactory, eventChain, action, node);
	}

	@Override
	public void exec() {
		log.info("source on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, getAction().getParams(), getAction().getFields());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());
		HashMap<String, Scalar> fields = evaluate(getAction().getFields());

		log.info("evaluated on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, params, fields);

		DBDelTree dbDelTree = new DBDelTree(getMessage().getAMI());

		if (fields.containsKey("Family"))
			dbDelTree.setFamily(fields.get("Family").asString());

		if (fields.containsKey("Key"))
			dbDelTree.setKey(fields.get("Key").asString());

		dbDelTree.submit();
		Response response = dbDelTree.waitForResponse(15000);

		log.info("{} result: {}", NAME, (response != null) ? response.getMessage() : null);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
