package ac.ictwsn.sensorfinder.dto;

import java.util.List;

public class ResultDTO {
	
	private Integer totalHits;
	private Double timeUsed; 
	private List<SensorDocument> itemlist;

	public Integer getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(Integer totalHits) {
		this.totalHits = totalHits;
	}
	public Double getTimeUsed() {
		return timeUsed;
	}
	public void setTimeUsed(Double timeUsed) {
		this.timeUsed = timeUsed;
	}
	public List<SensorDocument> getItemlist() {
		return itemlist;
	}
	public void setItemlist(List<SensorDocument> itemlist) {
		this.itemlist = itemlist;
	}

}
