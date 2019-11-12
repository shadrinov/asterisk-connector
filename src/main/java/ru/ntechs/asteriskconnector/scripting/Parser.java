package ru.ntechs.asteriskconnector.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {
	private final Logger log = LoggerFactory.getLogger(Parser.class);

	private Pattern namePattern = Pattern.compile("\\w+");
	private Pattern colonDelimiterPattern = Pattern.compile("\\s*:\\s*");
	private Pattern commaDelimiterPattern = Pattern.compile("\\s*,\\s*");
	private Pattern lfDelimiterPattern = Pattern.compile("\\s*(\\n|$)\\s*");

	private Script script = new Script();


	public Parser(String filename) throws FileNotFoundException {
		super();
		parseFile(new File(filename));
	}

	public Parser(File file) throws FileNotFoundException {
		super();
		parseFile(file);
	}

	static Integer parse(String filename) throws FileNotFoundException {
		Parser parser = new Parser(filename);
		return null;
	}

	static Integer parse(File file) throws FileNotFoundException {
		Parser parser = new Parser(file);
		return null;
	}

	public void parseFile(File file) throws FileNotFoundException {
		Scanner source = new Scanner(file);

		while (true) {
			Action action = new Action();

			while (true) {
				String attr = scanAttributeName(source);

				if (attr.equalsIgnoreCase("EventSequence")) {
					scanEventSequence(source, action);
				}
				else {
					source.useDelimiter(lfDelimiterPattern);
					String value = source.next();
					log.error(String.format("Unexpected attribute: %s, value: %s", attr, value));
					break;
				}
			}
		}

//		script.addChain();
//
//		scanEventSequence(source);
//		scanRequest(source);
	}

	private void scanEventSequence(Scanner source, Action action) {
		source.useDelimiter(commaDelimiterPattern);

		while (true) {
			if (!source.hasNext(namePattern)) {
				source.skip(commaDelimiterPattern);
				source.useDelimiter(lfDelimiterPattern);

				if (source.hasNext(namePattern)) {
					action.queueEvent(new Event(source.next()));
					break;
				}
				else {
					log.info(String.format("Unexpected token: %s", source.next()));
					break;
				}
			}
			else
				action.queueEvent(new Event(source.next()));
		}

		source.skip(lfDelimiterPattern);
	}

	private Scanner scanRequest(Scanner source) {
		log.info("URL: " + source.next());
		return source;
	}

	private String scanAttributeName(Scanner source) {
		String token;

		source.useDelimiter(colonDelimiterPattern);
		token = source.next(namePattern);
		source.skip(colonDelimiterPattern);

		return token;
	}
}
