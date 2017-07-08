package com.winonetech.jhpplugins.akka;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Intent implements Serializable {

	private String action;

	private List<Map<String, Object>> value;

	public Intent() {
		this("");
	}

	public Intent(String action) {
		super();
		this.action = action;
		value = new ArrayList<>();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<Map<String, Object>> getValue() {
		return value;
	}

	public void addValue(Map<String, Object> aMap) {
		value.add(aMap);
	}

	@Override
	public String toString() {
		return "action=" + action + ", value=" + value;
	}
}