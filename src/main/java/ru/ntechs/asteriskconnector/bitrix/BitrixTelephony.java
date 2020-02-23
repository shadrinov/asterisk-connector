package ru.ntechs.asteriskconnector.bitrix;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Event;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalLine;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEvent;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventBind;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventUnbind;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineAdd;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineDelete;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineGet;
import ru.ntechs.asteriskconnector.config.ConnectorConfig;

@Slf4j
@Component
public class BitrixTelephony {

	@Autowired
	private BitrixAuth bitrixAuth;

	public BitrixTelephony(ConnectorConfig config) {
	}

	public void installAuth(BitrixEvent be) {
		bitrixAuth.installAuth(be);
	}

	public void registerCall() {
	}

	public ArrayList<ExternalLine> getExternalLine() throws BitrixRestApiException {
		return new RestRequestExternalLineGet(bitrixAuth).exec().getResult();
	}

	public void addExternalLine(Integer number, String name) throws BitrixRestApiException {
		new RestRequestExternalLineAdd(bitrixAuth, number, name).exec();
	}

	public void deleteExternalLine(ExternalLine telephonyLine) throws BitrixRestApiException {
		new RestRequestExternalLineDelete(bitrixAuth, telephonyLine).exec();
	}

	public ArrayList<Event> getEvent() throws BitrixRestApiException {
		return new RestRequestEventGet(bitrixAuth).exec().getResult();
	}

	public void bindEvent(String event, String handler) throws BitrixRestApiException {
		new RestRequestEventBind(bitrixAuth, event, handler).exec();
	}

	public void unbindEvent(Event event) throws BitrixRestApiException {
		new RestRequestEventUnbind(bitrixAuth, event).exec();
	}
}
