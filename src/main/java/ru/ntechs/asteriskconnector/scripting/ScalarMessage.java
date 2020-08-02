package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.Date;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

public class ScalarMessage extends Scalar {
	MessageNode value;

	public ScalarMessage(String name) {
		super(name);
	}

	public ScalarMessage(String name, MessageNode event) {
		super(name);
		this.value = event;
	}

	@Override
	public boolean isNull() {
		return ((value != null) && (value.getMessage() != null));
	}

	@Override
	public boolean isEmpty() {
		return isNull();
	}

	@Override
	public String asString() {
		if ((value != null) && (value.getMessage() != null)) {
			Message message = value.getMessage();
			ArrayList<String> lines = new ArrayList<>();

			lines.add("Event: " + message.getName());

			for (String attr : message.getKeyOrder())
				lines.add(attr + ": " + message.getAttribute(attr));

			return String.join("\n", lines);
		}
		else
			return null;
	}

	@Override
	public Short asShort() throws BitrixLocalException {
		return 0;
	}

	@Override
	public Integer asInteger() throws BitrixLocalException {
		return 0;
	}

	@Override
	public Long asLong() throws BitrixLocalException {
		return 0l;
	}

	@Override
	public Double asDouble() throws BitrixLocalException {
		return 0.0;
	}

	@Override
	public Date asDate() throws BitrixLocalException {
		return new Date();
	}

	@Override
	public byte[] asByteArray() {
		String str = asString();
		return (str != null) ? str.getBytes() : null;
	}

	@Override
	public Scalar append(Scalar operand) {
		return new ScalarStringSplitted(getName(), asString()).append(operand);
	}

	@Override
	public Scalar append(char operand) {
		return new ScalarStringSplitted(getName(), asString()).append(operand);
	}

	@Override
	public Scalar trim() {
		return this;
	}

	public Message getMessage() {
		if ((value != null) && (value.getMessage() != null))
			return value.getMessage();
		else
			return null;
	}
}
