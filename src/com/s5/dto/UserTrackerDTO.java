package com.s5.dto;

public class UserTrackerDTO {

	private int tracker_type_id;
	private int userID;
	private int timeZoneOffSet;
	private boolean isDayLight;
	
	public UserTrackerDTO(int tracker_type_id) {
		this.tracker_type_id = tracker_type_id;
	}

	public int getTracker_type_id() {
		return tracker_type_id;
	}

	public void setTracker_type_id(int tracker_type_id) {
		this.tracker_type_id = tracker_type_id;
	}
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getTimeZoneOffSet() {
		return timeZoneOffSet;
	}

	public void setTimeZoneOffSet(int timeZoneOffSet) {
		this.timeZoneOffSet = timeZoneOffSet;
	}

	public boolean isDayLight() {
		return isDayLight;
	}

	public void setDayLight(boolean isDayLight) {
		this.isDayLight = isDayLight;
	}

	@Override
	public String toString() {
		return "UserTrackerDTO [tracker_type_id=" + tracker_type_id + ", userID=" + userID + ", timeZoneOffSet="
				+ timeZoneOffSet + ", isDayLight=" + isDayLight + "]";
	}
}