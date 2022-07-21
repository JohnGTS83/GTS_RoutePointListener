package com.s5.dto;

public class ParentDTO {
	private long tokenid;
	
	private String tokenKey;
	private int parentId;
	private String platform;
	private int stopId;
	 
	public String getTokenKey() {
		return tokenKey;
	}
	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public int getStopId() {
		return stopId;
	}
	public void setStopId(int stopId) {
		this.stopId = stopId;
	}
	
	
	@Override
	public String toString() {
		return "ParentDTO [tokenKey=" + tokenKey + ", parentId=" + parentId + ", platform=" + platform + ", stopId=" + stopId + "]";
	}
	/**
	 * @return the tokenid
	 */
	public long getTokenid() {
		return tokenid;
	}
	/**
	 * @param tokenid the tokenid to set
	 */
	public void setTokenid(long tokenid) {
		this.tokenid = tokenid;
	}
}