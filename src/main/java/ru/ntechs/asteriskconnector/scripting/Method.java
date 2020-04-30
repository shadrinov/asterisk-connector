package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
public abstract class Method {
	private static final DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	private ScriptFactory scriptFactory;
	private EventChain eventChain;
	private ConnectorAction action;

	public Method(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action) {
		super();

		this.scriptFactory = scriptFactory;
		this.eventChain = eventChain;
		this.action = action;
	}

	public EventDispatcher getEventDispatcher() {
		return scriptFactory.getEventDispatcher();
	}

	public EventChain getEventChain() {
		return eventChain;
	}

	public ConnectorAction getAction() {
		return action;
	}

	public BitrixAuth getAuth() {
		return scriptFactory.getAuth();
	}

	protected HashMap<String, String> evaluate(EventDispatcher eventDispatcher, EventChain eventChain, Map<String, String> template) {
		HashMap<String, String> params = new HashMap<>();

		if (template != null)
			try {
				for (String key : template.keySet()) {
					Expression test = new Expression(eventDispatcher, eventChain, template.get(key));
					params.put(key, test.eval());
				}
			} catch (IOException | BitrixLocalException e) {
				log.warn("Eror while evaluating expression: {}", e.getMessage());
			}

		return params;
	}

	protected Integer validateInt(Map<String, String> data, String key) throws BitrixLocalException {
		String value = data.get(key);
		Integer result = null;

		if (value != null) {
			try {
				result = Integer.valueOf(value);
			} catch (NumberFormatException e) {
				throw new BitrixLocalException(String.format("Failed to validate '%s', %s: %s", key, e.getMessage(), value));
			}
		}

		return result;
	}

	protected Date validateDate(Map<String, String> data, String key) throws BitrixLocalException {
		String value = data.get(key);
		Date result = null;

		if (value != null) {
			try {
				result = iso8601DateFormat.parse(value);
			} catch (ParseException e) {
				throw new BitrixLocalException(String.format("Failed to validate '%s', %s: %s", key, e.getMessage(), value));
			}
		}

		return result;
	}

	public abstract void exec();
}
