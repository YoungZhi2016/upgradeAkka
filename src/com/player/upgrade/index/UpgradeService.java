package com.player.upgrade.index;

import com.player.upgrade.akka.AkkaRemoteSystem;
import com.player.upgrade.akka.CommandUpgrade;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;

public class UpgradeService implements Runnable {

	@Override
	public void run() {
		Logs.info("启动更新程序");
		AkkaRemoteSystem.init();
		Intent aIntent = new Intent(CommandUpgrade.UPGRADE);
		AkkaRemoteSystem.ACTOR_REMOTE.tell(aIntent, ActorRef.noSender());
	}

}