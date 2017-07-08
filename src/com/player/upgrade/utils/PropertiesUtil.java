package com.player.upgrade.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum PropertiesUtil {

	LOCAL_HOST("upgrade_local_host"), // upgrade IP
	LOCAL_PORT("upgrade_local_port"), // upgrade port

	PLAYER_AKKA_HOST("upgrade_player_akka_host"), // player IP
	PLAYER_AKKA_PORT("upgrade_player_akka_port"), // player port

	PLUGIN_AKKA_HOST("upgrade_plugin_akka_host"), // plug-in IP
	PLUGIN_AKKA_PORT("upgrade_plugin_akka_port"), // plug-in port

	Test("sda"),

	BACKUP_FILE_DIR("backup_dir_file"), // backup directory and file
	IGNORE_FILE_DIR("ignore_backup_dir_file"), // ignore directory and file
	PLAYER_PATH("player_path");// player path

	private String title;
	private static Properties props;

	private PropertiesUtil(String title) {
		this.title = title;
	}

	// private static final String PROPERTIES = "upgrade.conf";

	private static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");

	static {
		String ROOT_PATH = new File("").getAbsolutePath();

		try {
			props = new Properties();
			InputStream ins = new BufferedInputStream(
					new FileInputStream(new File(ROOT_PATH + "/config/upgrade.conf")));
			props.load(ins);
			ins.close();
		} catch (IOException e) {
			Logs.error("升级程序配置文件读取失败!");
		}
	}

	private String getTitle() {
		return title;
	}

	public static String getTranslate(PropertiesUtil prop, Map<String, String> params) {
		String path = get(prop);
		if (path == null || path.equals("")) {
			return "";
		}
		if (params != null && !params.keySet().isEmpty()) {
			// 需要替换的字段均放入map中，包括需要替换的日期
			for (String key : params.keySet()) {
				path = path.replace("{" + key + "}", params.get(key));
			}
		}

		// 如不指定日期，则替换为当前日期
		Calendar cal = Calendar.getInstance();
		path = path.replace("{year}", cal.get(Calendar.YEAR) + "")
				.replace("{month}",
						cal.get(Calendar.MONTH) + 1 > 9 ? cal.get(Calendar.MONTH) + 1 + ""
								: "0" + (cal.get(Calendar.MONTH) + 1))
				.replace("{date}",
						cal.get(Calendar.DATE) > 9 ? cal.get(Calendar.DATE) + "" : "0" + cal.get(Calendar.DATE));

		return path;
	}

	public static String get(PropertiesUtil prop) {
		String value = props.getProperty(prop.getTitle());
		return value == null ? null : loop(value);
	}

	@SuppressWarnings("static-access")
	private static String loop(String key) {
		// 定义matcher匹配其中的路径变量
		Matcher matcher = PATTERN.matcher(key);
		StringBuffer buffer = new StringBuffer();
		boolean flag = false;
		while (matcher.find()) {
			String matcherKey = matcher.group(1);// 依次替换匹配到的路径变量
			String matchervalue = props.getProperty(matcherKey);
			if (matchervalue != null) {
				matcher.appendReplacement(buffer, matcher.quoteReplacement(matchervalue));// quoteReplacement方法对字符串中特殊字符进行转化
				flag = true;
			}
		}
		matcher.appendTail(buffer);
		// flag为false时说明已经匹配不到路径变量，则不需要再递归查找
		return flag ? loop(buffer.toString()) : key;
	}

}