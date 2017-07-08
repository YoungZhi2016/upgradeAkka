package com.player.upgrade.server;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.detect.Detect;
import com.player.upgrade.detect.DetectionResult;
import com.player.upgrade.detect.ManifestInfo;
import com.player.upgrade.player.NoticePlayer;
import com.player.upgrade.player.Player;
import com.player.upgrade.utils.Configs;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import scala.concurrent.duration.Duration;

/**
 * 升级环境检测actor
 */
public class EnvironmentDetectActor extends UntypedActor {

	private final ActorRef actor_unzip = getContext()
			.actorOf(Props.create(UnZipPackActor.class).withRouter(new RoundRobinPool(3)), "unzip-actor");

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), throwable -> {
			if (throwable instanceof Exception) {
				Logs.warn("EnvironmentDetectActor restart UnZipPackActor！" + throwable.getMessage());
				return SupervisorStrategy.restart();
			} else {
				Logs.warn("EnvironmentDetectActor escalate！" + throwable.getMessage());
				return SupervisorStrategy.escalate();
			}
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.STEP_ENVIRONMENT:
				Logs.info("正在验证升级必要条件...");
				DetectionResult aResult = detectEnvironment();
				if (aResult.isPass()) {// pass
					Logs.info("环境检测通过");
					aIntent.setAction(CommandUpgrade.STEP_UNZIP);
					actor_unzip.tell(aIntent, getSelf());
				} else {
					Logs.info("升级条件不符合");
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

	/**
	 * detect Environment
	 */
	private DetectionResult detectEnvironment() {
		DetectionResult aResult = new DetectionResult();
		aResult.setPass(false);
		String message;

		// 升级程序的清单文件是否存在
		String manifestFilePath = Configs.getManifestFilePath();
		if (manifestFilePath == null || !Detect.detectManifestFilExist(manifestFilePath)) {
			message = "更新程序的清单文件找不到!";
			Logs.info(message);
			aResult.setReason(message);
			return aResult;
		}

		ManifestInfo aInfo = ManifestInfo.getInstance();
		if (aInfo == null || aInfo.getUpgradeVersion() == null
				|| aInfo.getUpgradeVersion().equals(Player.getCurrentVersion())) {
			message = "清单文件解析失败或版本一致!";
			Logs.info(message);
			aResult.setReason(message);
			return aResult;
		}

		if (!Detect.detectDiskSpace(1024)) {
			message = "磁盘空间小于1G,清理空间再重试!";
			Logs.info(message);
			aResult.setReason(message);
			return aResult;
		}

		String packPath = ManifestInfo.getInstance().getUpdatePackPath();
		if (!Files.exists(Paths.get(packPath))) { // 没有更新包文件
			message = "找不到更新包!";
			Logs.info(message);
			aResult.setReason(message);
			return aResult;
		}
		aResult.setPass(true);
		return aResult;
	}

}
