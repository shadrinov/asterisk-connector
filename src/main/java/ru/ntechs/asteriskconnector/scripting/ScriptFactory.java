package ru.ntechs.asteriskconnector.scripting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixTelephony;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.config.ConnectorRule;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Slf4j
@Getter
@Component
public class ScriptFactory {
	@Autowired
	private BitrixAuth auth;

	@Autowired
	private EventDispatcher eventDispatcher;

	@Autowired
	private BitrixTelephony bitrixTelephony;

	public void buildScript(EventChain eventChain, ConnectorRule rule) {
		if (rule == null)
			return;

		List<ConnectorAction> actions = rule.getAction();

		if (actions == null) {
			log.info("undefined rule.action for event seqeunce: {}", rule.getEvents());
			return;
		}

		for (ConnectorAction action : actions) {
			if (action == null) {
				log.info("unspecified rule.action for event sequence: {}", rule.getEvents());
				continue;
			}

			if (action.getMethod() == null) {
				log.info("unspecified rule.action.method for event sequence: {}", rule.getEvents());
				continue;
			}

			switch (action.getMethod().toLowerCase()) {
			case ("telephony.externalcall.register"):
				new MethodRegisterExternalCall(this, eventChain, action).exec();
				break;

			case ("telephony.externalcall.show"):
				new MethodShowExternalCall(this, eventChain, action).exec();
				break;

			case ("telephony.externalcall.hide"):
				new MethodHideExternalCall(this, eventChain, action).exec();
				break;

			default:
				log.info("unsupported method: {}", action.getMethod());
				break;
			}
		}
	}
}
