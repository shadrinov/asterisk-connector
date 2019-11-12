package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;

public class Script {
	ArrayList<Event> chains = new ArrayList<>();
	Event lastEvent;

	Script() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addEvent(String name) {
		if (lastEvent == null) {
			lastEvent = new Event(name);
			chains.add(lastEvent);
		}
		else
			lastEvent = lastEvent.tie(new Event(name));
	}

	public void addChain() {
		lastEvent = null;
	}
}
