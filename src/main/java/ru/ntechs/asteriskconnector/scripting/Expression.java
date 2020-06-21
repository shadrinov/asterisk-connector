package ru.ntechs.asteriskconnector.scripting;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

	public ArrayList<Object> getIntermediateBeans() {
		return intermediateBeans;
	}

	public ScriptFactory getScriptFactory() {
		return scriptFactory;
	}

	public EventChain getEventChain() {
		return eventChain;
	}

	public Message getMessage() {
		return message;
	}

	public Scalar eval() throws IOException, BitrixLocalException {
		Scalar result = new ScalarString(expr);
		int chr;

		failCharIndex = 0;

		while ((chr = reader.read()) != -1) {
			failCharIndex++;

			switch (chr) {
				case ('$'): result = result.append(parseReplace()); break;
				case ('\\'): result = result.append(parseEscape()); break;

				default: result = result.append((char)chr); break;
			}
		}

		return result;
	}

	private Scalar evalEvent(Scalar eventName, HashMap<String,String> constraints, ArrayList<Scalar> params) throws BitrixLocalException {
		if (params.size() != 1)
			throw new BitrixLocalException(formatError("Wrong number of parameters in event reference"));

		Message msg;
		String eventNameStr = eventName.asString();

		if (eventNameStr.equals("!")) {
			msg = message;

			if (!constraints.isEmpty())
				throw new BitrixLocalException(formatError(String.format("no atribute constraints allowed on context event message ('%s')", eventNameStr)));

			if (msg == null)
				throw new BitrixLocalException(formatError(String.format("no context event message ('%s') specfied", eventNameStr)));
		}
		else {
			EventNode node = eventChain.findMessage(message, eventNameStr, constraints);

			if (node == null)
				throw new BitrixLocalException(formatError(String.format("Unable to find message '%s' in current event chain", eventNameStr)));

			msg = node.getMessage();

			if (msg == null)
				throw new BitrixLocalException(formatError(String.format("BUG: EventNode found, but it doesn't contain message '%s'", eventNameStr)));
		}

		String value = msg.getAttribute(params.get(0).asString());

		if (value == null)
			throw new BitrixLocalException(formatError(String.format("Attribute '%s' is not defined", params.get(0))));

		return new ScalarString("${" + eventNameStr + "}", value);
	}

	private Scalar evalFunc(Scalar funcName, ArrayList<Scalar> params) throws IOException, BitrixLocalException {
		Function func;

		switch (funcName.asString().toLowerCase()) {
			case (FunctionChannel.LC_NAME):
				func = new FunctionChannel(this, params);
				break;

			case (FunctionDuration.LC_NAME):
				func = new FunctionDuration(this, params);
				break;

			case (FunctionFileContents.LC_NAME):
				func = new FunctionFileContents(this, params);
				break;

			case (FunctionREST.LC_NAME):
				func = new FunctionREST(this, params);
				break;

			default:
				throw new BitrixLocalException(formatError(String.format("Function '%s' is not supported", funcName.asString())));
		}

		try {
			Scalar result = func.eval();
			ArrayList<? extends Object> beans = func.getIntermediateBeans();

			if (beans != null)
				intermediateBeans.addAll(beans);

			return result;
		}
		catch (BitrixLocalException e) {
			throw new BitrixLocalException(formatError(e.getMessage()));
		}
	}

	private Scalar parseReplace() throws IOException, BitrixLocalException {
		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('{'): return parseEvent();
				case ('('): return parseFunction();
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: replacement type '(' or '{' is expected"));

				default:
					new BitrixLocalException(formatError("Unsupported replacement, '(' or '{' is expected"));
			}
		}
	}

	private Scalar parseEvent() throws IOException, BitrixLocalException {
		Scalar eventName = new ScalarStringSplitted("<event name>");
		ArrayList<Scalar> params = null;
		HashMap<String, String> constraints = null;

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('['):
					if (constraints != null)
						throw new BitrixLocalException(formatError("The only constraint specification is allowed"));

					constraints = parseConstraints();
					break;

				case (']'): throw new BitrixLocalException(formatError("Unexpected closing bracket ']'"));

				case ('('):
					if (params != null)
						throw new BitrixLocalException(formatError("The only parameter specification is allowed"));

					params = parseParam();
					break;

				case (')'): throw new BitrixLocalException(formatError("Unexpected closing bracket ')'"));
				case ('{'): throw new BitrixLocalException(formatError("Excessive use of opening bracket '{'"));
				case ('}'): return evalEvent(eventName.trim(), constraints, params);
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: event name or closing bracket '}' is expected"));

				default: eventName = eventName.append((char)chr); break;
			}
		}
	}

	private Scalar parseFunction() throws IOException, BitrixLocalException {
		Scalar funcName = new ScalarStringSplitted("<function name>");
		ArrayList<Scalar> params = null;

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('('):
					if (params != null)
						throw new BitrixLocalException(formatError("The only parameter specification is allowed"));

					params = parseParam();
					break;

				case (')'): return evalFunc(funcName.trim(), params);
				case ('{'): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case ('}'): throw new BitrixLocalException(formatError("Unexpected closing bracket"));
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));

				default: funcName = funcName.append((char)chr); break;
			}
		}
	}

	private HashMap<String, String> parseConstraints() throws IOException, BitrixLocalException {
		HashMap<String, String> constraints = new HashMap<>();
		Scalar attrName = new ScalarStringSplitted("<constraint attribute name>");
		Scalar attrVal = new ScalarStringSplitted("<constraint attribute value>");

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('='):
					boolean doScan = true;

					while (doScan) {
						String attrNameStr;

						chr = reader.read();
						failCharIndex++;

						switch (chr) {
							case (','):
								attrNameStr = attrName.trim().asString();

								if (constraints.containsKey(attrNameStr))
									throw new BitrixLocalException(String.format("Event attribute constraint already defined: (%s = %s)", attrNameStr, constraints.get(attrNameStr)));

								constraints.put(attrNameStr, attrVal.trim().asString());
								doScan = false;
								break;

							case ('['): throw new BitrixLocalException(formatError("Excessive use of opening bracket '['"));

							case (']'):
								attrNameStr = attrName.trim().asString();

								if (constraints.containsKey(attrNameStr))
									throw new BitrixLocalException(String.format("Event attribute constraint already defined: (%s = %s)", attrNameStr, constraints.get(attrNameStr)));

								constraints.put(attrNameStr, attrVal.trim().asString());
								return constraints;

							case ('$'): attrName = attrName.append(parseReplace()); break;
							case ('"'): attrName = attrName.append(parseQuoted()); break;
							case ('\\'): attrName = attrName.append(parseEscape()); break;
							case (-1): throw new BitrixLocalException(formatError("Premature end of expression: attribute value or closing bracket ']' is expected"));

							default:
								attrVal = attrVal.append((char)chr);
								break;
						}
					}

					break;

				case ('['): throw new BitrixLocalException(formatError("Excessive use of opening bracket '['"));
				case (']'): throw new BitrixLocalException(formatError("Unexpected closing bracket ']'"));
				case ('$'): attrName = attrName.append(parseReplace()); break;
				case ('"'): attrName = attrName.append(parseQuoted()); break;
				case ('\\'): attrName = attrName.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: 'attribute = value' is expected"));

				default: attrName = attrName.append((char)chr); break;
			}
		}
	}

	private ArrayList<Scalar> parseParam() throws IOException, BitrixLocalException {
		Scalar param = new ScalarStringSplitted("<parameter>");
		ArrayList<Scalar> params = new ArrayList<>();

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case (','): params.add(param.trim()); param = new ScalarStringSplitted("<parameter>"); break;
				case ('('): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case (')'): params.add(param.trim()); return params;
				case ('$'): param = param.append(parseReplace()); break;
				case ('"'): param = param.append(parseQuoted()); break;
				case ('\\'): param = param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: parameter or closing bracket ')' is expected"));

				default: param = param.append((char)chr); break;
			}
		}
	}

	private Scalar parseQuoted() throws IOException, BitrixLocalException {
		Scalar param = new ScalarStringSplitted("<quted>");

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('"'): return param;
				case ('\\'): param = param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: closing quotes '\"' is expected"));

				default: param = param.append((char)chr); break;
			}
		}
	}

	private char parseEscape() throws IOException, BitrixLocalException {
		int chr = reader.read();
		failCharIndex++;

		if (chr != -1)
			return (char)chr;
		else
			throw new BitrixLocalException(formatError("Premature end of expression: escaped symbol is expected"));
	}

	private String formatError(String message) {
		if (failCharIndex >= expr.length())
			return String.format("%s: %s", message, expr);
		else
			return String.format("%s: %s <-- ... %s", message, expr.substring(0, failCharIndex), expr.substring(failCharIndex + 1));
	}
}
