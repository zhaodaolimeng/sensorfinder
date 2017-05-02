package ac.ictwsn.sensorfinder.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "datapoint_t")
@XmlRootElement
public class Datapoint {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	Long id;
	@Column(name = "feedid")
	Long feedId;
	@Column(name = "datastreamid")
	String datastreamId;
	@Column(name = "time_at")
	Date timestamp;
	@Column(name = "val")
	String val;
	@Column(name = "val_max")
	String valMax;
	@Column(name = "val_min")
	String valMin;
	@Column(name = "duration")
	String duration;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFeedId() {
		return feedId;
	}
	public void setFeedId(Long feedId) {
		this.feedId = feedId;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getDatastreamId() {
		return datastreamId;
	}
	public void setDatastreamId(String datastreamId) {
		this.datastreamId = datastreamId;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getValMax() {
		return valMax;
	}
	public void setValMax(String valMax) {
		this.valMax = valMax;
	}
	public String getValMin() {
		return valMin;
	}
	public void setValMin(String valMin) {
		this.valMin = valMin;
	}
}
