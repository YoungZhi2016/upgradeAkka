package com.player.upgrade.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 
 * @author ywkj config
 *
 */
public class Configs {

	/**
	 * PropertiesUtil
	 */
	private static String getPropertieString(PropertiesUtil propertiesUtil, String defaultValue) {
		String host = PropertiesUtil.get(propertiesUtil);
		return host == null ? defaultValue : host;
	}

	/**
	 * PropertiesUtil
	 */
	private static int getPropertieInt(PropertiesUtil propertiesUtil, int defaultValue) {
		String host = PropertiesUtil.get(propertiesUtil);
		return host == null ? defaultValue : Integer.parseInt(host);
	}

	/**
	 * ManifestFile DIR path
	 */
	public static Path getManifestFileDirPath() {
		return getPlayerDirPath().resolve("upgradeManifest");
	}

	/**
	 * ManifestFile name
	 */
	public static String getManifestFileName() {
		return "ManifestFile.json";
	}

	/**
	 * @return manifest file path
	 */
	public static String getManifestFilePath() {
		return getManifestFileDirPath().resolve(getManifestFileName()).toString();
	}

	/**
	 * @return backup directory or file
	 * 
	 */
	public static Set<String> getBackupDirFile() {
		String back = getPropertieString(PropertiesUtil.BACKUP_FILE_DIR, null);
		Set<String> set = null;
		if (back != null) {
			set = new HashSet<>();
			StringTokenizer aTokenizer = new StringTokenizer(back, ",");
			while (aTokenizer.hasMoreTokens()) {
				set.add(aTokenizer.nextToken());
			}
		}
		return set;
	}

	/**
	 * @return ignore directory or file
	 *
	 */
	public static Set<String> getIgnoreDirFile() {
		String ignore = getPropertieString(PropertiesUtil.IGNORE_FILE_DIR, null);
		Set<String> set = null;
		if (ignore != null) {
			set = new HashSet<>();
			StringTokenizer aTokenizer = new StringTokenizer(ignore, ",");
			while (aTokenizer.hasMoreTokens()) {
				set.add(aTokenizer.nextToken());
			}
		}
		return set;
	}

	/**
	 * Player DIR String
	 */
	public static String getPlayerDirString() {
		return getPropertieString(PropertiesUtil.PLAYER_PATH, ConfigUtil.getRootPath());
	}

	/**
	 * player Path
	 */
	public static Path getPlayerDirPath() {
		return Paths.get(getPlayerDirString());
	}

	/**
	 * player Parent Path
	 */
	public static Path getPlayerParentDirPath() {
		return getPlayerDirPath().getParent();
	}

	/**
	 * player Backup DIR Path
	 */
	public static Path getBackupDirPath() {
		return getPlayerParentDirPath().resolve("playerBackupDir");
	}

	/**
	 * @return update pack unzip DIR
	 */
	public static Path getUpdatePackUnDirPath() {
		return getPlayerParentDirPath().resolve("playerUpdatePackUnZip");
	}

	/*----------------------------------AKKA-Configuration-----------------------------------------------------------*/

	public static String getUpgradeAkkaIp() {
		return getPropertieString(PropertiesUtil.LOCAL_HOST, "127.0.0.1");
	}

	public static int getUpgradeAkkaPort() {
		return getPropertieInt(PropertiesUtil.LOCAL_PORT, 6002);
	}

	public static String getPluginsAkkaIp() {
		return getPropertieString(PropertiesUtil.PLUGIN_AKKA_HOST, "127.0.0.1");
	}

	public static int getPluginsAkkaPort() {
		return getPropertieInt(PropertiesUtil.PLUGIN_AKKA_PORT, 6001);
	}

	public static String getPlayerAkkaIp() {
		return getPropertieString(PropertiesUtil.PLAYER_AKKA_HOST, "127.0.0.1");
	}

	public static int getPlayerAkkaPort() {
		return getPropertieInt(PropertiesUtil.PLAYER_AKKA_PORT, 6000);
	}

}