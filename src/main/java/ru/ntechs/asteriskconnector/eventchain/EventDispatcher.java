package ru.ntechs.asteriskconnector.eventchain;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

@Slf4j
@Component
public class EventDispatcher {
	final static int EVENT_LIFETIME = 1800;

	@Autowired
	private ScriptFactory scriptFactory;

	private ConnectorConfig config;

	private ConcurrentHashMap<String, EventChain> chainByUniqueId = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> uniqueIdByChannel = new ConcurrentHashMap<>();
	private EventChain unmappableEvents;
	private int tickCount = 0;

	public EventDispatcher(ConnectorConfig config) {
		this.config = config;
		this.unmappableEvents = new EventChain(this, scriptFactory, config.getRules());
	}

	public void dispatch(Message msg) {
		EventChain eventChain;

		String uniqueId = msg.getAttribute("Uniqueid");

		if (uniqueId != null) {
			eventChain = chainByUniqueId.get(uniqueId);

			if (eventChain == null) {
				eventChain = new EventChain(this, scriptFactory, config.getRules());
				chainByUniqueId.put(uniqueId, eventChain);
			}
		}
		else
			eventChain = unmappableEvents;

		eventChain.enqueue(tickCount, msg);
	}

	public String registerChannel(Message msg) {
		String channel = msg.getAttribute("Channel");
		String uniqueId = msg.getAttribute("Uniqueid");

		if ((channel == null) && (msg.getName().equalsIgnoreCase("AgentCalled")))
			channel = msg.getAttribute("ChannelCalling");

		if ((channel != null) && (uniqueId != null)) {
			if (!uniqueIdByChannel.contains(channel))
				uniqueIdByChannel.put(channel, uniqueId);

			return channel;
		}
		else
			return null;
	}

	public EventChain getEventChain(String channelId) {
		if (chainByUniqueId.containsKey(channelId))
			return chainByUniqueId.get(channelId);

		if (uniqueIdByChannel.containsKey(channelId)) {
			channelId = uniqueIdByChannel.get(channelId);

			if (chainByUniqueId.containsKey(channelId))
				return chainByUniqueId.get(channelId);
		}

		return null;
	}

	public void collectGarbage() {
		tickCount++;

		for (Entry<String, EventChain> chainEntry : chainByUniqueId.entrySet()) {
			EventChain eventChain = chainEntry.getValue();

			if ((tickCount - eventChain.getTailBirthTicks()) > EVENT_LIFETIME) {
				String channel = eventChain.getChannel();

				log.debug("before garbage collection: {}", uniqueIdByChannel);
				log.debug("before garbage collection: {}", chainByUniqueId);

				if (channel != null)
					uniqueIdByChannel.remove(channel);

				chainByUniqueId.remove(chainEntry.getKey());

				log.debug("after garbage collection: {}", uniqueIdByChannel);
				log.debug("after garbage collection: {}", chainByUniqueId);
			}
		}

		unmappableEvents.garbageCollect(tickCount - EVENT_LIFETIME);
	}
}
