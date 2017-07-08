package com.player.upgrade.detect;

/**
 * detection result
 */
public final class DetectionResult {

	/**
	 * true-->pass
	 */
	private boolean isPass;

	/**
	 * message
	 */
	private String reason;

	public DetectionResult() {
		this(false, "reason");
	}

	public DetectionResult(boolean isPass, String reason) {
		this.isPass = isPass;
		this.reason = reason;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "isPass=" + isPass + ", reason=" + reason;
	}

}