package ru.ntechs.asteriskconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.AMI;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
@Component
public class MainLoop {
	@Autowired
	private EventDispatcher eventDispatcher;

	@Autowired
	private AMI ami;

	public void run() throws Exception {
		ami.addHandler("Join", message -> { OnJoin(message); });
		ami.addHandler("Leave", message -> { OnLeave(message); });
		ami.addHandler("AgentCalled", message -> { onAgentCalled(message); });
		ami.addHandler("AgentRingNoAnswer", message -> { onAgentRingNoAnswer(message); });
		ami.addHandler("AgentConnect", message -> { onAgentConnect(message); });
		ami.addHandler("AgentComplete", message -> { onAgentComplete(message); });

		while (true) {
			eventDispatcher.collectGarbage();
			Thread.sleep(1000);
		}
	}

	private void onAgentCalled(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		eventDispatcher.dispatch(message);
	}

	private void onAgentComplete(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		eventDispatcher.dispatch(message);
	}

	private void onAgentConnect(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		eventDispatcher.dispatch(message);
	}

	private void onAgentRingNoAnswer(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		eventDispatcher.dispatch(message);
	}

	private void OnJoin(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		eventDispatcher.dispatch(message);
	}

	private void OnLeave(Message message) {
		log.info(String.format("Plain Message: \"%s: %s\"", message.getType(), message.getName()));
		eventDispatcher.dispatch(message);
	}
}
