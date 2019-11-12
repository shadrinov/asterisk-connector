package ru.ntechs.asteriskconnector.scripting;

public class Action {
	public static final int TYPE_REST = 1;

	private Event eventFirst;
	private Event eventLast;
	private Byte type;
	private Byte method;
	private String url;
	private String data;

	public void queueEvent(Event event) {
		if (eventFirst == null)
			eventFirst = event;

		if (eventLast != null)
			eventLast.tie(event);

		eventLast = event;
	}
}
