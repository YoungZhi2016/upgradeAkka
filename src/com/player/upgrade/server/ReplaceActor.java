package com.player.upgrade.server;

import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.backupReplace.UpgradePlayerImpl;
import com.player.upgrade.detect.DetectionResult;
import com.player.upgrade.player.NoticePlayer;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.UntypedActor;

/**
 * replace file actor
 */
public class ReplaceActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.STEP_REPLACE:
				Logs.info("开始替换文件...");
				String reason = null;
				UpgradePlayerImpl aImpl = UpgradePlayerImpl.getInstance();
				boolean b = aImpl.updateReplace();
				if (!b) {
					reason = "替换失败";
					Logs.info(reason);
					aImpl.rollBackReplace();
				}
				Logs.info("替换已完成");
				// 重启player
				NoticePlayer.tell(CommandUpgrade.UPGRADE_RESULT, new DetectionResult(b, reason), true);
				break;
			}
		}
	}

}
