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

	public MethodAsteriskAmiDBDel(MessageChain eventChain, ConnectorAction action, MessageNode node) {
		super(eventChain, action, node);
	}

	@Override
	public void exec() {
		log.info("source on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, getAction().getParams(), getAction().getFields());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());
		HashMap<String, Scalar> fields = evaluate(getAction().getFields());

		log.info("evaluated on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, params, fields);

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
