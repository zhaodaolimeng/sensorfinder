package ac.ictwsn.sensorfinder.web.model.xively;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedVO extends XivelyFeedResponse {
	
	private Long id;
	private String title;
	@JsonProperty("private")
	private Boolean privacy;
	private String[] tags;
	private String description;
	private String feed;
	private String email;
	
	private String status;
	@JsonProperty("updated")
	private String updateTime;
	@JsonProperty("created")
	private String createTime;
	private String creator;
	private String version;
	@JsonProperty("datastreams")
	private List<DatastreamVO> dataStreams;
	
	@JsonProperty("location")
	private LocationVO location;
	@JsonProperty("website")
	private String website;
	@JsonProperty("icon")
	private String icon;
	
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String getFeed() {
		return feed;
	}
	public void setFeed(String feed) {
		this.feed = feed;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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
	public List<DatastreamVO> getDataStreams() {
		return dataStreams;
	}
	public void setDataStreams(List<DatastreamVO> dataStreams) {
		this.dataStreams = dataStreams;
	}
	public Boolean getPrivacy() {
		return privacy;
	}
	public void setPrivacy(Boolean privacy) {
		this.privacy = privacy;
	}
	public LocationVO getLocation() {
		return location;
	}
	public void setLocation(LocationVO location) {
		this.location = location;
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
}
