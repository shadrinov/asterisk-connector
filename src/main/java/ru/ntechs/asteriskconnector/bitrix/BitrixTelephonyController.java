package ru.ntechs.asteriskconnector.bitrix;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.AMI;
import ru.ntechs.ami.actions.Originate;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Event;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalLine;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEvent;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEventOnAppInstall;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEventOnExternalCallStart;
import ru.ntechs.asteriskconnector.config.ConnectorAsterisk;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;

@Slf4j
@RestController
@RequestMapping("/rest")
public class BitrixTelephonyController {
	@Autowired
	private BitrixTelephony bitrixTelephony;

	@Autowired
	private ConnectorConfig conf;

	@Autowired
	private AMI ami;

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public @ResponseBody User getUserById(@PathVariable String id) {
		User user = new User();
		user.setFirstName("john");
		user.setLastName("adward");
		return user;
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Users getAllUsers() {
		User user1 = new User();
		user1.setFirstName("john");
		user1.setLastName("adward");

		User user2 = new User();
		user2.setFirstName("tom");
		user2.setLastName("hanks");

		Users users = new Users();
		users.setUsers(new ArrayList<User>());
		users.getUsers().add(user1);
		users.getUsers().add(user2);

		log.info(String.format("!!!!!!!!!!!!!!!!!!!!!!Incoming request"));
		return users;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Users getAllUsers2() {
		User user1 = new User();
		user1.setFirstName("john");
		user1.setLastName("adward");

		User user2 = new User();
		user2.setFirstName("tom");
		user2.setLastName("hanks");

		Users users = new Users();
		users.setUsers(new ArrayList<User>());
		users.getUsers().add(user1);
		users.getUsers().add(user2);

		log.info(String.format("!!!!!!!!!!!!!!!!!!!!!!Incoming request"));
		return users;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded" }, value = "/")
	public @ResponseBody String appPage(@RequestBody MultiValueMap<String, String> params) {
		return "this is start page";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded" }, value = "/event")
	public @ResponseBody String event(@RequestBody MultiValueMap<String, String> params) {
		try {
			switch (BitrixEvent.getEvent(params)) {
				case ("ONAPPINSTALL"):
					BitrixEventOnAppInstall beOnAppInstall = new BitrixEventOnAppInstall(params);

					log.info("Handling event: {}", beOnAppInstall.getEvent());
					bitrixTelephony.installAuth(beOnAppInstall);

					for (Event event : bitrixTelephony.getEvent()) {
						if (!event.getEvent().equalsIgnoreCase("ONAPPINSTALL")) {
							log.info("Unbind event {}: {}", event.getEvent(), event.getHandler());
							bitrixTelephony.unbindEvent(event);
						}
					}

					for (ExternalLine telephonyLine : bitrixTelephony.getExternalLine()) {
						log.info("Deleting external line {}: {}", telephonyLine.getNumber(), telephonyLine.getName());
						bitrixTelephony.deleteExternalLine(telephonyLine);
					}

					if ((conf.getBitrix() != null) && (conf.getBitrix().getExternalLines() != null)) {
						for (ExternalLine line : conf.getBitrix().getExternalLines()) {
							log.info("Registering external line: {}, {}", line.getNumber(), line.getName());
							bitrixTelephony.addExternalLine(line.getNumber(), line.getName());
						}
					}

					log.info("Registering event: ONEXTERNALCALLSTART");
					bitrixTelephony.bindEvent("ONEXTERNALCALLSTART", "https://connector.ntechs.ru/rest/event");

					log.info(bitrixTelephony.getEvent().toString());
					log.info(bitrixTelephony.getExternalLine().toString());
					break;

				case ("ONEXTERNALCALLSTART"):
					BitrixEventOnExternalCallStart beOnExternalCallStart = new BitrixEventOnExternalCallStart(params);
					ConnectorAsterisk asterisk = conf.getAsterisk();

					log.info("Handling event: {}", beOnExternalCallStart.getEvent());

					if (asterisk != null) {
						Originate originate = new Originate(ami);

						log.info(asterisk.toString());
						originate.setChannel(MessageFormat.format(asterisk.getChannel(), "11"));
						originate.setContext(asterisk.getContext());
						originate.setExten(MessageFormat.format(asterisk.getExten(), "89605332222"));
						originate.setPriority(asterisk.getPriority());
						originate.submit();
					}
					else
						log.info("no configuration: connector.asterisk");

					break;

				default:
					log.info("Unhandled event: {}", BitrixEvent.getEvent(params));
					break;
			}
		} catch (BitrixRestApiException e) {
			log.info(e.getMessage());
		}

		return null;
	}
}
