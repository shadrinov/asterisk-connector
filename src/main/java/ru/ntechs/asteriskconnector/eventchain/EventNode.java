package ru.ntechs.asteriskconnector.eventchain;

import ru.ntechs.ami.Message;

public class EventNode {
	private Message message;
	private EventNode next;
	private EventNode head;
	private EventNode tail;
	private Integer ticks;

	public EventNode(Integer ticks, Message message) {
		super();

		this.message = message;
		this.next = null;
		this.head = this;
		this.tail = this;
		this.ticks = ticks;
	}

	public EventNode(Integer ticks, Message message, EventNode ancestor) {
		super();

		synchronized (ancestor.head) {
			this.message = message;
			this.next = null;
			this.head = ancestor.head;
			this.tail = null;
			this.ticks = ticks;

			head.tail.next = this;
			head.tail = this;
		}
	}

	public EventNode getHead() {
		return head;
	}

	public Message getMessage() {
		return message;
	}

	public EventNode getNext() {
		return next;
	}

	public EventNode getTail() {
		return tail;
	}

	public Integer getTicks() {
		return ticks;
	}
}
