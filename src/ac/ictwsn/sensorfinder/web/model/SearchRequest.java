package ac.ictwsn.sensorfinder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequest {
	
	@JsonProperty("feedid")
	Long feedid;
	@JsonProperty("streamid")
	String streamid;
	@JsonProperty("q")
	String keywords;
	String graphType;
	Integer sensorNum;
	
	public Long getFeedid() {
		return feedid;
	}
	public void setFeedid(Long feedid) {
		this.feedid = feedid;
	}
	public String getStreamid() {
		return streamid;
	}
	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getGraphType() {
		return graphType;
	}
	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}
	public Integer getSensorNum() {
		return sensorNum;
	}
	public void setSensorNum(Integer sensorNum) {
		this.sensorNum = sensorNum;
	}
}
