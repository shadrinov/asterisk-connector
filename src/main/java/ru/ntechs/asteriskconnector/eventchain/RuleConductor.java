package ru.ntechs.asteriskconnector.eventchain;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorRule;

@Slf4j
public class RuleConductor {
	private ConnectorRule rule;
	private int progress;

	List<String> eventNames;

	public RuleConductor(ConnectorRule rule) {
		this.rule = rule;
		this.progress = 0;

		this.eventNames = (rule != null) ? rule.getEvents() : null;
	}

	public ConnectorRule getRule() {
		return rule;
	}

	public boolean check (Message message, String channel) {
		if ((rule == null) || (eventNames == null) || (eventNames.size() == 0))
			return false;

		int preProgress = progress;

		if (check(message, channel, progress)) {
			log.info("MATCH! Got {} on {}, executing action: {}", eventNames, channel, rule.getAction());
			return true;
		}
		else {
			if (preProgress != progress)
				log.info("{}! Got {} on {}, waiting for {}",
						(progress == 0) ? "RESET" : "PROGRESS",
						eventNames.subList(0, progress), channel,
						eventNames.subList(progress, eventNames.size()));

			return false;
		}
	}

	private boolean check(Message message, String channel, int progress) {
		boolean result = false;
		String eventName = eventNames.get(progress++);

		if (eventName.charAt(0) == '!') {
			if (!eventName.substring(1).equalsIgnoreCase(message.getName()))
				result = (progress < eventNames.size()) ? check(message, channel, progress) : true;
			else
				this.progress = 0;
		}
		else {
			if (eventName.equalsIgnoreCase(message.getName())) {
				if (progress >= eventNames.size()) {
					progress = 0;
					result = true;
				}

				this.progress = progress;
			}
		}

		return result;
	}
}
