package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageDispatcher;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Getter
public abstract class Function {
	private Expression expression;
	private ArrayList<Scalar> params;

	public Function(Expression expression, ArrayList<Scalar> params) {
		super();

		this.expression = expression;
		this.params = params;
	}

	@Override
	public String toString() {
		ArrayList<String> params = new ArrayList<>();

		for (Scalar entry : this.params)
			params.add(entry.asString());

		return String.format("%s(%s)", getName(), String.join(", ", params));
	}

	public MessageNode getContextMessage() {
		return (expression != null) ? expression.getContextMessage() : null;
	}

	public ScriptFactory getScriptFactory() {
		return (expression != null) ? expression.getScriptFactory() : null;
	}

	public MessageChain getEventChain() {
		return (expression != null) ? expression.getEventChain() : null;
	}

	public MessageDispatcher getEventDispatcher() {
		ScriptFactory scriptFactory = getScriptFactory();
		return (scriptFactory != null) ? scriptFactory.getEventDispatcher() : null;
	}

	public abstract String getName();
	public abstract Scalar eval() throws IOException, BitrixLocalException;
	public abstract ArrayList<? extends Object> getIntermediateBeans();
}
