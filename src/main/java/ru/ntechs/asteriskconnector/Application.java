package ru.ntechs.asteriskconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"ru.ntechs.asteriskconnector"})
public class Application implements CommandLineRunner {

	@Autowired
	private MainLoop mainLoop;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mainLoop.run();
	}
}
