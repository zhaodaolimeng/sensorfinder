package ac.ictwsn.sensorfinder.dto;

/**
 * snapshot 
 * feed id
 * sensor id
 * ranking score
 * highlight message
 * 
 * @author limeng
 *
 */
public class SensorDocument {
	
	Long feedid;
	String sensorid; // streamid in database, could be description
	String feedDescription;
	String feedTags;
	String sensorTags;
	Double score;
	
	String snapshot;
	String createdTime;
	String sensorLabel;
	String feedTitle; // fetched from DB
	String sensorDescription; // fetched from DB
	
	public SensorDocument(){}
	
	public SensorDocument(
			Long feedid, String feedTitle, String feedDescription, String feedTags,  
			String sensorid, String sensorTags){
		
		this.feedid = feedid;
		this.feedTitle = feedTitle;
		this.feedDescription = feedDescription;
		this.feedTags = feedTags;
		
		this.sensorid = sensorid;
		this.sensorTags = sensorTags;
	}
	
	public String getSnapshotWithUpdate(){
		StringBuilder sb = new StringBuilder();
		this.snapshot = sb.append(this.feedTitle).append(' ')
				.append(this.feedTags).append(' ')
				.append(this.feedDescription).append(' ')
				.append(this.sensorid).append(' ')
				.append(this.sensorTags).append(' ').toString();
		return this.snapshot;
	}

	public String getFeedTitle() {
		return feedTitle;
	}
	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}
	public String getFeedDescription() {
		return feedDescription;
	}
	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}
	public String getFeedTags() {
		return feedTags;
	}
	public void setFeedTags(String feedTags) {
		this.feedTags = feedTags;
	}
	public String getSensorTags() {
		return sensorTags;
	}
	public void setSensorTags(String sensorTags) {
		this.sensorTags = sensorTags;
	}
	public Long getFeedid() {
		return feedid;
	}
	public void setFeedid(Long feedid) {
		this.feedid = feedid;
	}
	public String getSensorid() {
		return sensorid;
	}
	public void setSensorid(String sensorid) {
		this.sensorid = sensorid;
	}
	public String getSnapshot() {
		return snapshot;
	}
	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getSensorDescription() {
		return sensorDescription;
	}
	public void setSensorDescription(String sensorDescription) {
		this.sensorDescription = sensorDescription;
	}
	public String getSensorLabel() {
		return sensorLabel;
	}
	public void setSensorLabel(String sensorLabel) {
		this.sensorLabel = sensorLabel;
	}
}
