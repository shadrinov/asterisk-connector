package ru.ntechs.asteriskconnector;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.ntechs.ami.AMI;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.eventchain.EventNode;
import ru.ntechs.asteriskconnector.scripting.Parser;

@Service
public class MainLoop {
	private final Logger log = LoggerFactory.getLogger(MainLoop.class);
	private ConcurrentHashMap<String, EventNode> channels = new ConcurrentHashMap<>();
	private Integer tickCount = 0;
	private Parser test;

	@Autowired
	private AMI ami;

	public void run() throws Exception {
//		try {
//			test = new Parser("asterisk-event-actions");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}

		ami.addHandler("Join", message -> { OnJoin(message); });
		ami.addHandler("Leave", message -> { OnLeave(message); });
		ami.addHandler("AgentCalled", message -> { onAgentCalled(message); });
		ami.addHandler("AgentRingNoAnswer", message -> { onAgentRingNoAnswer(message); });
		ami.addHandler("AgentConnect", message -> { onAgentConnect(message); });
		ami.addHandler("AgentComplete", message -> { onAgentComplete(message); });

		while (true) {
			ArrayList<EventNode> condemned = new ArrayList<>();

			log.info(String.format("chains count: %d, ticks: %d", channels.size(), tickCount));

			for (EventNode head : channels.values()) {
				ArrayList<String> eventList = new ArrayList<>();
				EventNode node = head;

				while (node != null) {
					eventList.add(String.format("%s (%d)", node.getMessage().getName(), node.getTicks()));
					node = node.getNext();
				}

				log.info(String.format("channel %s chain: %s", head.getMessage().getAttribute("Uniqueid"), String.join(", ", eventList)));

				if ((tickCount - head.getTail().getTicks()) > 600)
					condemned.add(head);
			}

			for (EventNode head : condemned) {
				log.info(String.format("removing chain %s...", head.getMessage().getAttribute("Uniqueid")));
				channels.remove(head.getMessage().getAttribute("Uniqueid"));
			}

			tickCount++;
			Thread.sleep(1000);
		}
	}

	private void enqueue(Message message) {
		String uniqueId = message.getAttribute("Uniqueid");
		log.info(String.format("enqueueing message to channel queue: %s", uniqueId));

		if (uniqueId != null) {
			EventNode eventChain = channels.get(uniqueId);

			if (eventChain == null)
				channels.put(uniqueId, new EventNode(tickCount, message));
			else
				new EventNode(tickCount, message, eventChain);
		}
	}

	private void onAgentCalled(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		enqueue(message);
	}

	private void onAgentComplete(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		enqueue(message);
	}

	private void onAgentConnect(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		enqueue(message);
	}

	private void onAgentRingNoAnswer(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		enqueue(message);
	}

	private void OnJoin(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		enqueue(message);
	}

	private void OnLeave(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		enqueue(message);
	}
}
