package ru.ntechs.asteriskconnector.scripting;

import java.nio.ByteBuffer;
import java.util.Date;

import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

public class ScalarInteger extends Scalar {
	private Long value;

	public ScalarInteger(String name) {
		super(name);
	}

	public ScalarInteger(String name, Long value) {
		super(name);
		this.value = value;
	}

	@Override
	public boolean isNull() {
		return (value == null);
	}

	@Override
	public boolean isEmpty() {
		return (value == null);
	}

	@Override
	public String asString() {
		return (value != null) ? String.valueOf(value) : null;
	}

	@Override
	public Short asShort() throws BitrixLocalException {
		return value.shortValue();
	}

	@Override
	public Integer asInteger() throws BitrixLocalException {
		return value.intValue();
	}

	@Override
	public Long asLong() throws BitrixLocalException {
		return value;
	}

	@Override
	public Double asDouble() throws BitrixLocalException {
		return value.doubleValue();
	}

	@Override
	public Date asDate() throws BitrixLocalException {
		return (value != null) ? new Date(value) : null;
	}

	@Override
	public byte[] asByteArray() {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(value);
	    return buffer.array();
	}

	@Override
	public Scalar append(Scalar operand) {
		if ((operand == null) || operand.isNull())
			return this;

		if (value == null)
			return operand;

		return new ScalarString(getName(), String.valueOf(value)).append(operand);
	}

	@Override
	public Scalar append(char operand) {
		return new ScalarStringSplitted(getName(), String.valueOf(value)).append(operand);
	}

	@Override
	public Scalar trim() {
		return this;
	}
}
