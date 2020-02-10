package ru.ntechs.asteriskconnector.bitrix;

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

@Slf4j
@RestController
@RequestMapping("/rest")
public class BitrixTelephonyController {
	@Autowired
	private BitrixTelephony bitrixTelephony;

	@RequestMapping(method = RequestMethod.POST, path = "/say")
	public String say(@RequestBody String name) {
	    return "Say";
	}
	
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
	public @ResponseBody String appPage(@RequestBody MultiValueMap<String, String> params) throws Exception {
		return "this is start page";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded" }, value = "/event")
	public @ResponseBody String event(@RequestBody MultiValueMap<String, String> params) throws Exception {
		BitrixEvent be = new BitrixEvent(params);
		log.info(be.toString());

		switch (be.getEvent()) {
		case ("ONAPPINSTALL"):
			log.info("Handling event: " + be.getEvent());
			bitrixTelephony.installAuth(be);

			bitrixTelephony.bindEvent(be);
			break;

		default:
			log.info("Unhandled event: " + be.getEvent());
			break;
		}

		bitrixTelephony.getExternalLine();
		return "hello";
	}
}
