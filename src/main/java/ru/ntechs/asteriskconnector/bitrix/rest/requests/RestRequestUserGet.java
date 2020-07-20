package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.cache.Cache;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultUserGet;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RestRequestUserGet extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "user.get";

	@JsonIgnore
	private static Cache<RestRequestUserGet, RestResultUserGet> cache = new Cache<>();

	@EqualsAndHashCode.Include
	private Integer id;

	@EqualsAndHashCode.Include
	private HashMap<String, String> filter;

	public RestRequestUserGet(BitrixAuth auth) {
		super(auth);

		this.id = null;
		this.filter = null;
	}

	public RestRequestUserGet(BitrixAuth auth, Integer id) {
		super(auth);

		this.id = id;
		this.filter = null;
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultUserGet exec() throws BitrixRestApiException {
		RestResultUserGet result = cache.get(this);

		if (result == null)
			result = cache.put(this, exec(RestResultUserGet.class));

		return result;
	}
}
