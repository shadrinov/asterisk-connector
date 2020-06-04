package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

public class FunctionChannel extends Function {
	public static final String NAME    = "Channel";
	public static final String LC_NAME = "channel";

	private String channelId;
	private String expr;
	private ArrayList<Object> intermediateBeans;

	public FunctionChannel(ScriptFactory scriptFactory, ArrayList<Scalar> params) throws BitrixLocalException {
		super(scriptFactory, params);
		init(params);
	}

	public FunctionChannel(ScriptFactory scriptFactory, Message message, ArrayList<Scalar> params) throws BitrixLocalException {
		super(scriptFactory, message, params);
		init(params);
	}

	private void init(ArrayList<Scalar> params) throws BitrixLocalException {
		if (params.size() != 2)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s(UniqueId, Expression)",
					toString(), NAME));

		this.channelId = params.get(0).asString();
		this.expr = params.get(1).asString();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		ScriptFactory scriptFactory = getScriptFactory();
		EventDispatcher eventDispatcher = scriptFactory.getEventDispatcher();
		EventChain eventChain = eventDispatcher.getEventChain(channelId);

		if (eventChain != null) {
			Expression expr = new Expression(scriptFactory, eventChain, this.expr, getMessage());
			Scalar result = expr.eval();
			intermediateBeans = expr.getIntermediateBeans();
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
