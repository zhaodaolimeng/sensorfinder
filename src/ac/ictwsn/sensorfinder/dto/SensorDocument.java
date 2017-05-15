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
public class SensorDocument implements Comparable<SensorDocument>{
	
	Long feedid;
	String sensorid; // streamid in database, could be description
	String feedDescription;
	String feedTags;
	String sensorTags;
	Double score; //similarity
	
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
	
	public SensorDocument(Long feedid, String sensorid, Double score){
		this.feedid = feedid;
		this.sensorid = sensorid;
		this.score = score;
	}
	
	public SensorDocument(String feedsensorid, Double score){
		String[] fs = feedsensorid.split(",");
		this.feedid = Long.parseLong(fs[0]);
		this.sensorid = fs[1];
		this.score = score;
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
	
	public int compareTo(SensorDocument sensor){
		return Double.compare(this.score, sensor.score);
	}

	
	public String getFeedSensorStr(){
		return this.feedid + "," + this.sensorid;
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
