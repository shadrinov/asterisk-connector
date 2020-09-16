package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.actions.DBPut;
import ru.ntechs.ami.responses.Response;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class MethodAsteriskAmiDBPut extends Method {
	public static final String NAME = "asterisk.ami.dbput";

	public MethodAsteriskAmiDBPut(ScriptFactory scriptFactory, MessageChain eventChain, ConnectorAction action,
			MessageNode node) {
		super(scriptFactory, eventChain, action, node);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams(), false);
		HashMap<String, Scalar> fields = evaluate(getAction().getFields(), false);

		log.info("source: {}, params: {}, fields: {}", NAME, getAction().getParams(), getAction().getFields());
		log.info("evaluated: {}, params: {}, fields: {}", NAME, params, fields);

		DBPut dbPut = new DBPut(getMessage().getAMI());

		if ((params.containsKey("Family")) && !params.get("Family").isEmpty()) {
			dbPut.setFamily(params.get("Family").asString());

			if ((params.containsKey("Key")) && !params.get("Key").isEmpty()) {
				dbPut.setKey(params.get("Key").asString());

				if ((params.containsKey("Val")) && !params.get("Val").isEmpty()) {
					dbPut.setVal(params.get("Val").asString());

					dbPut.submit();
					Response response = dbPut.waitForResponse(15000);

					log.info("{} result: {}", NAME, (response != null) ? response.getMessage() : null);
				}
				else
					log.info("skipping DBPut due to \"Val\" argument is not defined");
			}
			else
				log.info("skipping DBPut due to \"Key\" argument is not defined");
		}
		else
			log.info("skipping DBPut due to \"Family\" argument is not defined");
	}
}
