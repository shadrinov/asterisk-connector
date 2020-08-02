package ru.ntechs.asteriskconnector.eventchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

public class EventChain {
	private EventNode tailEvent;
	private EventNode headEvent;

	private String channel;
	private EventDispatcher eventDispatcher;
	private ScriptFactory scriptFactory;

	private ArrayList<RuleConductor> conductors;

	private ChainContext context;

	EventChain(EventDispatcher eventDispatcher, ScriptFactory scriptFactory, List<ConnectorRule> rules) {
		super();

		this.tailEvent = null;
		this.headEvent = null;

		this.channel = null;
		this.eventDispatcher = eventDispatcher;
		this.scriptFactory = scriptFactory;

		this.conductors = new ArrayList<>(rules.size());

		for (ConnectorRule rule : rules)
			conductors.add(new RuleConductor(this, scriptFactory, rule));

		this.context = new ChainContext();
	}

	public synchronized void enqueue(int birthTicks, Message message) {
		if (tailEvent == null) {
			headEvent = new EventNode(birthTicks, message);
			tailEvent = headEvent;
		}
		else
			tailEvent = new EventNode(birthTicks, message, tailEvent);

		checkRules(tailEvent);
	}

	private synchronized void checkRules(EventNode node) {
		if (channel == null)
			channel = eventDispatcher.registerChannel(node.getMessage());

		for (RuleConductor rc : conductors)
			if (rc.check(node, channel))
				scriptFactory.buildScript(this, rc.getRule(), node);
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

	public EventNode getTail() {
		return tailEvent;
	}

	public ChainContext getContext() {
		return context;
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

	public EventNode findMessage(Message before, String name) {
		return (tailEvent != null) ? tailEvent.findMessage(before, name) : null;
	}

	public EventNode findMessage(Message before, String name, HashMap<String,String> constraints) {
		return (tailEvent != null) ? tailEvent.findMessage(before, name, constraints) : null;
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
