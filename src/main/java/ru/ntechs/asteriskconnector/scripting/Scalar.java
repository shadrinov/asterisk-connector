package ru.ntechs.asteriskconnector.scripting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;

public class Scalar {
	private static final DateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	private String name;

	private String valueStr;
	private Integer valueInt;
	private Date valueDate;
	private byte[] valueByteArray;

	public Scalar(String name) {
		this.name = name;
	}

	public Scalar(String name, String value) {
		this.name = name;
		this.valueStr = value;
	}

	public Scalar(String name, Integer value) {
		this.name = name;
		this.valueInt = value;
	}

	public Scalar(String name, Date value) {
		this.name = name;
		this.valueDate = value;
	}

	public Scalar(String name, byte[] value) {
		this.name = name;
		this.valueByteArray = value;
	}

	public void clear() {
		this.valueStr = null;
		this.valueInt = null;
		this.valueDate = null;
		this.valueByteArray = null;
	}

	public String asString() {
		if (valueStr != null) {
			return valueStr;
		}
		else if (valueInt != null) {
			return String.valueOf(valueInt);
		}
		else if (valueDate != null) {
			return iso8601DateFormat.format(valueDate);
		}
		else if (valueByteArray.length > 0) {
			return String.format("byte[%d]", valueByteArray.length);
		}

		return null;
	}

	public Integer asInteger() throws BitrixLocalException {
		if (valueInt != null) {
			return valueInt;
		}
		else if (valueStr != null) {
			try {
				return Integer.valueOf(valueStr);
			} catch (NumberFormatException e) {
				throw new BitrixLocalException(String.format("%s = %s: is not an integer: %s", name, valueStr, e.getMessage()));
			}
		}
		else if (valueDate != null) {
			return (int)(valueDate.getTime() / 1000);
		}
		else if (valueByteArray.length > 0) {
			return valueByteArray.length;
		}

		return null;
	}

	public Date asDate() throws BitrixLocalException {
		if (valueDate != null) {
			return valueDate;
		}
		else if (valueStr != null) {
			try {
				return iso8601DateFormat.parse(valueStr);
			} catch (ParseException e) {
				throw new BitrixLocalException(String.format("%s = %s: is not a date: %s", name, valueStr, e.getMessage()));
			}
		}
		else if (valueInt != null) {
			return new Date((long)valueInt * 1000);
		}

		return null;
	}

	public byte[] asByteArray() {
		return valueByteArray;
	}

	public void append(Scalar value) {
		// TODO Auto-generated method stub

	}

	public void append(char value) {
		// TODO Auto-generated method stub

	}
}
