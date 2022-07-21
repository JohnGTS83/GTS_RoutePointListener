package com.s5.dto;

import java.util.ArrayList;

public class PushResposeDTO {

	private String multicast_id;
	private int success;
	private int failure;
	private int canonical_ids;
	private ArrayList<PushMessageDTO> results;
	/**
	 * @return the multicast_id
	 */
	public String getMulticast_id() {
		return multicast_id;
	}
	/**
	 * @param multicast_id the multicast_id to set
	 */
	public void setMulticast_id(String multicast_id) {
		this.multicast_id = multicast_id;
	}
	/**
	 * @return the success
	 */
	public int getSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(int success) {
		this.success = success;
	}
	/**
	 * @return the failure
	 */
	public int getFailure() {
		return failure;
	}
	/**
	 * @param failure the failure to set
	 */
	public void setFailure(int failure) {
		this.failure = failure;
	}
	/**
	 * @return the canonical_ids
	 */
	public int getCanonical_ids() {
		return canonical_ids;
	}
	/**
	 * @param canonical_ids the canonical_ids to set
	 */
	public void setCanonical_ids(int canonical_ids) {
		this.canonical_ids = canonical_ids;
	}
	/**
	 * @return the results
	 */
	public ArrayList<PushMessageDTO> getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(ArrayList<PushMessageDTO> results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "PushResposeDTO [multicast_id=" + multicast_id + ", success=" + success + ", failure=" + failure
				+ ", canonical_ids=" + canonical_ids + ", results=" + results + "]";
	}
	
	
	
}
