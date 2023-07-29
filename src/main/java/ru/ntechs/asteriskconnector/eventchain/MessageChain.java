package ru.ntechs.asteriskconnector.eventchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

public class MessageChain {
	private MessageNode tailEvent;
	private MessageNode headEvent;

	private String channel;
	private MessageDispatcher eventDispatcher;

	private ArrayList<RuleConductor> conductors;

	private ChainContext context;

	private static ExecutorService scriptExecutionThreadPool = Executors.newCachedThreadPool();
	private final Object scriptExecutionLock = new Object();

	MessageChain(MessageDispatcher eventDispatcher, List<ConnectorRule> rules) {
		super();

		this.tailEvent = null;
		this.headEvent = null;

		this.channel = null;
		this.eventDispatcher = eventDispatcher;

		this.conductors = new ArrayList<>(rules.size());

		for (ConnectorRule rule : rules)
			conductors.add(new RuleConductor(this, eventDispatcher, rule));

		this.context = new ChainContext();
	}

	public void enqueue(int birthTicks, Message message) {
		MessageNode currentTail;
		ArrayList<ConnectorRule> rulesToExecute = new ArrayList<>();

		synchronized (this) {
			if (tailEvent == null) {
				headEvent = new MessageNode(birthTicks, message);
				tailEvent = headEvent;
			}
			else
				tailEvent = new MessageNode(birthTicks, message, tailEvent);

			currentTail = tailEvent;

			if (channel == null)
				channel = eventDispatcher.registerChannel(tailEvent.getMessage());

			for (RuleConductor rc : conductors)
				if (rc.check(tailEvent, channel))
					rulesToExecute.add(rc.getRule());
		}

		scriptExecutionThreadPool.execute(() -> {
			try {
				ScriptFactory scriptFactory = eventDispatcher.getScriptFactory();
				synchronized (scriptExecutionLock) {
					for (ConnectorRule rule : rulesToExecute)
						scriptFactory.buildScript(this, rule, currentTail);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public boolean isEmpty() {
		return (tailEvent == null);
	}

	public MessageDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public synchronized int getTailBirthTicks() {
		return (tailEvent != null) ? tailEvent.getTicks() : 0;
	}

	public synchronized String getUniqueId() {
		return (tailEvent != null) ? tailEvent.getMessage().getAttribute("Uniqueid") : null;
	}

	public String getChannel() {
		return channel;
	}

	public MessageNode getHead() {
		return headEvent;
	}

	public MessageNode getTail() {
		return tailEvent;
	}

	public ChainContext getContext() {
		return context;
	}

	public synchronized void garbageCollect(int age) {
		while ((headEvent != null) && (headEvent.getTicks() < age))
			headEvent = headEvent.split();

		if (headEvent == null)
			tailEvent = null;
	}

	public synchronized MessageNode findMessage(String name) {
		return (tailEvent != null) ? tailEvent.findMessage(name) : null;
	}

	public synchronized MessageNode findMessage(String name, HashMap<String,String> constraints) {
		return (tailEvent != null) ? tailEvent.findMessage(name, constraints) : null;
	}

	@Override
	public synchronized String toString() {
		ArrayList<String> eventList = new ArrayList<>();
		MessageNode node = getHead();

		while (node != null) {
			eventList.add(String.format("%s (%d)", node.getMessage().getName(), node.getTicks()));
			node = node.getNext();
		}

		String uniqueId = getUniqueId();
		return String.format("channel %s chain: %s", (uniqueId != null) ? uniqueId : "<no uniqueId>", String.join(", ", eventList));
	}
}
