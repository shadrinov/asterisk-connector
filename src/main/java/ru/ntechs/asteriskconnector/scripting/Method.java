package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.ChainContext;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
public abstract class Method {
	private static final DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	private ScriptFactory scriptFactory;
	private EventChain eventChain;
	private ConnectorAction action;
	private Message message;

	private ArrayList<Object> intermediateBeans;

	public Method(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super();

		this.scriptFactory = scriptFactory;
		this.eventChain = eventChain;
		this.action = action;
		this.message = message;
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

	public Message getMessage() {
		return message;
	}

	public BitrixAuth getAuth() {
		return scriptFactory.getAuth();
	}

	public ChainContext getContext() {
		return eventChain.getContext();
	}

	protected HashMap<String, Scalar> evaluateActionData() {
		Map<String, String> template = action.getData();
		HashMap<String, Scalar> params = new HashMap<>();

		if (template != null)
			for (String key : template.keySet()) {
				try {
					Expression expr = new Expression(scriptFactory, eventChain, template.get(key), message);
					Scalar result = expr.eval();
					intermediateBeans = expr.getIntermediateBeans();
					params.put(key, result);
				} catch (IOException | BitrixLocalException e) {
					log.warn("Expression evaluation failure: {}", e.getMessage());
				}
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
				throw new BitrixLocalException(String.format("Failed to validate integer '%s' for key '%s': %s", value, key, e.getMessage()));
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

	protected <T> ArrayList<T> findIntermediateBeans(Class<T> type) {
		ArrayList<T> result = new ArrayList<>();

		for  (Object obj : intermediateBeans)
			if (type.isInstance(obj))
				result.add(type.cast(obj));

		return result;
	}

	public abstract void exec();
}
