package ru.ntechs.asteriskconnector.bitrix.rest.cache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Entry<T, U> {
	private T request;
	private U result;
	private long ttl;
	private long expires;

	public Entry(T request, U result, long ttl) {
		super();
		this.request = request;
		this.result = result;
		this.ttl = ttl;
		this.expires = System.currentTimeMillis() + ttl * 1000;
	}

	public boolean isExpired() {
		return (System.currentTimeMillis() > expires);
	}
}
