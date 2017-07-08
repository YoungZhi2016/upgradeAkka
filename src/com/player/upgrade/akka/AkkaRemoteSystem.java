package com.player.upgrade.akka;

import com.player.upgrade.utils.Configs;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

public class AkkaRemoteSystem {

	public static final ActorSystem ACTOR_SYSTEM;
	public static final ActorRef ACTOR_REMOTE;
	public static final ActorRef ACTOR_SENDER;

	static {
		ACTOR_SYSTEM = ActorSystem.create("RemoteSystem",
				LocalActorConfig.getConfig(Configs.getUpgradeAkkaIp(), Configs.getUpgradeAkkaPort()));

		// .withRouter(new RoundRobinPool(20)
		ACTOR_REMOTE = ACTOR_SYSTEM.actorOf(Props.create(RemoteActor.class).withRouter(new RoundRobinPool(20)),
				"remote");
		ACTOR_SENDER = ACTOR_SYSTEM.actorOf(Props.create(SenderActor.class).withRouter(new RoundRobinPool(20)),
				"sender");
	}

	public static void init() {

	}

	public static ActorSelection getRemoteRef(String ip, int port) {
		StringBuilder comm = new StringBuilder("akka.tcp://RemoteSystem@");
		comm.append(ip);
		comm.append(":");
		comm.append(port);
		comm.append("/user/remote");
		return ACTOR_SYSTEM.actorSelection(comm.toString());
	}

}