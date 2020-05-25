package ru.ntechs.asteriskconnector.bitrix;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BitrixDateDeserializer extends JsonDeserializer<Date> {
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	@Override
	public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		String date = jsonParser.getText();

		if (date.equals("{")) {
			log.info("!!!! date is {}");
			return null;
		}

		try {
		    return format.parse(date);
		} catch (ParseException e) {
		    throw new RuntimeException(e);
		}
	}
}
