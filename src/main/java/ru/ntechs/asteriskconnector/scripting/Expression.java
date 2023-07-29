package ru.ntechs.asteriskconnector.scripting;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.eventchain.MessageChain;
import ru.ntechs.asteriskconnector.eventchain.MessageDispatcher;
import ru.ntechs.asteriskconnector.eventchain.MessageNode;

@Slf4j
public class Expression {
	private MessageChain eventChain;
	private String expr;
	private ArrayList<Object> intermediateBeans;
	private MessageNode contextNode;

	private CharArrayReader reader;

	private int failCharIndex;

	public Expression(MessageChain eventChain, String expr) {
		super();

		this.eventChain = eventChain;
		this.expr = expr.trim();
		this.intermediateBeans = new ArrayList<>();
		this.reader = new CharArrayReader(this.expr.toCharArray());
	}

	public Expression(MessageChain eventChain, String expr, MessageNode node) {
		super();

		this.eventChain = eventChain;
		this.expr = expr.trim();
		this.intermediateBeans = new ArrayList<>();
		this.reader = new CharArrayReader(this.expr.toCharArray());
		this.contextNode = node;
	}

	public ArrayList<Object> getIntermediateBeans() {
		return intermediateBeans;
	}

	public MessageDispatcher getMessageDispatcher() {
		return eventChain.getEventDispatcher();
	}

	public ScriptFactory getScriptFactory() {
		return eventChain.getEventDispatcher().getScriptFactory();
	}

	public MessageChain getEventChain() {
		return eventChain;
	}

	public MessageNode getContextMessage() {
		return contextNode;
	}

	public Scalar eval() throws IOException, BitrixLocalException {
		Scalar result = new ScalarString(expr);
		int chr;

		failCharIndex = 0;

		while ((chr = reader.read()) != -1) {
			failCharIndex++;

			if (chr == '|') {
				if (!result.isNull())
					return result;
				else
					continue;
			}

			switch (chr) {
				case ('$'): result = result.append(parseReplace()); break;

				case ('"'): result = result.append(parseQuoted()); break;
				case ('\\'): result = result.append(parseEscape()); break;
				case (' '): break;

				default: result = result.append((char)chr); break;
			}
		}

		return result;
	}

	private Scalar evalEvent(Scalar eventName, HashMap<String, String> constraints, ArrayList<Scalar> params) throws BitrixLocalException {
		if ((params != null) && params.size() > 1)
			throw new BitrixLocalException(formatError("Event search statement doesn't match prototype ${EventName[[attrName=attrValue[,attrName=attrValue]]][(attrName)]}"));

		MessageNode node;
		String name = eventName.asString();

		if (name.equals("!")) {
			node = contextNode;

			if ((constraints != null) && !constraints.isEmpty())
				throw new BitrixLocalException(formatError(String.format("no atribute constraints allowed on context event message ('%s')", name)));
		}
		else
			node = (contextNode != null) ?
					contextNode.findMessage(name, constraints) :
						eventChain.findMessage(name, constraints);

		if (node != null) {
			if ((params == null) || params.size() == 0)
				return new ScalarMessage(String.format("${%s}", name), node);
			else if (params.size() == 1) {
				String attrVal = node.getMessage().getAttribute(params.get(0).asString());

				if (attrVal == null)
					log.info("Warning: message attribute {} is not defined", formatEvent(eventName, constraints, params));

				return new ScalarString(String.format("${%s}", name), attrVal);
			}
		}
		else
			log.info("Warning: event {} not found", formatEvent(eventName, constraints, params));

		return new ScalarString(String.format("${%s}", name), null);
	}

	private Scalar evalFunc(Scalar funcName, ArrayList<Scalar> params) throws IOException, BitrixLocalException {
		Function func;

		if (params == null)
			params = new ArrayList<>();

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

			case (FunctionResponsible.LC_NAME):
				func = new FunctionResponsible(this, params);
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

	private void skipReplace() throws IOException, BitrixLocalException {
		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('{'): skipEvent(); return;
				case ('('): skipFunction(); return;
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

	private void skipEvent() throws IOException, BitrixLocalException {
		boolean paramsIsSkipped = false;
		boolean constraintsIsSkipped = false;

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('['):
					if (constraintsIsSkipped)
						throw new BitrixLocalException(formatError("The only constraint specification is allowed"));

					skipConstraints();
					constraintsIsSkipped = true;
					break;

				case (']'): throw new BitrixLocalException(formatError("Unexpected closing bracket ']'"));

				case ('('):
					if (paramsIsSkipped)
						throw new BitrixLocalException(formatError("The only parameter specification is allowed"));

					skipParam();
					paramsIsSkipped = true;
					break;

				case (')'): throw new BitrixLocalException(formatError("Unexpected closing bracket ')'"));
				case ('{'): throw new BitrixLocalException(formatError("Excessive use of opening bracket '{'"));
				case ('}'): return;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: event name or closing bracket '}' is expected"));
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

	private void skipFunction() throws IOException, BitrixLocalException {
		boolean paramsIsSkipped = false;

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('('):
					if (paramsIsSkipped)
						throw new BitrixLocalException(formatError("The only parameter specification is allowed"));

					skipParam();
					paramsIsSkipped = true;
					break;

				case (')'): return;
				case ('{'): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case ('}'): throw new BitrixLocalException(formatError("Unexpected closing bracket"));
				case (-1): throw new BitrixLocalException(formatError("Unexpected end of expression"));
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

								attrName = new ScalarStringSplitted("<constraint attribute name>");
								attrVal = new ScalarStringSplitted("<constraint attribute value>");
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

	private void skipConstraints() throws IOException, BitrixLocalException {
		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('='):
					boolean doScan = true;

					while (doScan) {
						chr = reader.read();
						failCharIndex++;

						switch (chr) {
							case (','):
								doScan = false;
								break;

							case ('['): throw new BitrixLocalException(formatError("Excessive use of opening bracket '['"));

							case (']'):
								return;

							case ('$'): skipReplace(); break;
							case ('"'): skipQuoted(); break;
							case ('\\'): parseEscape(); break;
							case (-1): throw new BitrixLocalException(formatError("Premature end of expression: attribute value or closing bracket ']' is expected"));
						}
					}

					break;

				case ('['): throw new BitrixLocalException(formatError("Excessive use of opening bracket '['"));
				case (']'): throw new BitrixLocalException(formatError("Unexpected closing bracket ']'"));
				case ('$'): skipReplace(); break;
				case ('"'): skipQuoted(); break;
				case ('\\'): parseEscape(); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: 'attribute = value' is expected"));
			}
		}
	}

	private ArrayList<Scalar> parseParam() throws IOException, BitrixLocalException {
		Scalar param = new ScalarStringSplitted("<parameter>");
		ArrayList<Scalar> params = new ArrayList<>();

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			if (chr == '|') {
				if (!param.isNull())
					chr = skipParam();
				else
					continue;
			}

			switch (chr) {
				case (','): params.add(param); param = new ScalarStringSplitted("<parameter>"); break;
				case ('('): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case (')'): params.add(param); return params;
				case ('{'): throw new BitrixLocalException(formatError("Unexpected opening curly bracket '{'"));
				case ('}'): throw new BitrixLocalException(formatError("Unexpected closing curly bracket '}'"));
				case ('$'): param = param.isEmpty() ? parseReplace() : param.append(parseReplace()); break;
				case ('"'): param = param.isEmpty() ? parseQuoted() : param.append(parseQuoted()); break;
				case (' '): break;
				case ('\\'): param = param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: parameter or closing bracket ')' is expected"));

				default: param = param.append((char)chr); break;
			}
		}
	}

	private int skipParam() throws IOException, BitrixLocalException {
		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('('): throw new BitrixLocalException(formatError("Excessive use of opening bracket"));
				case (')'): return chr;
				case ('$'): skipReplace(); break;
				case ('"'): skipQuoted(); break;
				case ('\\'): parseEscape(); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: parameter or closing bracket ')' is expected"));
			}
		}
	}

	private Scalar parseQuoted() throws IOException, BitrixLocalException {
		Scalar param = new ScalarStringSplitted("<quoted>");

		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('"'): return param.isEmpty() ? new ScalarString("<quoted>", "") : param;
				case ('\\'): param = param.append(parseEscape()); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: closing quotes '\"' is expected"));

				default: param = param.append((char)chr); break;
			}
		}
	}

	private void skipQuoted() throws IOException, BitrixLocalException {
		while (true) {
			int chr = reader.read();
			failCharIndex++;

			switch (chr) {
				case ('"'): return;
				case ('\\'): parseEscape(); break;
				case (-1): throw new BitrixLocalException(formatError("Premature end of expression: closing quotes '\"' is expected"));
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

	private String formatEvent(Scalar eventName, HashMap<String, String> constraints, ArrayList<Scalar> params) {
		String logConstrs = "";
		String logParams = "";
		ArrayList<String> strings = new ArrayList<>();

		if (constraints != null) {
			constraints.forEach((key, val) -> strings.add(String.format("%s=%s", key, val)));
			logConstrs = String.format("[%s]", String.join(",", strings));
		}

		if (params != null) {
			strings.clear();
			params.forEach((arg) -> strings.add(arg.toString()));
			logParams = String.format("(%s)", String.join(",", strings));
		}

		return String.format("${%s%s%s}", eventName.toString(), logConstrs, logParams);
	}
}
