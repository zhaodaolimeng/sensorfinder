package ac.ictwsn.sensorfinder.web.model;

public class QueryRequest {
	
	String query; // 全文查询
	
	boolean useXively;
	boolean useThingspeak;
	
	boolean assistedWithTopic;

	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public boolean isUseXively() {
		return useXively;
	}
	public void setUseXively(boolean useXively) {
		this.useXively = useXively;
	}
	public boolean isUseThingspeak() {
		return useThingspeak;
	}
	public void setUseThingspeak(boolean useThingspeak) {
		this.useThingspeak = useThingspeak;
	}
	public boolean isAssistedWithTopic() {
		return assistedWithTopic;
	}
	public void setAssistedWithTopic(boolean assistedWithTopic) {
		this.assistedWithTopic = assistedWithTopic;
	}
}
