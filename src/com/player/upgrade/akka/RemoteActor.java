package com.player.upgrade.akka;

import java.util.concurrent.TimeUnit;

import com.player.upgrade.server.EnvironmentDetectActor;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

public class RemoteActor extends UntypedActor {

	private final ActorRef actor_environment = getContext().actorOf(Props.create(EnvironmentDetectActor.class),
			"environment-actor");

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return new OneForOneStrategy(3, Duration.create(1, TimeUnit.MINUTES), throwable -> {
			if (throwable instanceof Exception) {
				Logs.warn("RemoteActor restart EnvironmentDetectActor！" + throwable.getMessage());
				return SupervisorStrategy.restart();
			} else {
				Logs.warn("RemoteActor escalate！" + throwable.getMessage());
				return SupervisorStrategy.escalate();
			}
		});
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		Logs.info("upgradeRemoteActor onReceive: " + message);
		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.UPGRADE:
				Logs.info("receive upgrade comm: " + aIntent.toString());
				aIntent.setAction(CommandUpgrade.STEP_ENVIRONMENT);
				actor_environment.tell(aIntent, getSelf());
				break;
			}
		}
		feedback();
	}

	/**
	 * feedback
	 */
	private void feedback() {
		ActorRef actorRef = getSender();
		if (actorRef != null) {
			Intent aIntent = new Intent(CommandUpgrade.RECEIVE);
			actorRef.tell(aIntent, ActorRef.noSender());
		}
	}

}