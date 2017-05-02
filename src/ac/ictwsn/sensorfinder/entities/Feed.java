package ac.ictwsn.sensorfinder.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "feed_t")
@XmlRootElement
public class Feed {
	
	@Id
	@Column(name = "id", unique = true)
	@JsonView(DataTablesOutput.View.class)
	private Long id;
	
	@Column(name = "title")
	@JsonView(DataTablesOutput.View.class)
	private String title;
	
	@Column(name = "private")
	@JsonView(DataTablesOutput.View.class)
	private Boolean privacy;
	
	@Column(name = "isalive")
	private Boolean isalive;	
	
	@Column(name = "tags")
	@JsonView(DataTablesOutput.View.class)
	private String tags;
	
	@Column(name = "description", columnDefinition="TEXT")
	@JsonView(DataTablesOutput.View.class)
	private String description;
	
	@Column(name = "feedUrl")
	@JsonView(DataTablesOutput.View.class)
	private String feedUrl;
	
	@Column(name = "status")
	@JsonView(DataTablesOutput.View.class)
	private String status;
	
	@Column(name = "updated")
	@JsonView(DataTablesOutput.View.class)
	private Date updated;
	
	@Column(name = "created")
	@JsonView(DataTablesOutput.View.class)
	private Date created;
	
	@Column(name = "creator")
	@JsonView(DataTablesOutput.View.class)
	private String creator;
	
	@Column(name = "version")
	@JsonView(DataTablesOutput.View.class)
	private String version;
	
	@Column(name = "website")
	@JsonView(DataTablesOutput.View.class)
	private String website;
	
	@Column(name = "icon")
	@JsonView(DataTablesOutput.View.class)
	private String icon;
	
	@Column(name = "user_login")
	@JsonView(DataTablesOutput.View.class)
	private String userLogin;
	
	// Location
	@Column(name = "disposition")
	@JsonView(DataTablesOutput.View.class)
	private String disposition;
	
	@Column(name = "device_name")
	@JsonView(DataTablesOutput.View.class)
	private String deviceName;
	
	@Column(name = "ele")
	@JsonView(DataTablesOutput.View.class)
	private String ele;
	
	@Column(name = "lat")
	@JsonView(DataTablesOutput.View.class)
	private String lat;
	
	@Column(name = "lng")
	@JsonView(DataTablesOutput.View.class)
	private String lng;
	
	@Column(name = "exposure")
	@JsonView(DataTablesOutput.View.class)
	private String exposure;
	
	@Column(name = "domain")
	@JsonView(DataTablesOutput.View.class)
	private String domain;
	
	@Column(name = "iana")
	@JsonView(DataTablesOutput.View.class)
	private String iana;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feed", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Sensor> datastreams = new HashSet<Sensor>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getPrivacy() {
		return privacy;
	}
	public void setPrivacy(Boolean privacy) {
		this.privacy = privacy;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFeedUrl() {
		return feedUrl;
	}
	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getEle() {
		return ele;
	}
	public void setEle(String ele) {
		this.ele = ele;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getExposure() {
		return exposure;
	}
	public void setExposure(String exposure) {
		this.exposure = exposure;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Set<Sensor> getDatastreams() {
		return datastreams;
	}
	public void setDatastreams(Set<Sensor> datastreams) {
		this.datastreams = datastreams;
	}
	public Boolean getIsalive() {
		return isalive;
	}
	public void setIsalive(Boolean isalive) {
		this.isalive = isalive;
	}
	public String getIana() {
		return iana;
	}
	public void setIana(String iana) {
		this.iana = iana;
	}
}
