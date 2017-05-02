package ac.ictwsn.sensorfinder.web.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AjaxResponse {
	
	@JsonProperty("code")
	String status;
	@JsonProperty("msg")
	String message;
	@JsonProperty("content")
	HashMap<String, Object> content;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public HashMap<String, Object> getContent() {
		return content;
	}
	public void setContent(HashMap<String, Object> content) {
		this.content = content;
	}
}
