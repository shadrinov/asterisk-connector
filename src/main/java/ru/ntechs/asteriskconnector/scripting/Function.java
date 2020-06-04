package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

@Getter
public abstract class Function {
	private ScriptFactory scriptFactory;
	private Message message;
	private ArrayList<Scalar> params;

	public Function(ScriptFactory scriptFactory, ArrayList<Scalar> params) {
		super();

		this.scriptFactory = scriptFactory;
		this.params = params;
	}

	public Function(ScriptFactory scriptFactory, Message message, ArrayList<Scalar> params) {
		super();

		this.scriptFactory = scriptFactory;
		this.message = message;
		this.params = params;
	}

	@Override
	public String toString() {
		ArrayList<String> params = new ArrayList<>();

		for (Scalar entry : this.params)
			params.add(entry.asString());

		return String.format("%s(%s)", getName(), String.join(", ", params));
	}

	public abstract String getName();
	public abstract Scalar eval() throws IOException, BitrixLocalException;
	public abstract ArrayList<? extends Object> getIntermediateBeans();
}
