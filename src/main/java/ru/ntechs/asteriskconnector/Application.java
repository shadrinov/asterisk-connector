package ru.ntechs.asteriskconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.ntechs.ami.AMI;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;
import ru.ntechs.asteriskconnector.config.ConnectorEvent;

@SpringBootApplication(scanBasePackages={"ru.ntechs.asteriskconnector", "ru.ntechs.ami"})
public class Application implements CommandLineRunner {
	static private Integer tickCount = 0;

	@Autowired
	private MainLoop mainLoop;

	@Autowired
	private ConnectorConfig config;

	@Autowired
	private AMI ami;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		for (ConnectorEvent event : config.getRules()) {
			System.out.println(event);
		}

		mainLoop.run();
	}

	public static Integer getTickCount() {
		return tickCount;
	}
}
