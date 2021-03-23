package com.wang.common;

import org.apache.log4j.*;

public class LogTest {
	private static Logger logger = Logger.getLogger(LogTest.class);

	public static void main(String[] args) {
		logger.debug("This is a debug msg.");
		logger.info("This is a info msg.");
		logger.error("This is a error msg.");
	}
}
