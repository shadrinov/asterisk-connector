package ru.ntechs.asteriskconnector.scripting;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;

import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventNode;

public class Expression {
	private ScriptFactory scriptFactory;
	private EventChain eventChain;
	private String expr;
	private ArrayList<Object> intermediateBeans;
	private Message message;

	private CharArrayReader reader;

	private int failCharIndex;

	public Expression(ScriptFactory scriptFactory, EventChain eventChain, String expr) {
		super();

		this.scriptFactory = scriptFactory;
		this.eventChain = eventChain;
		this.expr = expr.trim();
		this.intermediateBeans = new ArrayList<>();
		this.reader = new CharArrayReader(this.expr.toCharArray());
	}

	public Expression(ScriptFactory scriptFactory, EventChain eventChain, String expr, Message message) {
		super();

		this.scriptFactory = scriptFactory;
		this.eventChain = eventChain;
		this.expr = expr.trim();
		this.intermediateBeans = new ArrayList<>();
		this.reader = new CharArrayReader(this.expr.toCharArray());
		this.message = message;
	}

	public Scalar eval() throws IOException, BitrixLocalException {
		Scalar result = new Scalar(expr);
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

		return result;
	}

	private Scalar evalEvent(String eventName, ArrayList<Scalar> params) throws BitrixLocalException {
		if (params.size() != 1)
			throw new BitrixLocalException(formatError("Wrong number of parameters in event reference"));

		Message msg;

		if (eventName.equalsIgnoreCase("!")) {
			msg = message;

			if (msg == null)
				throw new BitrixLocalException(formatError(String.format("no context event message ('%s') specfied", eventName)));
		}
		else {
			EventNode node = eventChain.findMessage(message, eventName);

			if (node == null)
				throw new BitrixLocalException(formatError(String.format("Unable to find message '%s' in current event chain", eventName)));

			msg = node.getMessage();

			if (msg == null)
				throw new BitrixLocalException(formatError(String.format("BUG: EventNode found, but it doesn't contain message '%s'", eventName)));
		}

		String value = msg.getAttribute(params.get(0).asString());

		if (value == null)
			throw new BitrixLocalException(formatError(String.format("Attribute '%s' is not defined", params.get(0))));

		return new Scalar("${" + eventName + "}", value);
	}

	private Scalar evalFunc(String funcName, ArrayList<Scalar> params) throws IOException, BitrixLocalException {
		Function func;

		switch (funcName.toLowerCase()) {
			case ("channel"): func = new FunctionChannel(scriptFactory, params); break;
//			case ("filecontents"): func = new FunctionFileContents(scriptFactory, message, params); break;
			case ("rest"): func = new FunctionREST(scriptFactory, message, params); break;

			default:
				throw new BitrixLocalException(formatError("Unknown function"));
		}

		Scalar result = func.eval();
		ArrayList<? extends Object> beans = func.getIntermediateBeans();

		if (beans != null)
			intermediateBeans.addAll(beans);

		return result;
	}

	private Scalar parseReplace() throws IOException, BitrixLocalException {
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

	private Scalar parseEvent() throws IOException, BitrixLocalException {
		StringBuilder eventName = new StringBuilder();
		ArrayList<Scalar> params = null;

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

	private Scalar parseFunction() throws IOException, BitrixLocalException {
		StringBuilder funcName = new StringBuilder();
		ArrayList<Scalar> params = null;

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

	private ArrayList<Scalar> parseParam() throws IOException, BitrixLocalException {
		StringBuilder param = new StringBuilder();
		ArrayList<Scalar> params = new ArrayList<>();

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case (','): params.add(new Scalar("<unnamed parameter>", param.toString().trim())); param = new StringBuilder(); break;
				case ('('): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case (')'): params.add(new Scalar("<unnamed parameter>", param.toString().trim())); return params;
				case ('$'): param.append(parseReplace()); break;
				case ('"'): param.append(parseQuoted()); break;
				case ('\\'): param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));

				default: param.append((char)chr); break;
			}
		}
	}

	private Scalar parseQuoted() throws IOException, BitrixLocalException {
		StringBuilder param = new StringBuilder();

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('"'): return new Scalar("<unnamed quoted>", param.toString());
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
		if (failCharIndex >= expr.length())
			return String.format("%s: %s", message, expr);
		else
			return String.format("%s: %s <-- ... %s", message, expr.substring(0, failCharIndex), expr.substring(failCharIndex + 1));
	}

	public ArrayList<Object> getIntermediateBeans() {
		return intermediateBeans;
	}
}
