package ru.ntechs.asteriskconnector.bitrix.rest.requests;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ntechs.asteriskconnector.bitrix.BitrixAuth;
import ru.ntechs.asteriskconnector.bitrix.BitrixRestApiException;
import ru.ntechs.asteriskconnector.bitrix.rest.results.RestResultCrmDuplicateFindByComm;

@Getter
@Setter
@ToString(callSuper = true)
public class RestRequestCrmDuplicateFindByComm extends RestRequest {
	@JsonIgnore
	public static final String METHOD = "crm.duplicate.findbycomm";

	@JsonIgnore
	public final static String COMM_TYPE_EMAIL = "EMAIL";
	@JsonIgnore
	public final static String COMM_TYPE_PHONE = "PHONE";

	@JsonIgnore
	public final static String ENTITY_TYPE_LEAD = "LEAD";
	@JsonIgnore
	public final static String ENTITY_TYPE_CONTACT = "CONTACT";
	@JsonIgnore
	public final static String ENTITY_TYPE_COMPANY = "COMPANY";

	private String type;
	private ArrayList<String> values;
	private String entity_type;

	public RestRequestCrmDuplicateFindByComm(BitrixAuth auth) {
		super(auth);
		this.type = COMM_TYPE_PHONE;
	}

	public RestRequestCrmDuplicateFindByComm(BitrixAuth auth, String phone) {
		super(auth);
		this.type = COMM_TYPE_PHONE;

		if (phone != null) {
			values = new ArrayList<>();
			values.add(phone);
		}
	}

	@Override
	public String getMethod() {
		return METHOD;
	}

	public RestResultCrmDuplicateFindByComm exec() throws BitrixRestApiException {
		return exec(RestResultCrmDuplicateFindByComm.class);
	}
}
