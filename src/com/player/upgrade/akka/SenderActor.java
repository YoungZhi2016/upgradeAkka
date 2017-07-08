package com.player.upgrade.akka;

import java.util.Iterator;
import java.util.Map;

import com.player.upgrade.server.ClearActor;
import com.player.upgrade.utils.Logs;
import com.winonetech.jhpplugins.akka.Intent;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class SenderActor extends UntypedActor {

	private final ActorRef actor_clear = getContext().actorOf(Props.create(ClearActor.class), "clear-actor");

	@Override
	public void onReceive(Object message) throws Throwable {
		Logs.info("upgrade SenderActor onReceive: " + message);

		if (message instanceof Intent) {
			Intent aIntent = (Intent) message;
			switch (aIntent.getAction()) {
			case CommandUpgrade.PLAYER_SHUTDOWN:
				// got close player result; backup and replace

				break;
			case CommandUpgrade.PLAYER_RESTARE:
				// got restart player result; clear
				Iterator<Map<String, Object>> aIterator = aIntent.getValue().iterator();
				while (aIterator.hasNext()) {
					System.out.println("map keySet: " + aIterator.next().keySet());
				}
				break;
			case CommandUpgrade.STEP_CLEAR:
				actor_clear.tell(aIntent, getSelf());
				break;
			}
		}

		getSender().tell("I Got it!---", ActorRef.noSender());
	}
}