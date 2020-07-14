package ru.ntechs.asteriskconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ntechs.ami.AMI;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Component
public class MainLoop {
	@Autowired
	private EventDispatcher eventDispatcher;

	@Autowired
	private AMI ami;

	public void run() throws Exception {
		ami.addHandler(message -> { eventDispatcher.dispatch(message); });

		while (true) {
			eventDispatcher.collectGarbage();
			Thread.sleep(1000);
		}
	}
}
