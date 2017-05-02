package ac.ictwsn.sensorfinder.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "datastream_t")
@XmlRootElement
public class Sensor {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "streamid")
	private String streamId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "feedid", nullable = false, referencedColumnName = "id")
	private Feed feed;
	
	// Add another way to access 
	@Column(name="feedid", updatable=false, insertable=false)
	private Long feedid;
	
	@Column(name = "updated")
	private Date updated;
	@Column(name = "tags")
	private String tags;
	
	@Column(name = "units")
	private String units;
	@Column(name = "unit_type")
	private String unitType;
	@Column(name = "unit_symbol")
	private String unitSymbol;
	
	@Column(name = "min_value")
	private String minValue;
	@Column(name = "max_value")
	private String maxValue;
	@Column(name = "current_value")
	private String currentValue;
	@Column(name = "label")
	private String label;
	
	public Long getFeedid() {
		return feedid;
	}
	public void setFeedid(Long feedid) {
		this.feedid = feedid;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Feed getFeed() {
		return feed;
	}
	public void setFeed(Feed feed) {
		this.feed = feed;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getUnitSymbol() {
		return unitSymbol;
	}
	public void setUnitSymbol(String unitSymbol) {
		this.unitSymbol = unitSymbol;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getStreamId() {
		return streamId;
	}
	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
