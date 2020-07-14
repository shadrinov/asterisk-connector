package ru.ntechs.asteriskconnector.bitrix;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ntechs.asteriskconnector.bitrix.rest.data.CrmDuplicateFindByComm;
import ru.ntechs.asteriskconnector.bitrix.rest.data.Event;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalLine;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.events.BitrixEvent;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmCompanyGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmContactGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmDuplicateFindByComm;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestCrmLeadGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventBind;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestEventUnbind;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineAdd;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineDelete;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalLineGet;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestUserCurrent;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestUserGet;

@Component
public class BitrixTelephony {
	@Autowired
	private BitrixAuth bitrixAuth;

	private BitrixTelephony(BitrixAuth clone) {
		this.bitrixAuth = clone;
	}

	public BitrixTelephony clone(BitrixEvent be) {
		return new BitrixTelephony(bitrixAuth.clone(be));
	}

	public void afterInstall(BitrixTelephony btInstall) {
		bitrixAuth.afterInstall(btInstall.bitrixAuth);
	}

	public boolean validateAppToken(BitrixEvent event) {
		return bitrixAuth.validateAppToken(event);
	}

	public boolean isInstalled() {
		return bitrixAuth.isInstalled();
	}

	public ArrayList<ExternalLine> getExternalLine() throws BitrixRestApiException {
		return new RestRequestExternalLineGet(bitrixAuth).exec().getResult();
	}

	public void addExternalLine(String number, String name) throws BitrixRestApiException {
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

	public User getCurrentUser() throws BitrixRestApiException {
		return new RestRequestUserCurrent(bitrixAuth).exec().getResult();
	}

	public ArrayList<User> getUser(Integer id) throws BitrixRestApiException {
		return new RestRequestUserGet(bitrixAuth, id).exec().getResult();
	}

	public ArrayList<User> getUser(HashMap<String, String> constraints) throws BitrixRestApiException {
		RestRequestUserGet req = new RestRequestUserGet(bitrixAuth);
		req.setFilter(constraints);
		return req.exec().getResult();
	}

	public CrmDuplicateFindByComm findCrmDuplicateByComm(String phone) throws BitrixRestApiException {
		RestRequestCrmDuplicateFindByComm req = new RestRequestCrmDuplicateFindByComm(bitrixAuth, phone);
		return req.exec().getResult();
	}

	public HashMap<String, Object> getCrmLead(Long leadId) throws BitrixRestApiException {
		RestRequestCrmLeadGet req = new RestRequestCrmLeadGet(bitrixAuth, leadId);
		return req.exec().getResult();
	}

	public HashMap<String, Object> getCrmContact(Long contactId) throws BitrixRestApiException {
		RestRequestCrmContactGet req = new RestRequestCrmContactGet(bitrixAuth, contactId);
		return req.exec().getResult();
	}

	public HashMap<String, Object> getCrmCompany(Long companyId) throws BitrixRestApiException {
		RestRequestCrmCompanyGet req = new RestRequestCrmCompanyGet(bitrixAuth, companyId);
		return req.exec().getResult();
	}
}
