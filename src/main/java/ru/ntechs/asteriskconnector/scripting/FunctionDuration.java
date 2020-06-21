package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventNode;

public class FunctionDuration extends Function {
	public static final String NAME    = "Duration";
	public static final String LC_NAME = "duration";

	private String firstEvent;
	private String lastEvent;

	public FunctionDuration(Expression expression, ArrayList<Scalar> params) throws BitrixLocalException {
		super(expression, params);

		if (params.size() > 2)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s([firstEvent[, lastEvent]])",
					toString(), NAME));

		this.firstEvent = (params.size() > 0) ? params.get(0).asString() : null;
		this.lastEvent = (params.size() > 1) ? params.get(1).asString() : null;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		EventChain eventChain = getEventChain();
		Message messageCurrent = getMessage();
		Long firstMessageMillis, lastMessageMillis;

		if (eventChain.isEmpty())
			return new ScalarInteger("Duration", 0l);

		EventNode eventNode = (firstEvent != null) ?
				eventChain.findMessage(messageCurrent, firstEvent) :
					eventChain.getHead();

		if (eventNode != null)
			firstMessageMillis = eventNode.getMillis();
		else
			throw new BitrixLocalException(String.format("AMI event (firstEvent) '%s' not found in current event chain", firstEvent));

		eventNode = (lastEvent != null) ?
				eventChain.findMessage(messageCurrent, lastEvent) :
					eventChain.getTail();

		if (eventNode != null)
			lastMessageMillis = eventNode.getMillis();
		else
			throw new BitrixLocalException(String.format("AMI event (lastEvent) '%s' not found in current event chain", firstEvent));

		return new ScalarInteger("Duration", Math.abs(lastMessageMillis - firstMessageMillis) / 1000000l);
	}

	@Override
	public ArrayList<? extends Object> getIntermediateBeans() {
		// TODO Auto-generated method stub
		return null;
	}
}
