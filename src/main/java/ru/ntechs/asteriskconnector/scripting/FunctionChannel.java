package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

public class FunctionChannel extends Function {
	private String channelId;
	private String expr;
	private ArrayList<Object> intermediateBeans;

	public FunctionChannel(ScriptFactory scriptFactory, ArrayList<String> params) throws BitrixLocalException {
		super(scriptFactory);

		if (params.size() != 2)
			throw new BitrixLocalException(String.format("Channel(%s) doesn't match prototype Channel(UniqueId, Expression)"));

		this.channelId = params.get(0);
		this.expr = params.get(1);
	}

	@Override
	public String eval() throws IOException, BitrixLocalException {
		ScriptFactory scriptFactory = getScriptFactory();
		EventDispatcher eventDispatcher = scriptFactory.getEventDispatcher();
		EventChain eventChain = eventDispatcher.getEventChain(channelId);

		if (eventChain != null) {
			Expression interpreter = new Expression(scriptFactory, eventChain, expr, getMessage());
			String result = interpreter.eval();
			intermediateBeans = interpreter.getIntermediateBeans();
			return result;
		}
		else
			throw new BitrixLocalException(String.format("Unable to find reference channel: %s", channelId));
	}

	@Override
	public ArrayList<Object> getIntermediateBeans() {
		return intermediateBeans;
	}
}
