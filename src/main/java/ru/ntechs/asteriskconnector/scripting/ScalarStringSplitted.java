package ru.ntechs.asteriskconnector.scripting;

import java.util.Date;

import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

public class ScalarStringSplitted extends ScalarString {
	private StringBuilder builder;

	public ScalarStringSplitted(String name) {
		super(name);
		this.builder = new StringBuilder();
	}

	public ScalarStringSplitted(String name, String value) {
		super(name, value);
		this.builder = (value != null) ? new StringBuilder(value) : new StringBuilder();
	}

	@Override
	public boolean isNull() {
		return (builder.length() == 0);
	}

	@Override
	public boolean isEmpty() {
		return (builder.length() == 0);
	}

	@Override
	public String asString() {
		value = !isNull() ? builder.toString() : null;
		return super.asString();
	}

	@Override
	public Short asShort() throws BitrixLocalException {
		value = !isNull() ? builder.toString() : null;
		return super.asShort();
	}

	@Override
	public Integer asInteger() throws BitrixLocalException {
		value = !isNull() ? builder.toString() : null;
		return super.asInteger();
	}

	@Override
	public Long asLong() throws BitrixLocalException {
		value = !isNull() ? builder.toString() : null;
		return super.asLong();
	}

	@Override
	public Date asDate() throws BitrixLocalException {
		value = !isNull() ? builder.toString() : null;
		return super.asDate();
	}

	@Override
	public byte[] asByteArray() {
		value = !isNull() ? builder.toString() : null;
		return super.asByteArray();
	}

	@Override
	public Scalar append(Scalar operand) {
		if (operand == null)
			return this;

		if (operand instanceof ScalarStringSplitted) {
			int len = ((ScalarStringSplitted) operand).builder.length();
			int idx;

			for (idx = 0; idx < len; idx++)
				builder.append(((ScalarStringSplitted) operand).builder.charAt(idx));
		}
		else
			builder.append(operand.asString());

		return this;
	}

	@Override
	public Scalar append(char operand) {
		builder.append(operand);
		return this;
	}

	@Override
	public Scalar trim() {
		int len = builder.length();

		while ((len > 0) && (builder.charAt(0) <= ' ')) {
			builder.deleteCharAt(0);
			len--;
		}

		len--;

		while ((len >= 0) && (builder.charAt(len) <= ' ')) {
			builder.deleteCharAt(len);
			len--;
		}

		return this;
	}
}
