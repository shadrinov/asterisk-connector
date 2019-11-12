package ru.ntechs.asteriskconnector.config;

import java.util.List;
import java.util.Map;

public class ConnectorEvent {
	private List<String> events;
	private ConnectorAction action;
	private Map<Object,Object> data;

	public List<String> getEvents() {
		return events;
	}

	public void setEvents(List<String> events) {
		this.events = events;
	}

	public ConnectorAction getAction() {
		return action;
	}

	public void setAction(ConnectorAction action) {
		this.action = action;
	}

	public Map<Object, Object> getData() {
		return data;
	}

	public void setData(Map<Object, Object> data) {
		this.data = data;
	}
}
