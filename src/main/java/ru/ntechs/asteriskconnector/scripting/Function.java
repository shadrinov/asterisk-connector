package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;

import lombok.Getter;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;

@Getter
public abstract class Function {
	private EventDispatcher eventDispatcher;

	public Function(EventDispatcher eventDispatcher) {
		super();
		this.eventDispatcher = eventDispatcher;
	}

	public abstract String eval() throws IOException, BitrixLocalException;
}
