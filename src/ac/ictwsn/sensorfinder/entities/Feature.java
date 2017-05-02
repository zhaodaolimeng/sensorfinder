package ac.ictwsn.sensorfinder.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Offline features, generated by 
 * @author li
 *
 */
@Entity
@Table(name = "features_t")
@XmlRootElement
public class Feature {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "feedid")
	Long feedid;
	@Column(name = "streamid")
	String streamid;
	@Column(name = "doc")
	String doc;
	@Column(name = "iana")
	String iana;
	@Column(name = "location_type")
	String locationType;
	@Column(name = "created")
	Date created;
	@Column(name = "is_training_set")	
	Boolean isTrainingSet;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getIana() {
		return iana;
	}
	public void setIana(String iana) {
		this.iana = iana;
	}
	public String getLocationType() {
		return locationType;
	}
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	

}
