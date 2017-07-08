package com.player.upgrade.index;

import java.util.concurrent.Executors;

public class UpgradeIndex {

	public static void main(String[] args) {
		start();
	}

	private static void start() {
		// 启动更新程序
		UpgradeService aUpgradeService = new UpgradeService();
		Executors.newSingleThreadExecutor().submit(aUpgradeService);
	}
}