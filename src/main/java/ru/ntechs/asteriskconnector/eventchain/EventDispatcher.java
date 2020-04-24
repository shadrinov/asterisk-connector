package ru.ntechs.asteriskconnector.eventchain;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;
import ru.ntechs.asteriskconnector.scripting.ScriptFactory;

@Slf4j
@Component
public class EventDispatcher {
	final static int EVENT_LIFETIME = 1800;

	private ScriptFactory scriptFactory;
	private ConcurrentHashMap<String, EventChain> chainByUniqueId;
	private ConcurrentHashMap<String, String> uniqueIdByChannel;
	private EventChain unmappableEvents;
	private int tickCount;

	private ConnectorConfig config;

	public EventDispatcher(ScriptFactory scriptFactory, ConnectorConfig config) {
		this.scriptFactory = scriptFactory;
		this.chainByUniqueId = new ConcurrentHashMap<>();
		this.uniqueIdByChannel = new ConcurrentHashMap<>();
		this.unmappableEvents = new EventChain(this, scriptFactory, config.getRules());
		this.tickCount = 0;
		this.config = config;
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

				log.info("before garbage collection: {}", uniqueIdByChannel);
				log.info("before garbage collection: {}", chainByUniqueId);

				if (channel != null)
					uniqueIdByChannel.remove(channel);

				chainByUniqueId.remove(chainEntry.getKey());

				log.info("after garbage collection: {}", uniqueIdByChannel);
				log.info("after garbage collection: {}", chainByUniqueId);
			}
		}

		unmappableEvents.garbageCollect(tickCount - EVENT_LIFETIME);
	}
}
