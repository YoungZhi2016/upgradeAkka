package com.player.upgrade.utils;

import org.apache.log4j.Logger;

public final class Logs {

	private static Logger LOGGER;

	static {
		LOGGER = Logger.getRootLogger();
	}

	private Logs() {
	}

	public static void debug(String message) {
		LOGGER.debug(message);
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.warn(message);
	}

	public static void error(String message) {
		LOGGER.error(message);
	}
}