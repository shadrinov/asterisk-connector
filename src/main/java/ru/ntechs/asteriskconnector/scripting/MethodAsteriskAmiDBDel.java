package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.actions.DBDel;
import ru.ntechs.ami.responses.Response;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodAsteriskAmiDBDel extends Method {
	public static final String NAME = "asterisk.ami.dbdel";

	public MethodAsteriskAmiDBDel(ScriptFactory scriptFactory, MessageChain eventChain, ConnectorAction action,
			MessageNode node) {
		super(scriptFactory, eventChain, action, node);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams(), false);
		HashMap<String, Scalar> fields = evaluate(getAction().getFields(), false);

		log.info("source: {}, params: {}, fields: {}", NAME, getAction().getParams(), getAction().getFields());
		log.info("evaluated: {}, params: {}, fields: {}", NAME, params, fields);

		DBDel dbDel = new DBDel(getMessage().getAMI());

		if (fields.containsKey("Family"))
			dbDel.setFamily(fields.get("Family").asString());

		if (fields.containsKey("Key"))
			dbDel.setKey(fields.get("Key").asString());

		dbDel.submit();
		Response response = dbDel.waitForResponse(15000);

		log.info("{} result: {}", NAME, (response != null) ? response.getMessage() : null);
	}

	@Override
	public String getName() {
		return NAME;
	}
}
