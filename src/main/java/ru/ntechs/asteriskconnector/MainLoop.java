package ru.ntechs.asteriskconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.AMI;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
@Component
public class MainLoop {
	@Autowired
	private EventDispatcher eventDispatcher;

	@Autowired
	private AMI ami;

	public void run() throws Exception {
		ami.addHandler("Join", message -> { eventDispatcher.dispatch(message); });
		ami.addHandler("Leave", message -> { eventDispatcher.dispatch(message); });
		ami.addHandler("AgentCalled", message -> { eventDispatcher.dispatch(message); });
		ami.addHandler("AgentRingNoAnswer", message -> { eventDispatcher.dispatch(message); });
		ami.addHandler("AgentConnect", message -> { eventDispatcher.dispatch(message); });
		ami.addHandler("AgentComplete", message -> { eventDispatcher.dispatch(message); });

		while (true) {
			eventDispatcher.collectGarbage();
			Thread.sleep(1000);
		}
	}
}
