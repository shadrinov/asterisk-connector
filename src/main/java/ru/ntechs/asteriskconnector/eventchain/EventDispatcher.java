package ru.ntechs.asteriskconnector.eventchain;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

@Component
public class EventDispatcher {
	final static int EVENT_LIFETIME = 600;

	private ScriptFactory scriptFactory;
	private ConcurrentHashMap<String, EventChain> chainsByUniqueId;
	private ConcurrentHashMap<String, String> uniqueIdByChannel;
	private EventChain unmappableEvents;
	private int tickCount;

	private ConnectorConfig config;

	public EventDispatcher(ScriptFactory scriptFactory, ConnectorConfig config) {
		this.scriptFactory = scriptFactory;
		this.chainsByUniqueId = new ConcurrentHashMap<>();
		this.uniqueIdByChannel = new ConcurrentHashMap<>();
		this.unmappableEvents = new EventChain(this, scriptFactory, config.getRules());
		this.tickCount = 0;
		this.config = config;
	}

	public void dispatch(Message msg) {
		EventChain eventChain;

		String uniqueId = msg.getAttribute("Uniqueid");

		if (uniqueId != null) {
			eventChain = chainsByUniqueId.get(uniqueId);

			if (eventChain == null) {
				eventChain = new EventChain(this, scriptFactory, config.getRules());
				chainsByUniqueId.put(uniqueId, eventChain);
			}

			String channel = msg.getAttribute("Channel");

			if ((channel != null) && !uniqueIdByChannel.contains(channel))
				uniqueIdByChannel.put(channel, uniqueId);
		}
		else
			eventChain = unmappableEvents;

		eventChain.enqueue(tickCount, msg);
	}

	public EventChain getEventChain(String channelId) {
		if (chainsByUniqueId.containsKey(channelId))
			return chainsByUniqueId.get(channelId);

		if (uniqueIdByChannel.containsKey(channelId)) {
			channelId = uniqueIdByChannel.get(channelId);

			if (chainsByUniqueId.containsKey(channelId))
				return chainsByUniqueId.get(channelId);
		}

		return null;
	}

	public void collectGarbage() {
		tickCount++;

		for (Entry<String, EventChain> chainEntry : chainsByUniqueId.entrySet()) {
			if ((tickCount - chainEntry.getValue().getTailBirthTicks()) > EVENT_LIFETIME) {
				chainsByUniqueId.remove(chainEntry.getKey());
				uniqueIdByChannel.remove(chainEntry.getValue().getChannel());
			}
		}

		unmappableEvents.garbageCollect(tickCount - EVENT_LIFETIME);
	}
}
