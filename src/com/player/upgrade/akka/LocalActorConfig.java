package com.player.upgrade.akka;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class LocalActorConfig {

	public static Config getConfig(String host, int port) {
		Map<String, Object> aMap = new HashMap<>();
		aMap.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider");
		Set<String> aSet = new HashSet<>();
		aSet.add("akka.remote.netty.tcp");
		aMap.put("akka.remote.enabled-transports", aSet);

		aMap.put("akka.remote.netty.tcp.hostname", host);
		aMap.put("akka.remote.netty.tcp.port", port);
		return ConfigFactory.parseMap(aMap);
	}

}
