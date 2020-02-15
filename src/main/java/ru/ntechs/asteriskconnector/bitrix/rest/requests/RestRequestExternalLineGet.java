package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultExternalLineGet;

@Slf4j
@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestExternalLineGet extends RestRequestTemplate {

	public RestRequestExternalLineGet(BitrixAuth auth) {
		super(auth);
	}

	@Override
	public String getMethod() {
		return "telephony.externalLine.get";
	}

	@Override
	public RestResultExternalLineGet exec() throws BitrixRestApiException {
		ResponseEntity<RestResultExternalLineGet> response = restTemplate.postForEntity(getAuth().getMethodUri(getMethod()), this, RestResultExternalLineGet.class);

		String msg = formatErrorMessage(response.getStatusCode(), response.getBody());
		log.debug(msg);
		log.info(response.getBody().toString());

		if (response.getStatusCode().isError())
			throw new BitrixRestApiException(msg);

		return response.getBody();
	}
}
