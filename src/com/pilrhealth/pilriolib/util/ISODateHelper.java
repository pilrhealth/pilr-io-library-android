package com.pilrhealth.pilriolib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISODateHelper {

	public static String toString(Date date) {
		// TODO handle time zones
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'TZD'");
		// df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df.format(date);

	}

	public static Date fromString(String str) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'TZD'").parse(str);
		} catch (ParseException e) {
			return null;
		}

	}

}
