package ac.ictwsn.sensorfinder.web.model.xively;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatastreamVO extends XivelyFeedResponse{
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("current_value")
	private String current_value;
	
	@JsonProperty("at")
	private String at;
	
	@JsonProperty("min_value")
	private String min_value;
	
	@JsonProperty("max_value")
	private String max_value;
	
	private String[] tags;
	private DataUnitVO unit;
	
	@JsonProperty("datapoints")
	private List<DataPointVO> datapoints;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCurrent_value() {
		return current_value;
	}
	public void setCurrent_value(String current_value) {
		this.current_value = current_value;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public String getMin_value() {
		return min_value;
	}
	public void setMin_value(String min_value) {
		this.min_value = min_value;
	}
	public String getMax_value() {
		return max_value;
	}
	public void setMax_value(String max_value) {
		this.max_value = max_value;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public DataUnitVO getUnit() {
		return unit;
	}
	public void setUnit(DataUnitVO unit) {
		this.unit = unit;
	}
	public List<DataPointVO> getDatapoints() {
		return datapoints;
	}
	public void setDatapoints(List<DataPointVO> datapoints) {
		this.datapoints = datapoints;
	}
}
