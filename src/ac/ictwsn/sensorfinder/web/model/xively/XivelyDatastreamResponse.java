package ac.ictwsn.sensorfinder.web.model.xively;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class XivelyDatastreamResponse extends XivelyResponse{
	
	@JsonProperty("id")
	String id;
	@JsonProperty("current_value")
	String currentValue;
	@JsonProperty("at")
	String timeAt;
	@JsonProperty("max_value")
	String maxValue;
	@JsonProperty("min_value")
	String minValue;
	@JsonProperty("tags")
	List<String> tags;
	@JsonProperty("datapoints")
	List<DataPoint> datapoints;
	@JsonProperty("version")
	String version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getTimeAt() {
		return timeAt;
	}
	public void setTimeAt(String timeAt) {
		this.timeAt = timeAt;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<DataPoint> getDatapoints() {
		return datapoints;
	}
	public void setDatapoints(List<DataPoint> datapoints) {
		this.datapoints = datapoints;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
