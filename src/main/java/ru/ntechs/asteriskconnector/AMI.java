package ru.ntechs.asteriskconnector;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.config.ConnectorAmi;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;

@Slf4j
@Component
public class AMI extends ru.ntechs.ami.AMI {
	public AMI(ConnectorConfig conf) {
		super();

		ConnectorAmi confAmi = conf.getAmi();

		if (confAmi.getHostname() == null) {
			log.error("ami.hostname not defined");
			return;
		}

		if (confAmi.getPort() == null) {
			log.error("ami.port not defined");
			return;
		}

		if (confAmi.getUsername() == null) {
			log.error("ami.username not defined");
			return;
		}

		if (confAmi.getPassword() == null) {
			log.error("ami.password not defined");
			return;
		}

		setHostname(confAmi.getHostname());
		setPort(confAmi.getPort());
		setUsername(confAmi.getUsername());
		setPassword(confAmi.getPassword());

		if (confAmi.isDebug())
			enableDebug();

		start();
	}
}
