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

		this.message = message;
		this.ticks = birthTicks;
		this.next = null;
		this.prev = null;

		while (true) {
			synchronized (ancestor) {
				if (ancestor.next != null) {
					log.debug("ancestor EventNode is not tail... searching for tail...");
					ancestor = ancestor.next;
					continue;
				}

				ancestor.next = this;
				this.prev = ancestor;
				break;
			}
		}
	}

	public synchronized MessageNode split() {
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
		MessageNode candidate = this;

		while ((candidate != null) && (candidate.message != null)
				&& !candidate.message.getName().equalsIgnoreCase(name)) {
			candidate = candidate.prev;
		}

		return candidate;
	}

	public MessageNode findMessage(String name, HashMap<String, String> constraints) {
		if ((constraints != null) && !constraints.isEmpty()) {
			MessageNode candidate = this;

			while ((candidate != null) && (candidate = candidate.findMessage(name)) != null) {
				boolean match = true;

				for (Entry<String, String> entry : constraints.entrySet()) {
					if (entry.getKey() != null) {
						String value = candidate.message.getAttribute(entry.getKey());

						if (entry.getValue() != null) {
							if (value != null) {
								if (!value.equalsIgnoreCase(entry.getValue())) {
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
							if (value != null) {
								match = false;
								break;
							}
						}
					}
				}

				if (match)
					break;

				candidate = candidate.prev;
			}

			return candidate;
		}
		else
			return findMessage(name);
	}
}
