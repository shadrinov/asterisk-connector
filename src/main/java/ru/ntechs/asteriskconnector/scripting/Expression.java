package ru.ntechs.asteriskconnector.scripting;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventDispatcher;
import ru.ntechs.asteriskconnector.eventchain.EventNode;

public class Expression {
	private EventDispatcher eventDispatcher;
	private EventChain eventChain;
	private String expr;

	private CharArrayReader reader;

	private int failCharIndex;

	public Expression(EventDispatcher eventDispatcher, EventChain eventChain, String expr) {
		super();

		this.eventDispatcher = eventDispatcher;
		this.eventChain = eventChain;
		this.expr = expr.trim();
		this.reader = new CharArrayReader(this.expr.toCharArray());
	}

	public String eval() throws IOException, BitrixLocalException {
		StringBuilder result = new StringBuilder();
		int chr;

		failCharIndex = 0;

		while ((chr = reader.read()) != -1) {
			failCharIndex++;

			switch (chr) {
				case ('$'): result.append(parseReplace()); break;
				case ('\\'): result.append(parseEscape()); break;

				default: result.append((char)chr); break;
			}
		}

		return result.toString();
	}

	private String evalEvent(String eventName, ArrayList<String> params) throws BitrixLocalException {
		if (params.size() != 1)
			throw new BitrixLocalException(formatError("Wrong number of parameters in event reference"));

		EventNode node = eventChain.findMessage(eventName);

		if (node == null)
			throw new BitrixLocalException(formatError(String.format("Unable to find message '%s' in current event chain", eventName)));

		Message msg = node.getMessage();

		if (msg == null)
			throw new BitrixLocalException(formatError(String.format("BUG: EvetNode found, but it doesn't contain message '%s'", eventName)));

		String value = msg.getAttribute(params.get(0));

		if (value == null)
			throw new BitrixLocalException(formatError(String.format("Attribute '%s' is not defined", params.get(0))));

		return value;
	}

	private String evalFunc(String funcName, ArrayList<String> params) throws IOException, BitrixLocalException {
		switch (funcName.toLowerCase()) {
			case ("channel"): return new FunctionChannel(eventDispatcher, params.get(0), params.get(1)).eval();

			default:
				throw new BitrixLocalException(formatError("Unknown function"));
		}
	}

	private String parseReplace() throws IOException, BitrixLocalException {
		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('{'): return parseEvent();
				case ('('): return parseFunction();
				case (-1): throw new BitrixLocalException(formatError("End of expression, but replacement type is expected"));

				default:
					new BitrixLocalException(formatError("Unsupported replacement, '(' or '{' expected"));
			}
		}
	}

	private String parseEvent() throws IOException, BitrixLocalException {
		StringBuilder eventName = new StringBuilder();
		ArrayList<String> params = null;

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('('): params = parseParam(); break;
				case (')'): throw new BitrixLocalException(formatError("Unexpected closing bracket"));
				case ('{'): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case ('}'): return evalEvent(eventName.toString().trim(), params);
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));

				default: eventName.append((char)chr); break;
			}
		}
	}

	private String parseFunction() throws IOException, BitrixLocalException {
		StringBuilder funcName = new StringBuilder();
		ArrayList<String> params = null;

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('('): params = parseParam(); break;
				case (')'): return evalFunc(funcName.toString().trim(), params);
				case ('{'): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case ('}'): throw new BitrixLocalException(formatError("Unexpected closing bracket"));
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));

				default: funcName.append((char)chr); break;
			}
		}
	}

	private ArrayList<String> parseParam() throws IOException, BitrixLocalException {
		StringBuilder param = new StringBuilder();
		ArrayList<String> params = new ArrayList<>();

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case (','): params.add(param.toString().trim()); param = new StringBuilder(); break;
				case ('('): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case (')'): params.add(param.toString().trim()); return params;
				case ('$'): param.append(parseReplace()); break;
				case ('"'): param.append(parseQuoted()); break;
				case ('\\'): param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));

				default: param.append((char)chr); break;
			}
		}
	}

	private String parseQuoted() throws IOException, BitrixLocalException {
		StringBuilder param = new StringBuilder();

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('"'): return param.toString();
				case ('\\'): param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));

				default: param.append((char)chr); break;
			}
		}
	}

	private char parseEscape() throws IOException, BitrixLocalException {
		int chr = reader.read();
		failCharIndex++;

		if (chr != -1)
			return (char)chr;
		else
			throw new BitrixLocalException(formatError("Unexpected end of expression"));
	}

	private String formatError(String message) {
		return String.format("%s: %s <-- ... %s", message, expr.substring(0, failCharIndex), expr.substring(failCharIndex + 1));
	}
}
