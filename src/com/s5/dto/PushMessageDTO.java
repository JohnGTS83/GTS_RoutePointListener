package com.s5.dto;

public class PushMessageDTO {

	private String error;
	private String message_id;
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return the message_id
	 */
	public String getMessage_id() {
		return message_id;
	}
	/**
	 * @param message_id the message_id to set
	 */
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	@Override
	public String toString() {
		return "PushMessageDTO [error=" + error + ", message_id=" + message_id + "]";
	}
	
	
}
