package ru.ntechs.asteriskconnector.bitrix;

import java.io.IOException;

public class BitrixRestApiException extends IOException {
	private static final long serialVersionUID = -8074168678174632290L;

//	public BitrixRestApiException(RestResponce responce) {
//		super(responce.toString());
//	}

	public BitrixRestApiException(String format) {
		super(format);
	}
}
