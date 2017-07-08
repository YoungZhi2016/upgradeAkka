package com.player.upgrade.detect;

/**
 * upgrade environment detection
 */
public class EnvironmentUpgrade {

	private EnvironmentUpgrade() {
	}

	public static EnvironmentUpgrade getInstance() {
		return EnvironmentUpgradeHolder.aEnvironmentUpgrade;
	}

	/**
	 * @param ip
	 *            detect network
	 * @param minSpace
	 *            detect freeSpace
	 */
	public DetectionResult getDetectResult(String ip, long minSpace) {
		boolean isPass = true;
		StringBuffer reasonBuff = new StringBuffer();
		if (!Detect.detectDiskSpace(minSpace)) {
			reasonBuff.append("磁盘空间不足!");
			isPass = false;
		}
		if (!Detect.detectNetwork(ip)) {
			reasonBuff.append("网络连接错误!");
			isPass = false;
		}
		return new DetectionResult(isPass, reasonBuff.toString());
	}

	private static class EnvironmentUpgradeHolder {
		private static EnvironmentUpgrade aEnvironmentUpgrade = new EnvironmentUpgrade();
	}
}