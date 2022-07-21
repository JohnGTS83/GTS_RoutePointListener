package com.s5.dto;

public class TrackerDTO {

	private int tracker_type_id;

	public TrackerDTO(int tracker_type_id) {
		this.tracker_type_id = tracker_type_id;
	}

	public int getTracker_type_id() {
		return tracker_type_id;
	}

	public void setTracker_type_id(int tracker_type_id) {
		this.tracker_type_id = tracker_type_id;
	}

	@Override
	public String toString() {
		return "TrackerDTO [tracker_type_id=" + tracker_type_id + "]";
	}
}