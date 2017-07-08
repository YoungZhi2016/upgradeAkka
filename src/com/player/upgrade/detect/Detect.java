package com.player.upgrade.detect;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.player.upgrade.utils.ConfigUtil;

/**
 * detection network
 */
public class Detect {

	/**
	 * detect network can be connected
	 */
	public static boolean detectNetwork(String remoteInetAddr) {
		boolean reachable = false;
		try {
			// timeout: 5s
			reachable = InetAddress.getByName(remoteInetAddr).isReachable(5000);
		} catch (Exception e) {
		}
		return reachable;
	}

	/**
	 * detect disk space Is less than minSpace unit MB
	 */
	public static boolean detectDiskSpace(long minSpace) {
		File aFile = new File(ConfigUtil.getRootPath());
		long kb = 1024;
		long freeMB = aFile.getFreeSpace() / (kb * kb);
		return freeMB >= minSpace;
	}

	/**
	 * detect manifest file exist
	 */
	public static boolean detectManifestFilExist(String manifestFilePath) {
		return Files.exists(Paths.get(manifestFilePath));
	}
}