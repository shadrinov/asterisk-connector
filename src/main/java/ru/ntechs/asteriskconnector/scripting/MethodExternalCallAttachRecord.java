package ru.ntechs.asteriskconnector.scripting;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

public class MethodExternalCallAttachRecord extends Method {

	public MethodExternalCallAttachRecord(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub
	}

}
