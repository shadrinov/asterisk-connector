package ru.ntechs.asteriskconnector.eventchain;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

@Slf4j
public class EventChain {
	private EventNode tailEvent;
	private EventNode headEvent;

	private String channel;
	private EventDispatcher eventDispatcher;
	private ScriptFactory scriptFactory;

	private List<ConnectorRule> rules;
	private ArrayList<Integer> rulesProgress;

	private ArrayList<Object> context;

	EventChain(EventDispatcher eventDispatcher, ScriptFactory scriptFactory, List<ConnectorRule> rules) {
		super();

		this.tailEvent = null;
		this.headEvent = null;

		this.channel = null;
		this.eventDispatcher = eventDispatcher;
		this.scriptFactory = scriptFactory;

		this.rules = rules;
		this.rulesProgress = new ArrayList<>(rules.size());

		for (int index = 0; index < rules.size(); index++)
			rulesProgress.add(0);

		this.context = new ArrayList<>();
	}

	public synchronized void enqueue(int birthTicks, Message message) {
		Object lock = new Object();
		ArrayList<ConnectorRule> matched = new ArrayList<>();

		synchronized (lock) {
			if (tailEvent == null) {
				headEvent = new EventNode(birthTicks, message);
				tailEvent = headEvent;
			}
			else
				tailEvent = new EventNode(birthTicks, message, tailEvent);

			if (channel == null)
				channel = eventDispatcher.registerChannel(message);

			for (int index = 0; index < rules.size(); index++) {
				ConnectorRule rule = rules.get(index);
				if (rule == null)
					continue;

				List<String> eventNames = rule.getEvents();
				if (eventNames == null)
					continue;

				Integer ruleProgress = rulesProgress.get(index);

				if (eventNames.get(ruleProgress).equalsIgnoreCase(message.getName())) {
					rulesProgress.set(index, ++ruleProgress);

					if (ruleProgress >= eventNames.size()) {
						List<ConnectorAction> action = rule.getAction();

						log.info("Progress: {}, Result: MATCH! Got {}, executing action: {}",
								rulesProgress.toString(),
								eventNames.get(ruleProgress - 1),
								(action != null) ? action.toString() : "<null>");

						rulesProgress.set(index, 0);
						matched.add(rule);
					}
					else {
						log.info("Progress: {}, Result: PROGRESS! Got {}, waiting for: \"{}\" at {}",
								rulesProgress.toString(),
								eventNames.get(ruleProgress - 1),
								eventNames.get(ruleProgress),
								ruleProgress);
					}
				}
			}
		}

		for (ConnectorRule rule : matched)
			scriptFactory.buildScript(this, rule);
	}

	public boolean isEmpty() {
		return (tailEvent == null);
	}

	public int getTailBirthTicks() {
		return (tailEvent != null) ? tailEvent.getTicks() : 0;
	}

	public String getUniqueId() {
		return (tailEvent != null) ? tailEvent.getMessage().getAttribute("Uniqueid") : null;
	}

	public String getChannel() {
		return channel;
	}

	public EventNode getHead() {
		return headEvent;
	}

	public void putInContext(Object obj) {
		context.add(obj);
	}

	public <T> ArrayList<T> getFromContext(Class<T> requestedData) {
		ArrayList<T> result = new ArrayList<>();

		for  (Object obj : context)
			if (requestedData.isInstance(obj))
				result.add(requestedData.cast(obj));

		return result;
	}

	public void garbageCollect(int age) {
		while ((headEvent != null) && (headEvent.getTicks() < age))
			headEvent = headEvent.split();

		if (headEvent == null)
			tailEvent = null;
	}

	public EventNode findMessage(String name) {
		return (tailEvent != null) ? tailEvent.findMessage(name) : null;
	}

	@Override
	public String toString() {
		ArrayList<String> eventList = new ArrayList<>();
		EventNode node = getHead();

		while (node != null) {
			eventList.add(String.format("%s (%d)", node.getMessage().getName(), node.getTicks()));
			node = node.getNext();
		}

		String uniqueId = getUniqueId();
		return String.format("channel %s chain: %s", (uniqueId != null) ? uniqueId : "<no uniqueId>", String.join(", ", eventList));
	}
}
