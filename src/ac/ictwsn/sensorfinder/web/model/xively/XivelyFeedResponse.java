package ac.ictwsn.sensorfinder.web.model.xively;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class XivelyFeedResponse extends XivelyResponse{
	
	@JsonProperty("totalResults")
	private Integer totalResults;
	@JsonProperty("itemsPerPage")
	private Integer itemsPerPage;
	@JsonProperty("startIndex")
	private Integer startIndex;
	@JsonProperty("results")
	private List<FeedVO> results;
	
	public Integer getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
	public Integer getItemsPerPage() {
		return itemsPerPage;
	}
	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}
	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public List<FeedVO> getResults() {
		return results;
	}
	public void setResults(List<FeedVO> results) {
		this.results = results;
	}
}
