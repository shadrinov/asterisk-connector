package ru.ntechs.asteriskconnector.eventchain;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;

@Slf4j
public class EventNode {
	private Message message;
	private EventNode next;
	private EventNode prev;
	private int ticks;

	public EventNode(int birthTicks, Message message) {
		super();

		this.message = message;
		this.ticks = birthTicks;
		this.next = null;
		this.prev = null;
	}

	public EventNode(int birthTicks, Message message, EventNode ancestor) {
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

	public Message getMessage() {
		return message;
	}

	public EventNode getNext() {
		return next;
	}

	public EventNode getPrev() {
		return prev;
	}

	public Integer getTicks() {
		return ticks;
	}

	public EventNode split() {
		EventNode newHead = next;

		if (next != null) {
			synchronized (next) {
				next.prev = null;
				next = null;
			}
		}

		return newHead;
	}

	public EventNode findMessage(String name) {
		EventNode candidate = this;

		while ((candidate != null) && (candidate.getMessage() != null)
				&& !candidate.getMessage().getName().equalsIgnoreCase(name)) {
			candidate = candidate.getPrev();
		}

		return (candidate != null) ? candidate : null;
	}
}
