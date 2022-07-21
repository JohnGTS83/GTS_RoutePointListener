package com.s5.dto;

public class RouteCustomAlartDTO {

	private int userid;
	private String alerttype;
	private int timespan;
	private String email;

	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getAlerttype() {
		return alerttype;
	}
	public void setAlerttype(String alerttype) {
		this.alerttype = alerttype;
	}
	public int getTimespan() {
		return timespan;
	}
	public void setTimespan(int timespan) {
		this.timespan = timespan;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}