package ac.ictwsn.sensorfinder.web.model.xively;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {
	
	@JsonProperty("value")
	String value;
	@JsonProperty("current_value")
	String currentValue;
	@JsonProperty("max_value")
	String valueMax;
	@JsonProperty("min_value")
	String valueMin;
	@JsonProperty("at")
	String timeAt;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getTimeAt() {
		return timeAt;
	}
	public void setTimeAt(String timeAt) {
		this.timeAt = timeAt;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getValueMax() {
		return valueMax;
	}
	public void setValueMax(String valueMax) {
		this.valueMax = valueMax;
	}
	public String getValueMin() {
		return valueMin;
	}
	public void setValueMin(String valueMin) {
		this.valueMin = valueMin;
	}
}
