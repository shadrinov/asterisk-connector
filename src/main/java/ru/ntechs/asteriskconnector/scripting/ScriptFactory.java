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
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageDispatcher;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
@Getter
@Component
public class ScriptFactory {
	@Autowired
	private BitrixAuth auth;

	@Autowired
	private MessageDispatcher eventDispatcher;

	@Autowired
	private BitrixTelephony bitrixTelephony;

	public void buildScript(MessageChain eventChain, ConnectorRule rule, MessageNode node) {
		if (rule == null)
			return;

		if (node == null) {
			log.info("internal error (bug): no final message specified");
			return;
		}

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
			case (MethodAsteriskAmiDBDel.NAME):
				new MethodAsteriskAmiDBDel(this, eventChain, action, node).exec();
				break;

			case (MethodAsteriskAmiDBDelTree.NAME):
				new MethodAsteriskAmiDBDelTree(this, eventChain, action, node).exec();
				break;

			case (MethodAsteriskAmiDBPut.NAME):
				new MethodAsteriskAmiDBPut(this, eventChain, action, node).exec();
				break;

			case (MethodAsteriskAmiSetvar.NAME):
				new MethodAsteriskAmiSetvar(this, eventChain, action, node).exec();
				break;

			case (MethodCrmLeadAdd.NAME):
				new MethodCrmLeadAdd(this, eventChain, action, node).exec();
				break;

			case (MethodExternalCallFinish.NAME):
				new MethodExternalCallFinish(this, eventChain, action, node).exec();
				break;

			case (MethodExternalCallHide.NAME):
				new MethodExternalCallHide(this, eventChain, action, node).exec();
				break;

			case (MethodExternalCallRegister.NAME):
				new MethodExternalCallRegister(this, eventChain, action, node).exec();
				break;

			case (MethodExternalCallShow.NAME):
				new MethodExternalCallShow(this, eventChain, action, node).exec();
				break;

			case (MethodExternalCallAttachRecord.NAME):
				new MethodExternalCallAttachRecord(this, eventChain, action, node).exec();
				break;

			default:
				log.info("unsupported method: {}", action.getMethod());
				break;
			}
		}
	}
}
