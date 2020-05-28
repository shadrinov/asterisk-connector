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

	public Function(ScriptFactory scriptFactory) {
		super();

		this.scriptFactory = scriptFactory;
	}

	public Function(ScriptFactory scriptFactory, Message message) {
		super();

		this.scriptFactory = scriptFactory;
		this.message = message;
	}

	public abstract Scalar eval() throws IOException, BitrixLocalException;
	public abstract ArrayList<? extends Object> getIntermediateBeans();
}
