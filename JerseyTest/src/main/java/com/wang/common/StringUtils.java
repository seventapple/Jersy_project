package com.wang.common;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdk.nashorn.internal.ir.ObjectNode;

public class StringUtils {
	public static boolean isNull(String str) {
		return str == null || str.length() == 0;
	}

	public static int parseInt(String str) {
		if (isNull(str)) {
			return 0;
		}
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// get current timestamp without timezone
	public static Timestamp getCurrentUtc() {
		Instant instant = Instant.now();
		ZoneId zone = ZoneId.of("UTC");
		return Timestamp.valueOf(LocalDateTime.ofInstant(instant, zone));
	}

	// get current timestamp with timezone
	public static Timestamp getCurrenTimestamp() {
		LocalDateTime locDate = LocalDateTime.now();
		return Timestamp.valueOf(locDate);
	}

	public static String beanToJsonString(Object bean) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			return "{}";
		}
	}

	public static String underLineWord2Upper(String str) {
		StringBuffer newStr = new StringBuffer();
		final String UNDERLINE = "_";
		int firstIndex = 0;
		while (str.indexOf(UNDERLINE) != -1) {
			firstIndex = str.indexOf(UNDERLINE);
			if (firstIndex != str.length()) {
				newStr = newStr.append(str.substring(0, firstIndex));
				str = str.substring(firstIndex + 1, str.length());
				str = firstWord2Uppere(str);
			}
		}
		newStr = newStr.append(str);
		return newStr.toString();
	}

	public static String firstWord2Uppere(String str) {
		char beanProperty[] = str.toCharArray();
		beanProperty[0] = Character.toUpperCase(beanProperty[0]);
		return new String(beanProperty);
	}

	public static ObjectNode strToObjectNode(String str) {
//		if(isNull(str)) {
//			return null;
//		}
//		ObjectMapper mapper=new ObjectMapper();
//		try {
//			return (ObjectNode) mapper.readTree(str);
//		}catch(Exception e) {
		return null;
//		}
	}

	public static <T> T strToBean(String str, Class<T> clazz) {
		if (isNull(str)) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(str, clazz);
		} catch (Exception e) {
			return null;
		}
	}

}
