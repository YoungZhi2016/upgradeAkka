package com.player.upgrade.backupReplace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import com.player.upgrade.utils.Configs;
import com.player.upgrade.utils.Logs;

public class WatchService implements Runnable {

	private static WatchDir aWatchDir;

	static {
		try {
			aWatchDir = new WatchDir(Configs.getPlayerDirPath(), true);
		} catch (IOException e) {
			Logs.error("检测player文件夹失败！" + e.getMessage());
		}
	}

	private WatchService() {
	}

	public static WatchService getInstance() {
		return ServiceHolder.aWatchService;
	}

	@Override
	public void run() {
		aWatchDir.processEvents();
	}

	public Set<Path> getChangFilePath() {
		return aWatchDir.getChangFilePath();
	}

	private static class ServiceHolder {
		private static WatchService aWatchService = new WatchService();
	}

}