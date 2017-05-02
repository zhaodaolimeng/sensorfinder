package ac.ictwsn.sensorfinder.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrawlRequest {
	
	@JsonProperty("isalive")
	Boolean alive;
	@JsonProperty("startat")
	String startat;
	@JsonProperty("feedlist")
	List<String> feedlist;
	
	public Boolean isAlive() {
		return alive;
	}
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	public String getStartat() {
		return startat;
	}
	public void setStartat(String startat) {
		this.startat = startat;
	}
	public List<String> getFeedlist() {
		return feedlist;
	}
	public void setFeedlist(List<String> feedlist) {
		this.feedlist = feedlist;
	}
}
