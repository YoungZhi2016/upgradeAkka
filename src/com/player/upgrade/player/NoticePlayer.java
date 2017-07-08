package com.player.upgrade.player;

import java.util.HashMap;
import java.util.Map;

import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.akka.NoticeUpgrade;
import com.player.upgrade.detect.DetectionResult;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;

/**
 * Notice Player Actor
 */
public class NoticePlayer {

	public static void tell(String action, DetectionResult aResult) {
		tell(action, aResult, false);
	}

	public static void tell(String action, DetectionResult aResult, boolean restart) {
		if (restart) {// 发送更新结果
			// 重启player
			Logs.info("需要重启player");
		}

		if (CommandUpgrade.UPGRADE_RESULT.equals(action)) {// 返回升级结果
			// 清理升级的临时文件
			Intent aIntent = new Intent(CommandUpgrade.STEP_CLEAR);
			NoticeUpgrade.noticeSelf(aIntent, ActorRef.noSender());
		}

		notice(action, aResult);
	}

	private static void notice(String action, DetectionResult aResult) {
		Intent intent = new Intent();
		intent.setAction(action);
		Map<String, Object> aMap = new HashMap<>();
		aMap.put("name", "upgrade");
		aMap.put("result", aResult.isPass());
		if (aResult.getReason() != null) {
			aMap.put("message", aResult.getReason());
		}
		intent.addValue(aMap);
		NoticeUpgrade.noticePlayer(intent);
	}

}
