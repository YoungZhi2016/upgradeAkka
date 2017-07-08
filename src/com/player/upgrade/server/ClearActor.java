package com.player.upgrade.server;

import com.player.upgrade.akka.AkkaRemoteSystem;
import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.backupReplace.UpgradePlayerImpl;
import com.player.upgrade.detect.ManifestInfo;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.UntypedActor;

public class ClearActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.STEP_CLEAR:
				Logs.info("正在清理升级临时文件...");
				clear(false);
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 
	 * @param deletePackFile
	 *            true->delete pack file
	 */
	private void clear(boolean deletePackFile) {
		boolean res = false;
		if (deletePackFile) {
			res = UpgradePlayerImpl.getInstance().cleanJunkFiles(ManifestInfo.getInstance().getUpdatePackPath());
		} else {
			res = UpgradePlayerImpl.getInstance().cleanJunkFiles();
		}
		Logs.info("clear: " + res);
		AkkaRemoteSystem.ACTOR_SYSTEM.terminate();
	}

}
