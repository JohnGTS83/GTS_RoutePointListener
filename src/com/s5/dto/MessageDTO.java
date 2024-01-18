package com.s5.dto;

import java.util.Date;

public class MessageDTO {
	
	private String sn_imei_id;
	private double latitude;
	private double longitude;
	private String l_datetime;
	private int alarm_id;
	private int studentCountId;
	
	private Date lDate;

	public String getSn_imei_id() {
		return sn_imei_id;
	}
	public void setSn_imei_id(String sn_imei_id) {
		this.sn_imei_id = sn_imei_id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getL_datetime() {
		return l_datetime;
	}
	public void setL_datetime(String l_datetime) {
		this.l_datetime = l_datetime;
	}
	public int getAlarm_id() {
		return alarm_id;
	}
	public void setAlarm_id(int alarm_id) {
		this.alarm_id = alarm_id;
	}
	public Date getlDate() {
		return lDate;
	}
	public void setlDate(Date lDate) {
		this.lDate = lDate;
	}
	
	public int getStudentCountId() {
		return studentCountId;
	}
	public void setStudentCountId(int studentCountId) {
		this.studentCountId = studentCountId;
	}
	@Override
	public String toString() {
		return sn_imei_id + ": " + l_datetime;
	}
	
	
}