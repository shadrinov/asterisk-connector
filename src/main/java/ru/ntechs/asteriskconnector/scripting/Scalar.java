package ru.ntechs.asteriskconnector.scripting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

@Getter
public abstract class Scalar {
	private static final DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	private String name;

	public Scalar(String name) {
		this.name = name;
	}

	public DateFormat getDateFormat() {
		return iso8601DateFormat;
	}

	public abstract boolean isNull();
	public abstract String asString();
	public abstract Short asShort() throws BitrixLocalException;
	public abstract Integer asInteger() throws BitrixLocalException;
	public abstract Long asLong() throws BitrixLocalException;
	public abstract Date asDate() throws BitrixLocalException;
	public abstract byte[] asByteArray();

	public abstract Scalar append(Scalar operand);
	public abstract Scalar append(char operand);
	public abstract Scalar trim();

	@Override
	public String toString() {
		return asString();
	}
}
