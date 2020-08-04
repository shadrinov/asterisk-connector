package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class FunctionDuration extends Function {
	public static final String NAME    = "Duration";
	public static final String LC_NAME = "duration";

	private Scalar firstMessage;
	private Scalar lastMessage;

	public FunctionDuration(Expression expression, ArrayList<Scalar> params) throws BitrixLocalException {
		super(expression, params);

		if (params.size() > 2)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s([event1[, event2]])",
					toString(), NAME));

		this.firstMessage = (params.size() > 0) ? params.get(0) : null;
		this.lastMessage = (params.size() > 1) ? params.get(1) : null;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		MessageChain eventChain = getEventChain();
		MessageNode contextNode = getContextMessage();
		Long firstMessageMillis = null, lastMessageMillis = null;
		MessageNode messageNode;

		if (eventChain.isEmpty())
			return new ScalarInteger("Duration", 0l);

		messageNode = ((firstMessage != null) && !firstMessage.isNull()) ?
				((firstMessage instanceof ScalarMessage) ?
						((ScalarMessage)firstMessage).getMessage() :
							contextNode.findMessage(firstMessage.asString())) :
								eventChain.getHead();

		if (messageNode != null)
			firstMessageMillis = messageNode.getMillis();
		else
			log.info("Warning: AMI event (firstEvent) '{}' not found in current event chain", firstMessage);

		messageNode = ((lastMessage != null) && !lastMessage.isNull()) ?
				((lastMessage instanceof ScalarMessage) ?
						((ScalarMessage)lastMessage).getMessage() :
							contextNode.findMessage(lastMessage.asString())) :
								eventChain.getTail();

		if (messageNode != null)
			lastMessageMillis = messageNode.getMillis();
		else
			log.info("Warning: AMI event (lastEvent) '{}' not found in current event chain", lastMessage);

		if ((firstMessageMillis != null) && (lastMessageMillis != null))
			return new ScalarInteger("Duration", Math.abs(lastMessageMillis - firstMessageMillis) / 1000l);
		else
			return new ScalarInteger("Duration");
	}

	@Override
	public ArrayList<? extends Object> getIntermediateBeans() {
		// TODO Auto-generated method stub
		return null;
	}
}
