package com.player.upgrade.utils;

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigUtil {

	private final static String ROOT_PATH;

	private Config aConfig;

	static {
		ROOT_PATH = new File("").getAbsolutePath();
	}

	public ConfigUtil() {
		this("upgrade.conf");
	}

	public ConfigUtil(String fileName) {
		aConfig = ConfigFactory.parseFile(new File(getRootPath() + "/config/" + fileName));
	}

	public static String getRootPath() {
		return ROOT_PATH;
	}

	public boolean getBoolean(String val, String def) {
		String str = getString(val);
		boolean bool;
		try {
			if (str == null) {
				str = def;
			}
			bool = Boolean.parseBoolean(str);
		} catch (Exception e) {
			e.printStackTrace();
			bool = Boolean.parseBoolean(def);
		}
		return bool;
	}

	public String getString(String val, String def) {
		String str = getString(val);
		return str == null ? def : str;
	}

	public int getInt(String val, int def) {
		String str = getString(val);
		int type;
		try {
			type = Integer.parseInt(str);
		} catch (Exception e) {
			type = def;
		}
		return type;
	}

	public long getLong(String val, long def) {
		String str = getString(val);
		long type;
		try {
			type = Integer.parseInt(str);
		} catch (Exception e) {
			type = def;
		}
		return type;
	}

	/**
	 * 不同的配置文件格式采用不用的读取方式
	 * 
	 * @param val
	 * @return
	 */
	private String getString(String val) {
		String str = null;
		try {
			str = aConfig.getString(val);
		} catch (Exception e) {
			Logs.error("undefined key: " + val + "---" + e.getMessage());
		}
		return str;
	}

}