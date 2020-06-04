package ru.ntechs.asteriskconnector.scripting;

import java.text.ParseException;
import java.util.Date;

import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

public class ScalarString extends Scalar {
	protected String value;

	public ScalarString(String name) {
		super(name);
	}

	public ScalarString(String name, String value) {
		super(name);
		this.value = value;
	}

	@Override
	public boolean isNull() {
		return (value == null);
	}

	@Override
	public String asString() {
		return value;
	}

	@Override
	public Short asShort() throws BitrixLocalException {
		if (value == null)
			return null;

		try {
			return Short.valueOf(value);
		} catch (NumberFormatException e) {
			throw new BitrixLocalException(String.format("%s = %s: is not a short: %s",
					getName(), value, e.getMessage()));
		}
	}

	@Override
	public Integer asInteger() throws BitrixLocalException {
		if (value == null)
			return null;

		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			throw new BitrixLocalException(String.format("%s = %s: is not an integer: %s",
					getName(), value, e.getMessage()));
		}
	}

	@Override
	public Long asLong() throws BitrixLocalException {
		if (value == null)
			return null;

		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			throw new BitrixLocalException(String.format("%s = %s: is not a long: %s",
					getName(), value, e.getMessage()));
		}
	}

	@Override
	public Double asDouble() throws BitrixLocalException {
		if (value == null)
			return null;

		try {
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			throw new BitrixLocalException(String.format("%s = %s: is not a double: %s",
					getName(), value, e.getMessage()));
		}
	}

	@Override
	public Date asDate() throws BitrixLocalException {
		if (value == null)
			return null;

		try {
			return getDateFormat().parse(value);
		} catch (ParseException e) {
			throw new BitrixLocalException(String.format("%s = %s: is not a date: %s",
					getName(), value, e.getMessage()));
		}
	}

	@Override
	public byte[] asByteArray() {
		return (value != null) ? value.getBytes() : null;
	}

	@Override
	public Scalar append(Scalar operand) {
		if ((operand == null) || operand.isNull())
			return this;

		if (value == null)
			return operand;

		value = value.concat(operand.asString());

		return this;
	}

	@Override
	public Scalar append(char operand) {
		return (new ScalarStringSplitted(getName(), value)).append(operand);
	}

	@Override
	public Scalar trim() {
		value = value.trim();
		return this;
	}
}
