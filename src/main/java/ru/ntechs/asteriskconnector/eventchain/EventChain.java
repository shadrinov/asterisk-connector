package ru.ntechs.asteriskconnector.eventchain;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
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
	}

	public synchronized void enqueue(int birthTicks, Message message) {
		Object lock = new Object();
		ConnectorRule matched = null;

		synchronized (lock) {
			if (tailEvent == null) {
				headEvent = new EventNode(birthTicks, message);
				tailEvent = headEvent;
			}
			else
				tailEvent = new EventNode(birthTicks, message, tailEvent);

			if (channel == null) {
				channel = message.getAttribute("Channel");

				if ((channel == null) && (message.getName().equalsIgnoreCase("AgentCalled")))
					channel = message.getAttribute("ChannelCalling");
			}

			for (int index = 0; index < rules.size(); index++) {
				List<String> eventNames = rules.get(index).getEvents();

				if (eventNames.get(rulesProgress.get(index)).equalsIgnoreCase(message.getName())) {
					rulesProgress.set(index, rulesProgress.get(index) + 1);

					if (rulesProgress.get(index) >= rules.get(index).getEvents().size()) {
						log.info("Progress: {}, Result: MATCH! Got {}, executing action: {}",
								rulesProgress.toString(),
								eventNames.get(rulesProgress.get(index) - 1),
								rules.get(index).getAction().toString());

						matched = rules.get(index);
						rulesProgress.set(index, 0);
					}
					else
						log.info("Progress: {}, Result: PROGRESS! Got {}, waiting for: \"{}\" at {}",
								rulesProgress.toString(),
								eventNames.get(rulesProgress.get(index) - 1),
								eventNames.get(rulesProgress.get(index)),
								rulesProgress.get(index));
				}
			}
		}

		if (matched != null)
			scriptFactory.buildScript(eventDispatcher, this, matched);
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
		return null;
	}

	public EventNode getHead() {
		return headEvent;
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
