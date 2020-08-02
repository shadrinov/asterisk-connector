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
import ru.ntechs.asteriskconnector.eventchain.EventNode;

@Slf4j
public abstract class Method {
	private static final DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	private ScriptFactory scriptFactory;
	private EventChain eventChain;
	private ConnectorAction action;
	private EventNode contextNode;

	private ArrayList<Object> intermediateBeans;

	public Method(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, EventNode node) {
		super();

		this.scriptFactory = scriptFactory;
		this.eventChain = eventChain;
		this.action = action;
		this.contextNode = node;
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
		return (contextNode != null) ? contextNode.getMessage() : null;
	}

	public BitrixAuth getAuth() {
		return scriptFactory.getAuth();
	}

	public ChainContext getContext() {
		return eventChain.getContext();
	}

	protected HashMap<String, Scalar> evaluate(Map<String, String> template) {
		return evaluate(template, true);
	}

	protected HashMap<String, Scalar> evaluate(Map<String, String> template, boolean doLog) {
		HashMap<String, Scalar> result = new HashMap<>();

		if (template != null) {
			if (doLog)
				log.info("source: {}, params: {}", getName(), template.toString());

			for (String key : template.keySet()) {
				try {
					Expression expr = new Expression(scriptFactory, eventChain, template.get(key), contextNode);
					Scalar evaluated = expr.eval();
					intermediateBeans = expr.getIntermediateBeans();
					result.put(key, evaluated);
				} catch (IOException | BitrixLocalException e) {
					log.warn("Expression evaluation failure: {}", e.getMessage());
				}
			}

			if (doLog)
				log.info("evaluated: {}, params: {}", getName(), result.toString());
		}
		else
			if (doLog)
				log.info("source: {}, no params", getName());

		return result;
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

	public abstract String getName();
	public abstract void exec();
}
