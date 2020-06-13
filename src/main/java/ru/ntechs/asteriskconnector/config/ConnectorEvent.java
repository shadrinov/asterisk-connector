package ru.ntechs.asteriskconnector.config;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectorEvent {
	private String name;
	private HashMap<String, String> constraints;

	@Override
	public String toString() {
		return name;
	}
}
