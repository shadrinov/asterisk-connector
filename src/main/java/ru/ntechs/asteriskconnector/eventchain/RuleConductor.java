package ru.ntechs.asteriskconnector.eventchain;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.config.ConnectorEvent;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.scripting.Expression;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

@Slf4j
public class RuleConductor {
	private EventChain eventChain;
	private ScriptFactory scriptFactory;
	private ConnectorRule rule;
	private int progress;

	List<ConnectorEvent> eventNames;

	public RuleConductor(EventChain eventChain, ScriptFactory scriptFactory, ConnectorRule rule) {
		this.eventChain = eventChain;
		this.scriptFactory = scriptFactory;
		this.rule = rule;
		this.progress = 0;

		this.eventNames = (rule != null) ? rule.getEvents() : null;
	}

	public ConnectorRule getRule() {
		return rule;
	}

	public boolean check(EventNode node, String channel) {
		if ((rule == null) || (eventNames == null) || (eventNames.size() == 0))
			return false;

		int preProgress = progress;

		if (check(node, channel, progress)) {
			log.info("MATCH! Got {} on {}, executing action: {}", eventNames, channel, rule.getAction());
			return true;
		}
		else {
			if (preProgress != progress)
				if (progress != 0)
					log.info("PROGRESS! Got {} on {}, waiting for {}",
							eventNames.subList(0, progress), channel,
							eventNames.subList(progress, eventNames.size()));
				else
					log.info("RESET! Got {} on {} waiting for {}",
							node.getMessage().getName(), channel,
							eventNames.subList(progress, eventNames.size()));

			return false;
		}
	}

	private boolean check(EventNode node, String channel, int progress) {
		boolean result = false;

		ConnectorEvent event = eventNames.get(progress++);
		String eventName = event.getName();

		try {
			if (eventName.charAt(0) == '!') {
				if (!(eventName.substring(1).equalsIgnoreCase(node.getMessage().getName()) && checkConstraints(event, node)))
					result = (progress < eventNames.size()) ? check(node, channel, progress) : true;
				else
					this.progress = 0;
			}
			else {
				if (eventName.equalsIgnoreCase(node.getMessage().getName()) && checkConstraints(event, node)) {
					if (progress >= eventNames.size()) {
						progress = 0;
						result = true;
					}

					this.progress = progress;
				}
			}
		} catch (IOException | BitrixLocalException e) {
			log.info(String.format("failure during evaluation event constraints: %s", e.getMessage()));
			this.progress = 0;
			result = false;
		}

		return result;
	}

	private boolean checkConstraints(ConnectorEvent event, EventNode node) throws IOException, BitrixLocalException {
		HashMap<String, String> constraints = event.getConstraints();

		if (event.getConstraints() != null) {
			boolean result = true;

			for (Entry<String, String> entry : constraints.entrySet()) {
				String entryKey = entry.getKey();
				String entryValue = entry.getValue();

				if ((entryKey != null) && (entryValue != null)) {
					String messageAttr = node.getMessage().getAttribute(entryKey);

					Expression expr = new Expression(scriptFactory, eventChain, entryValue, node);
					entryValue = expr.eval().toString();

					if ((entryValue == null) && (messageAttr == null))
						continue;

					if ((entryValue == null) || (messageAttr == null) || (!messageAttr.equalsIgnoreCase(entryValue))) {
						result = false;
						break;
					}
				}
			}

			return result;
		}
		else
			return true;
	}
}
