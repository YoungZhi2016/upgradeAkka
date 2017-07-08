package com.player.upgrade.detect;

import com.fasterxml.jackson.databind.JsonNode;
import com.player.upgrade.utils.Configs;
import com.player.upgrade.utils.JsonUtil;
import com.player.upgrade.utils.Logs;

/**
 * manifest file info
 */
public final class ManifestInfo {

	private JsonNode manifestNode;

	private ManifestInfo() {
		try {
			manifestNode = JsonUtil.getJsonNodeByFilePath(Configs.getManifestFilePath());
		} catch (Exception e) {
			Logs.error("manifest file parsing failure" + e.getMessage());
			manifestNode = null;
		}
	}

	public static ManifestInfo getInstance() {
		return ManifestInfoHolder.aManifestInfo;
	}

	public JsonNode getManifestNode() {
		return manifestNode;
	}

	public String getUpgradeVersion() {
		return manifestNode.has("version") ? manifestNode.findValue("version").asText() : null;
	}

	public String getMD5() {
		return manifestNode.has("md5") ? manifestNode.findValue("md5").asText() : null;
	}

	/**
	 * update pack file path
	 */
	public String getUpdatePackPath() {
		return manifestNode.has("packPath") ? manifestNode.findValue("packPath").asText() : null;
	}

	private static class ManifestInfoHolder {
		private static ManifestInfo aManifestInfo = new ManifestInfo();
	}

}