package ac.ictwsn.sensorfinder.dto;

import java.util.HashMap;

public class WordTopicDTO {
	
	int docId;
	HashMap<String, Integer> topicAllocation;
	
	public WordTopicDTO(Integer docId){
		this.topicAllocation = new HashMap<String, Integer>();
		this.docId = docId;
	}
	
	public void addAllocationForWord(String k, Integer v){
		topicAllocation.put(k, v);
	}
	
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public HashMap<String, Integer> getTopicAllocation() {
		return topicAllocation;
	}
	public void setTopicAllocation(HashMap<String, Integer> topicAllocation) {
		this.topicAllocation = topicAllocation;
	}
}
