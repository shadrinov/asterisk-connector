package ru.ntechs.asteriskconnector.bitrix;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
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
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEvent;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEventOnAppInstall;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEventOnExternalCallStart;
import ru.ntechs.asteriskconnector.config.ConnectorBitrix;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;
import ru.ntechs.asteriskconnector.config.ConnectorExternalLine;

@Slf4j
@RestController
@RequestMapping("/rest")
public class BitrixTelephonyController {
	@Autowired
	private BitrixTelephony bitrixTelephony;

	@Autowired
	private AMI ami;

	private ConnectorConfig conf;
	private HashMap<String, ConnectorExternalLine> externalLines;

	@Autowired
	public BitrixTelephonyController(ConnectorConfig conf) {
		this.conf = conf;

		externalLines = new HashMap<>();

		ConnectorBitrix bitrix = conf.getBitrix();

		if (bitrix != null) {
			ArrayList<ConnectorExternalLine> externalLines = bitrix.getExternalLines();

			if (externalLines != null) {
				for (ConnectorExternalLine line : externalLines) {
					this.externalLines.put(line.getNumber(), line);
				}
			}
			else
				log.info("no configuration: connector.bitrix.externalLines");
		}
		else
			log.info("no configuration: connector.bitrix");
	}

//	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	public @ResponseBody User getUserById(@PathVariable String id) {
//		User user = new User();
//		user.setFirstName("john");
//		user.setLastName("adward");
//		return user;
//	}

//	@RequestMapping(method = RequestMethod.POST)
//	public @ResponseBody Users getAllUsers() {
//		User user1 = new User();
//		user1.setFirstName("john");
//		user1.setLastName("adward");
//
//		User user2 = new User();
//		user2.setFirstName("tom");
//		user2.setLastName("hanks");
//
//		Users users = new Users();
//		users.setUsers(new ArrayList<User>());
//		users.getUsers().add(user1);
//		users.getUsers().add(user2);
//
//		log.info(String.format("!!!!!!!!!!!!!!!!!!!!!!Incoming request"));
//		return users;
//	}

//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody Users getAllUsers2() {
//		User user1 = new User();
//		user1.setFirstName("john");
//		user1.setLastName("adward");
//
//		User user2 = new User();
//		user2.setFirstName("tom");
//		user2.setLastName("hanks");
//
//		Users users = new Users();
//		users.setUsers(new ArrayList<User>());
//		users.getUsers().add(user1);
//		users.getUsers().add(user2);
//
//		log.info(String.format("!!!!!!!!!!!!!!!!!!!!!!Incoming request"));
//		return users;
//	}

	@RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded" }, value = "/")
	public @ResponseBody String appPage(@RequestBody MultiValueMap<String, String> params) {
		return "this is start page";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded" }, value = "/event")
	public @ResponseBody String event(@RequestBody MultiValueMap<String, String> params) {
		try {
			switch (BitrixEvent.getEvent(params).toUpperCase()) {
				case ("ONAPPINSTALL"):
					onAppInstall(params);
					break;

				case ("ONEXTERNALCALLSTART"):
					onExternalCallStart(params);
					break;

				default:
					log.info("Unhandled event: {}", BitrixEvent.getEvent(params));
					break;
			}
		} catch (BitrixRestApiException e) {
			log.info(e.getMessage());
		} catch (BitrixLocalException e) {
			log.info(e.getMessage());
		}

		return null;
	}

	private String cleanupPhoneNumber(String phone) {
		return phone.replaceAll("[^\\d\\+]", "").replaceAll("^\\+7", "8");
	}

	private void onAppInstall(MultiValueMap<String, String> params) throws BitrixLocalException, BitrixRestApiException {
		BitrixEventOnAppInstall event = new BitrixEventOnAppInstall(params);

		log.info("Handling event: {}", event.getEvent());
		log.debug("Detailed event: {}", event.toString());

		if (bitrixTelephony.isInstalled())
			throw new BitrixLocalException(String.format("application is already installed, to perform new installation, please remove data file (connector.json)", event.getAuthAccessToken()));

		BitrixTelephony btInstall = bitrixTelephony.clone(event);

		for (Event eventName : btInstall.getEvent()) {
			if (!eventName.getEvent().equalsIgnoreCase("ONAPPINSTALL")) {
				log.info("Unbind event {}: {}", eventName.getEvent(), eventName.getHandler());
				btInstall.unbindEvent(eventName);
			}
		}

		for (ExternalLine telephonyLine : btInstall.getExternalLine()) {
			log.info("Deleting external line {}: {}", telephonyLine.getNumber(), telephonyLine.getName());
			btInstall.deleteExternalLine(telephonyLine);
		}

		if ((conf.getBitrix() != null) && (conf.getBitrix().getExternalLines() != null)) {
			for (ConnectorExternalLine line : conf.getBitrix().getExternalLines()) {
				log.info("Registering external line: {}, {}", line.getNumber(), line.getName());
				btInstall.addExternalLine(line.getNumber(), line.getName());
			}
		}

		log.info("Registering event: ONEXTERNALCALLSTART");
		btInstall.bindEvent("ONEXTERNALCALLSTART", "https://connector.ntechs.ru/rest/event");

		bitrixTelephony.afterInstall(btInstall);

		log.info(btInstall.getEvent().toString());
		log.info(btInstall.getExternalLine().toString());
	}

	private void onExternalCallStart(MultiValueMap<String, String> params) throws BitrixLocalException, BitrixRestApiException {
		BitrixEventOnExternalCallStart event = new BitrixEventOnExternalCallStart(params);

		log.info("Handling event: {}", event.getEvent());
		log.debug("Detailed event: {}", event.toString());

		if (!bitrixTelephony.validateAppToken(event))
			throw new BitrixLocalException(String.format("invalid application token: %s", event.getAuthAccessToken()));

		ConnectorExternalLine externalLine = externalLines.get(event.getDataLineNumber());

		if (externalLine == null)
			throw new BitrixLocalException(String.format("unknown requested line number: %s", event.getDataLineNumber()));

		ArrayList<User> usersInfo = bitrixTelephony.getUser(event.getAuthUserId());
		log.info("Bitrix user info: {}", usersInfo.toString());

		if (usersInfo.isEmpty())
			throw new BitrixLocalException("unable to get information about calling agent from Bitrix24");

		User userInfo = usersInfo.get(0);
		userInfo.setUfPhoneInner(cleanupPhoneNumber(userInfo.getUfPhoneInner()));

		Originate originate = new Originate(ami);

		originate.setChannel(MessageFormat.format(externalLine.getChannel(), userInfo.getUfPhoneInner()));
		originate.setContext(externalLine.getContext());
		originate.setExten(MessageFormat.format(externalLine.getExten(), cleanupPhoneNumber(event.getDataPhoneNumber())));
		originate.setPriority(externalLine.getPriority());
		originate.setCallerId(String.format("<%s>", cleanupPhoneNumber(event.getDataPhoneNumber())));
		originate.submit();
	}
}
