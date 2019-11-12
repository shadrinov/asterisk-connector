package ru.ntechs.asteriskconnector.scripting;

public class Event {
	private String name;
	private Event next;

	Event(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Event tie(Event event) {
		next = event;
		return next;
	}

	public Event getNext() {
		return next;
	}
}
