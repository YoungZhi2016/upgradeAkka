package com.player.upgrade.backupReplace;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchIndex {

	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	private WatchService aService;

	private WatchIndex() {
		aService = WatchService.getInstance();
	}

	public static WatchIndex getInstansce() {
		return WatcHolder.aWatchIndex;
	}

	public void start() {
		if (!executorService.isShutdown()) {
			executorService.submit(aService);
		}
	}

	public void close() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdownNow();
		}
	}

	public Set<Path> getChangFilePath() {
		return aService.getChangFilePath();
	}

	private static class WatcHolder {
		private static WatchIndex aWatchIndex = new WatchIndex();
	}

}