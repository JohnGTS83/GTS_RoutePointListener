package com.s5.dto;

public class AlertDTO {
	
	int id;
	String stopType;
	int stopNumber;
	String routeName;
	String runName;
	String unitName;
	String stopDesc;
	String stopId;
	String fromProvider;
	
	
	long lateMinuts;
	String starTime;
	String endTime;
	int routeFKId;
	int d_driver_id;
	
	public String getStopDesc() {
		return stopDesc;
	}
	public void setStopDesc(String stopDesc) {
		this.stopDesc = stopDesc;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStopType() {
		return stopType;
	}
	public void setStopType(String stopType) {
		this.stopType = stopType;
	}
	public int getStopNumber() {
		return stopNumber;
	}
	public void setStopNumber(int stopNumber) {
		this.stopNumber = stopNumber;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public String getRunName() {
		return runName;
	}
	public void setRunName(String runName) {
		this.runName = runName;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public long getLateMinuts() {
		return lateMinuts;
	}
	public void setLateMinuts(long lateMinuts) {
		this.lateMinuts = lateMinuts;
	}
	public String getStopId() {
		return stopId;
	}
	public void setStopId(String stopId) {
		this.stopId = stopId;
	}
	public String getFromProvider() {
		return fromProvider;
	}
	public void setFromProvider(String fromProvider) {
		this.fromProvider = fromProvider;
	}
	public String getStarTime() {
		return starTime;
	}
	public void setStarTime(String starTime) {
		this.starTime = starTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getRouteFKId() {
		return routeFKId;
	}
	public void setRouteFKId(int routeFKId) {
		this.routeFKId = routeFKId;
	}
	public int getD_driver_id() {
		return d_driver_id;
	}
	public void setD_driver_id(int d_driver_id) {
		this.d_driver_id = d_driver_id;
	}
}