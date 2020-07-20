package ru.ntechs.asteriskconnector.bitrix.rest.cache;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequest;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResult;

@Slf4j
public class Cache<T extends RestRequest, U extends RestResult> {
	private final static long DEFAULT_TTL = 1800l;

	private long ttl;
	private long nextGC;
	private HashMap<T, Entry<T, U>> cache = new HashMap<>();

	public Cache() {
		super();
		this.ttl = DEFAULT_TTL;
		this.nextGC = System.currentTimeMillis() + ttl;
	}

	public Cache(long ttl) {
		super();
		this.ttl = ttl;
		this.nextGC = System.currentTimeMillis() + ttl;
	}

	public synchronized U put(T request, U result) {
		long time = System.currentTimeMillis();

		if (time > nextGC) {
			ArrayList<T> list = new ArrayList<>();

			cache.forEach((req, entry) -> {
				if (entry.isExpired())
					list.add(req);
			});

			for (T req : list)
				cache.remove(req);

			nextGC = time + ttl;
		}

		cache.put(request, new Entry<>(request, result, ttl));
		return result;
	}

	public synchronized U get(T request) {
		Entry<T, U> entry = cache.get(request);

		if ((entry != null) && !entry.isExpired()) {
			log.info("cache hit: {}", entry.getRequest().getMethod());
			return entry.getResult();
		}

		return null;
	}
}
