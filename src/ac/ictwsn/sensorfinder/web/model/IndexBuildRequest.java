package ac.ictwsn.sensorfinder.web.model;

import java.util.ArrayList;

public class IndexBuildRequest {
	
	ArrayList<String> options; // 用于索引建立
	String query; // 全文查询

	public ArrayList<String> getOptions() {
		return options;
	}
	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
}
