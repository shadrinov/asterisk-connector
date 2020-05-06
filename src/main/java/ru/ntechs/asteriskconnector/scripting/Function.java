package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

@Getter
public abstract class Function {
	private ScriptFactory scriptFactory;

	public Function(ScriptFactory scriptFactory) {
		super();
		this.scriptFactory = scriptFactory;
	}

	public abstract String eval() throws IOException, BitrixLocalException;
	public abstract ArrayList<? extends Object> getIntermediateBeans();
}
