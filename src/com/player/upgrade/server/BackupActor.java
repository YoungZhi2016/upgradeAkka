package com.player.upgrade.server;

import java.util.concurrent.TimeUnit;

import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.backupReplace.UpgradePlayerImpl;
import com.player.upgrade.detect.DetectionResult;
import com.player.upgrade.player.NoticePlayer;
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
 * 备份文件actor
 */
public class BackupActor extends UntypedActor {

	private final ActorRef actor_replace = getContext()
			.actorOf(Props.create(ReplaceActor.class).withRouter(new RoundRobinPool(2)), "replace-actor");

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), throwable -> {
			if (throwable instanceof Exception) {
				Logs.warn("BackupActor restart ReplaceActor！" + throwable.getMessage());
				return SupervisorStrategy.restart();
			} else {
				Logs.warn("BackupActor escalate！" + throwable.getMessage());
				return SupervisorStrategy.escalate();
			}
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.STEP_BACKUP:
				Logs.info("开始备份文件...");
				boolean b = UpgradePlayerImpl.getInstance().backuPlayer();
				if (b) {
					Logs.info("备份已完成!");
					aIntent.setAction(CommandUpgrade.STEP_REPLACE);
					actor_replace.tell(aIntent, getSelf());
				} else {
					String reason = "备份失败";
					Logs.info(reason);
					NoticePlayer.tell(CommandUpgrade.UPGRADE_RESULT, new DetectionResult(b, reason), true);
				}
				break;
			}
		}
	}
}
