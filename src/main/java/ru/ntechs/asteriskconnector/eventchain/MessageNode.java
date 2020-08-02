package ru.ntechs.asteriskconnector.eventchain;

import java.util.HashMap;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;

@Slf4j
@Getter
public class MessageNode {
	private Message message;
	private MessageNode next;
	private MessageNode prev;

	private final int ticks;
	private final long millis = System.currentTimeMillis();

	public MessageNode(int birthTicks, Message message) {
		super();

		this.message = message;
		this.ticks = birthTicks;
		this.next = null;
		this.prev = null;
	}

	public MessageNode(int birthTicks, Message message, MessageNode ancestor) {
		super();

		while (ancestor.next != null) {
			log.warn("ancestor EventNode is not tail... searching for tail...");
			ancestor = ancestor.next;
		}

		synchronized (ancestor) {
			this.message = message;
			this.next = null;
			this.prev = ancestor;
			this.ticks = birthTicks;

			ancestor.next = this;
		}
	}

	public MessageNode split() {
		MessageNode newHead = next;

		if (next != null) {
			synchronized (next) {
				next.prev = null;
				next = null;
			}
		}

		return newHead;
	}

	public MessageNode findMessage(String name) {
		return findMessage(null, name);
	}

	public MessageNode findMessage(String name, HashMap<String, String> constraints) {
		return findMessage(null, name, constraints);
	}

	public MessageNode findMessage(Message before, String name) {
		MessageNode candidate = this;

		if (before != null) {
			while ((candidate != null) && (candidate.message != null)
					&& (candidate.message != before)) {
				candidate = candidate.prev;
			}
		}

		while ((candidate != null) && (candidate.message != null)
				&& !candidate.message.getName().equalsIgnoreCase(name)) {
			candidate = candidate.prev;
		}

		return candidate;
	}

	public MessageNode findMessage(Message before, String name, HashMap<String, String> constraints) {
		MessageNode candidate = findMessage(before, name);

		if ((constraints == null) || constraints.isEmpty())
			return candidate;

		while (candidate != null) {
			if (candidate.message == null) {
				candidate = candidate.findMessage(null, name);
				continue;
			}

			boolean match = true;

			for (Entry<String, String> entry : constraints.entrySet()) {
				if (entry.getKey() != null) {
					String msgAttrValue = candidate.message.getAttribute(entry.getKey());

					if (entry.getValue() != null) {
						if (msgAttrValue != null) {
							if (!msgAttrValue.equalsIgnoreCase(entry.getValue())) {
								match = false;
								break;
							}
						}
						else {
							match = false;
							break;
						}
					}
					else {
						if (msgAttrValue != null) {
							match = false;
							break;
						}
					}
				}
			}

			if (match)
				break;

			candidate = candidate.findMessage(null, name);
		}

		return candidate;
	}
}
