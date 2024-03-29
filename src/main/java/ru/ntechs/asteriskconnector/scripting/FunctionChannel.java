package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageDispatcher;

@Slf4j
public class FunctionChannel extends Function {
	public static final String NAME    = "Channel";
	public static final String LC_NAME = "channel";

	private String channelId;
	private String expressionString;
	private ArrayList<Object> intermediateBeans;

	public FunctionChannel(Expression expression, ArrayList<Scalar> params) throws BitrixLocalException {
		super(expression, params);

		if (params.size() != 2)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s(channel, expression)",
					toString(), NAME));

		this.channelId = params.get(0).asString();
		this.expressionString = params.get(1).asString();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		MessageDispatcher eventDispatcher = getEventDispatcher();

		MessageChain eventChain = eventDispatcher.getEventChain(channelId);

		if (eventChain != null) {
			Expression expr = new Expression(eventChain, this.expressionString);
			Scalar result = expr.eval();
			intermediateBeans = expr.getIntermediateBeans();
			return result;
		}
		else {
			log.info("Warning: unable to find referenced channel: {}", channelId);
			return new ScalarString("<undef>");
		}
	}

	@Override
	public ArrayList<Object> getIntermediateBeans() {
		return intermediateBeans;
	}
}
