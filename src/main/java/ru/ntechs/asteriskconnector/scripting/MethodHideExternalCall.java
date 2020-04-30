package ru.ntechs.asteriskconnector.scripting;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodHideExternalCall extends Method {

	public MethodHideExternalCall(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action) {
		super(scriptFactory, eventChain, action);
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub

	}

}
