package ru.ntechs.asteriskconnector.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.ami.Message;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.data.ExternalCall;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestExternalCallAttachRecord;
import ru.ntechs.asteriskconnector.config.ConnectorAction;
import ru.ntechs.asteriskconnector.eventchain.EventChain;

@Slf4j
public class MethodExternalCallAttachRecord extends Method {

	public MethodExternalCallAttachRecord(ScriptFactory scriptFactory, EventChain eventChain, ConnectorAction action, Message message) {
		super(scriptFactory, eventChain, action, message);
	}

	@Override
	public void exec() {
		HashMap<String, Scalar> data = evaluate(getEventDispatcher(), getEventChain(), getAction().getData());

		try {
			log.info("source: {}", (getAction().getData() != null) ? getAction().getData().toString() : "null");
			log.info("evaluated: {}", ParamsToString(data));

			String callId = null;
			String filename = null;
			String fileContent = null;

			ArrayList<ExternalCall> calls = getContext().get(ExternalCall.class);
			ExternalCall firstCall = (calls.size() > 0) ? calls.get(0) : null;

			if (data.containsKey("CALL_ID"))
				callId = data.get("CALL_ID").asString();
			else if (firstCall != null)
				callId = firstCall.getCallId();

			if (data.containsKey("FILENAME"))
				filename = data.get("FILENAME").asString();

			if (data.containsKey("FILE_CONTENT"))
				fileContent = data.get("FILE_CONTENT").asString();

			if (callId == null)
				throw new BitrixLocalException("Required parameter is not defined: CALL_ID");

			if (filename == null)
				throw new BitrixLocalException("Required parameter is not defined: FILENAME");

			if (fileContent == null)
				throw new BitrixLocalException("Required parameter is not defined: FILE_CONTENT");

			RestRequestExternalCallAttachRecord req = new RestRequestExternalCallAttachRecord(getAuth(), callId, filename);

			if (data.containsKey("RECORD_URL"))
				req.setRecordURL(data.get("RECORD_URL").asString());

			req.setFileContent(fileContent);
			req.exec();
		} catch (BitrixRestApiException | BitrixLocalException e) {
			log.info(e.getMessage());
		}
	}

	private String ParamsToString(HashMap<String, Scalar> params) {
		ArrayList<String> elements = new ArrayList<>();

		params.forEach(new BiConsumer<String, Scalar>() {
			@Override
			public void accept(String t, Scalar u) {
				if (!t.equals("FILE_CONTENT"))
				elements.add(String.format("%s=%s", t, u));
			}
		});

		return String.join(", ", elements);
	}
}
