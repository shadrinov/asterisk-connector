package ru.ntechs.asteriskconnector.eventchain;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorEvent;

@Slf4j
public class EventChain {
	private EventNode tailEvent;
	private EventNode headEvent;
	private List<ConnectorEvent> rules;
	private ArrayList<Integer> rulesProgress;

	EventChain(List<ConnectorEvent> rules) {
		super();

		this.tailEvent = null;
		this.headEvent = null;
		this.rules = rules;
		this.rulesProgress = new ArrayList<>(rules.size());

		for (int index = 0; index < rules.size(); index++)
			rulesProgress.add(0);
	}

	public void enqueue(int birthTicks, Message message) {
		if (tailEvent == null) {
			headEvent = new EventNode(birthTicks, message);
			tailEvent = headEvent;
		}
		else
			tailEvent = new EventNode(birthTicks, message, tailEvent);

		for (int index = 0; index < rules.size(); index++) {
			List<String> eventNames = rules.get(index).getEvents();

			log.info(String.format("Expected sequence: %s", eventNames.toString()));
			log.info(String.format("Expected message: \"%s\" at %d", eventNames.get(rulesProgress.get(index)), rulesProgress.get(index)));

			if (eventNames.get(rulesProgress.get(index)).equalsIgnoreCase(message.getName())) {
				rulesProgress.set(index, rulesProgress.get(index) + 1);

				if (rulesProgress.get(index) >= rules.get(index).getEvents().size()) {
					log.info(String.format("Result: MATCH! Executing action: %s: %s", rules.get(index).getAction().getType(), rules.get(index).getAction().getUrl()));
					rulesProgress.set(index, 0);
				}
				else
					log.info(String.format("Result: PROGRESS! Expected message: \"%s\" at %d", eventNames.get(rulesProgress.get(index)), rulesProgress.get(index)));
			}
		}

		log.info(String.format("Progress: %s", rulesProgress.toString()));
	}

	public boolean isEmpty() {
		return (tailEvent == null);
	}

	public int getTailBirthTicks() {
		if (tailEvent != null)
			return tailEvent.getTicks();
		else
			return 0;
	}

	public String getUniqueId() {
		return (tailEvent != null) ? tailEvent.getMessage().getAttribute("Uniqueid") : null;
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
