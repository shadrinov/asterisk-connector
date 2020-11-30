package ru.ntechs.asteriskconnector.scripting;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.actions.DBDel;
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
	public void exec() {
		log.info("source on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, getAction().getParams(), getAction().getFields());

		HashMap<String, Scalar> params = evaluate(getAction().getParams());
		HashMap<String, Scalar> fields = evaluate(getAction().getFields());

		log.info("evaluated on {}: {}, params: {}, fields: {}", getEventChain().getChannel(), NAME, params, fields);

		boolean isNullValue = false;
		DBPut dbPut = new DBPut(getMessage().getAMI());

		if ((fields.containsKey("Family")) && !fields.get("Family").isEmpty()) {
			dbPut.setFamily(fields.get("Family").asString());

			if ((fields.containsKey("Key")) && !fields.get("Key").isEmpty()) {
				isNullValue = true;
				dbPut.setKey(fields.get("Key").asString());

				if ((fields.containsKey("Val")) && !fields.get("Val").isEmpty()) {
					isNullValue = false;
					dbPut.setVal(fields.get("Val").asString());
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

		if (isNullValue) {
			Scalar deleteIfNull = params.get("DELETE_IF_NULL");

			if ((deleteIfNull != null) && (!deleteIfNull.isNull()) && (deleteIfNull.asString().equalsIgnoreCase("TRUE"))) {
				DBDel dbDel = new DBDel(getMessage().getAMI());

				dbDel.setFamily(dbPut.getFamily());
				dbDel.setKey(dbPut.getKey());
				dbDel.submit();
				Response response = dbDel.waitForResponse(15000);

				log.info("{} result: {}", NAME, (response != null) ? response.getMessage() : null);
			}
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
