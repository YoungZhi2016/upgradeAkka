package com.player.upgrade.server;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.detect.DetectionResult;
import com.player.upgrade.detect.ManifestInfo;
import com.player.upgrade.player.NoticePlayer;
import com.player.upgrade.utils.Configs;
import com.player.upgrade.utils.Logs;
import com.player.upgrade.utils.MD5Util;
import com.player.upgrade.utils.ZIPUtil;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import scala.concurrent.duration.Duration;

public class UnZipPackActor extends UntypedActor {

	private final ActorRef actor_backup = getContext()
			.actorOf(Props.create(BackupActor.class).withRouter(new RoundRobinPool(2)), "backup-actor");

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), throwable -> {
			if (throwable instanceof Exception) {
				Logs.warn("UnZipPackActor restart BackupActor！" + throwable.getMessage());
				return SupervisorStrategy.restart();
			} else {
				Logs.warn("UnZipPackActor escalate！" + throwable.getMessage());
				return SupervisorStrategy.escalate();
			}
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.STEP_UNZIP:
				Logs.info("开始解压更新包文件");
				DetectionResult aResult = unZipPack();
				if (aResult.isPass()) {// 解压成功
					Logs.info("解压更新包成功");
					// 关闭player
					NoticePlayer.tell(CommandUpgrade.PLAYER_SHUTDOWN, aResult);

					// 备份player
					aIntent.setAction(CommandUpgrade.STEP_BACKUP);
					actor_backup.tell(aIntent, getSelf());
				} else {
					NoticePlayer.tell(CommandUpgrade.UPGRADE_RESULT, aResult);
				}
				break;
			case CommandUpgrade.RECEIVE:
			default:
				return;
			}
			feedback();
		}
	}

	private void feedback() {
		ActorRef actorRef = getSender();
		if (actorRef != null) {
			Intent aIntent = new Intent(CommandUpgrade.RECEIVE);
			actorRef.tell(aIntent, getSelf());
		}
	}

	private DetectionResult unZipPack() throws Exception {
		DetectionResult aResult = new DetectionResult();
		aResult.setPass(false);
		String message;

		String zipFilePath = ManifestInfo.getInstance().getUpdatePackPath();

		// 校验文件完整性
		String md5 = ManifestInfo.getInstance().getMD5();
		if (md5 == null || !md5.equals(MD5Util.getMd5ByFile(new File(zipFilePath)))) {
			message = "md5值不一致";
			Logs.error(message);
			aResult.setReason(message);
			return aResult;
		}

		// unZip
		boolean unZ = ZIPUtil.unZip(zipFilePath, Configs.getUpdatePackUnDirPath().toString());
		if (!unZ) {
			message = "解压失败";
			Logs.error(message);
			aResult.setReason(message);
			return aResult;
		}
		aResult.setPass(true);
		return aResult;
	}

}
