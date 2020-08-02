package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallAttachRecord;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;
import ru.ntechs.asteriskconnector.eventchain.EventNode;

@Slf4j
public class MethodExternalCallAttachRecord extends Method {
	public static final String NAME = RestRequestExternalCallAttachRecord.METHOD;

	public MethodExternalCallAttachRecord(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, EventNode node) {
		super(scriptFactory, eventChain, action, node);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> params = evaluate(getAction().getParams());

		try {
			String callId = null;
			String filename = null;
			String fileContent = null;

			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);
			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;

			if (params.containsKey("CALL_ID"))
				callId = params.get("CALL_ID").asString();
			else if (firstCall != null)
				callId = firstCall.getCallId();

			if (params.containsKey("FILENAME"))
				filename = params.get("FILENAME").asString();

			if (params.containsKey("FILE_CONTENT"))
				fileContent = params.get("FILE_CONTENT").asString();

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (filename == null)
				throw new BitrixLocalException("Required parameter is not defined: FILENAME");

			if (fileContent == null)
				throw new BitrixLocalException("Required parameter is not defined: FILE_CONTENT");

			RestRequestExternalCallAttachRecord req = new RestRequestExternalCallAttachRecord(getAuth(), callId, filename);

			if (params.containsKey("RECORD_URL"))
				req.setRecordURL(params.get("RECORD_URL").asString());

			req.setFileContent(fileContent);
			req.exec();
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
