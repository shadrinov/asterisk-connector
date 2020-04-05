package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;

import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

public class FunctionChannel extends Function {
	private String channelId;
	private String expr;

	public FunctionChannel(EventDispatcher eventDispatcher, String channelId, String expr) {
		super(eventDispatcher);

		this.channelId = channelId;
		this.expr = expr;
	}

	@Override
	public String eval() throws IOException, BitrixLocalException {
		EventDispatcher eventDispatcher = getEventDispatcher();
		EventChain eventChain = eventDispatcher.getEventChain(channelId);

		if (eventChain != null)
			return new Expression(eventDispatcher, eventChain, expr).eval();
		else
			throw new BitrixLocalException(String.format("Unable to find reference channel: %s", channelId));
	}
}
