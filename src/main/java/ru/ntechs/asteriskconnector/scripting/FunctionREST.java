package ru.ntechs.asteriskconnector.scripting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import ru.ntechs.asteriskconnector.bitrix.BitrixLocalException;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.BitrixTelephony;
import ru.ntechs.asteriskconnector.bitrix.rest.data.User;
import ru.ntechs.asteriskconnector.bitrix.rest.requests.RestRequestUserGet;

@Slf4j
public class FunctionREST extends Function {
	public static final String NAME    = "REST";
	public static final String LC_NAME = "rest";

	private String method;
	private String field;
	private HashMap<String, String> constraints;
	private ArrayList<User> intermediateBeans;

	public FunctionREST(Expression expression, ArrayList<Scalar> params) throws BitrixLocalException {
		super(expression, params);
		init(params);
	}

	private void init(ArrayList<Scalar> params) throws BitrixLocalException {
		if (params.size() < 4)
			throw new BitrixLocalException(String.format("%s doesn't match prototype %s(method, field[, attr, value]...)",
					toString(), NAME));

		this.method = params.get(0).asString();
		this.field = params.get(1).asString();

		this.constraints = new HashMap<>();

		String key = null;
		String val = null;
		int index = 2;

		try {
			while (index < params.size()) {
				key = params.get(index++).asString();
				val = params.get(index++).asString();
				constraints.put(key, val);
			}
		} catch (IndexOutOfBoundsException e) {
			throw new BitrixLocalException(String.format("Constraint value not defined: %s", key));
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Scalar eval() throws IOException, BitrixLocalException {
		switch (method.toLowerCase()) {
			case (RestRequestUserGet.METHOD): return userGet();

			default:
				throw new BitrixLocalException(String.format("Unsupported method: %s", method));
		}
	}

	private Scalar userGet() throws BitrixLocalException {
		ScriptFactory scriptFactory = getScriptFactory();
		BitrixTelephony bitrixTelephony = scriptFactory.getBitrixTelephony();
		String result = null;

		try {
			intermediateBeans = bitrixTelephony.getUser(constraints);

			if ((intermediateBeans == null) || (intermediateBeans.size() == 0)) {
				log.info("Warning: no Bitrix24 user account found, constraints: {}",  constraintsToString());
				return new ScalarString(String.format("$(REST(%s, %s...))", method, field));
			}

			if (intermediateBeans.size() > 1) {
				ArrayList<String> userIds = new ArrayList<>();

				for (User user : intermediateBeans)
					userIds.add(String.valueOf(user.getId()));

				result = "(" + String.join(", ", userIds) + ")";
			}
			else if (intermediateBeans.size() > 0)
				result = String.valueOf(intermediateBeans.get(0).getId());
		} catch (BitrixRestApiException e) {
			ArrayList<String> params = new ArrayList<>();

			constraints.forEach((key, val) -> params.add(key + " => " + val));

			throw new BitrixLocalException(
					String.format("%s: failed method: %s(%s, %s)",
							e.getMessage(), method, field, String.join(", ", params)));
		}

		return new ScalarString(String.format("$(REST(%s, %s...))", method, field), result);
	}

	@Override
	public ArrayList<User> getIntermediateBeans() {
		return intermediateBeans;
	}

	private String constraintsToString() {
		ArrayList<String> strConstr = new ArrayList<>();
		constraints.forEach((key, val) -> strConstr.add(key + " = " + val));
		return String.join(", ", strConstr);
	}
}
// USER_ID: $(REST("user.get", "USER_ID", "UF_PHONE_INNER", $(Channel(${AgentCalled(DestinationChannel)}, "${Newchannel(CallerIDNum)}"))))
// USER_PHONE_INNER: $(Channel(${AgentCalled(DestinationChannel)}, "${Newchannel(CallerIDNum)}"))
