package com.player.upgrade.akka;

import java.util.concurrent.TimeUnit;

import com.player.upgrade.utils.Configs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class NoticeUpgrade {

	public static void noticeSelf(Intent aIntent, ActorRef actorRef) {
		AkkaRemoteSystem.ACTOR_SENDER.tell(aIntent, actorRef);
	}

	/**
	 * notice plug-in
	 */
	public static void noticePlugins(Intent aIntent) {
		notice(Configs.getPluginsAkkaIp(), Configs.getPluginsAkkaPort(), aIntent);
	}

	/**
	 * notice player
	 */
	public static void noticePlayer(Intent aIntent) {
		notice(Configs.getPlayerAkkaIp(), Configs.getPlayerAkkaPort(), aIntent);
	}

	/**
	 * notice
	 */
	public static void notice(String ip, int port, Intent aIntent) {
		AkkaRemoteSystem.getRemoteRef(ip, port).tell(aIntent, AkkaRemoteSystem.ACTOR_SENDER);
	}

	/**
	 * ask
	 */
	public static void ask(String ip, int port, Intent aIntent, Timeout aTimeout) {
		Patterns.ask(AkkaRemoteSystem.getRemoteRef(ip, port), aIntent, aTimeout);
	}

	/**
	 * notice
	 */
	public static void ask(String ip, int port, Intent aIntent) {
		Timeout aTimeout = new Timeout(5, TimeUnit.SECONDS);
		ask(ip, port, aIntent, aTimeout);
	}
}
