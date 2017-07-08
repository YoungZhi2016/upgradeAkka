package com.player.upgrade.akka;

public interface CommandUpgrade {

	/**
	 * upgrade result instruction
	 */
	String UPGRADE_RESULT = "AURT";

	/**
	 * upgrade instruction
	 */
	String UPGRADE = "UPGR";

	/**
	 * RECEIVE instruction
	 */
	String RECEIVE = "ARCV";

	/**
	 * player shutdown instruction
	 */
	String PLAYER_SHUTDOWN = "AOFF";

	/**
	 * player restart instruction
	 */
	String PLAYER_RESTARE = "ARON";

	/*-----------------------------Internal use-----------------------------*/

	String STEP_ENVIRONMENT = "STEP_EVRMT";// Environment detect

	String STEP_UNZIP = "STEP_UNZIP";// unZip pack detect

	String STEP_REPLACE = "STEP_RPLCE";// replace file

	String STEP_BACKUP = "STEP_BACKP";// backup file

	String STEP_CLEAR = "STEP_CLEAR";// clear file

}