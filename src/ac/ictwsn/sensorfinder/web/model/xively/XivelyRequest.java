package ac.ictwsn.sensorfinder.web.model.xively;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

public class XivelyRequest {
	
	private static final Logger logger = Logger.getLogger(XivelyRequest.class);
	
	private final String[] options = {
			"page", "per_page",	"content", "q", "tag", 
			"user", "units", "status", "order", "show_user", 
			"lat", "lon", "distance", "distance_unit",
			"start", "end", "duration", "interval", "limit", "alive"};
	
	private String URL = "https://api.xively.com/v2/feeds/";
	private final String X_API_KEY = "qFLtQIpZ5cBQqFP4AscSB4B7MUyLPVNpx4XdI9VIOZ5kp3Hg";
	private final String charset = "UTF-8";
	
	private HashSet<String> keySet;
	private HashMap<String, String> requestParams;
	private String feedId;
	
	public XivelyRequest(){
		requestParams = new HashMap<String, String>();
		keySet = new HashSet<String>(Arrays.asList(options));
		setFeedId(null);
	}
	
	public void clear(){
		requestParams.clear();
		keySet = new HashSet<String>(Arrays.asList(options));
		setFeedId(null);
	}
	
	public XivelyRequest addUrl(String key){
		this.URL += key;
		return this;
	}
	
	public XivelyRequest config(String key, String value){
		if(keySet.contains(key))
			requestParams.put(key, value);
		else
			logger.error("Request paramter error!");
		return this;
	}
	
	public HttpURLConnection buildConnection() 
			throws IOException{
		String query = "";
		for (Map.Entry<String, String> entry : requestParams.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    if(!query.equals(""))
		    	query += "&";
			query += (key + "=" + URLEncoder.encode(value, charset));
		}
		String tUrl = URL + (feedId==null?"":feedId + ".json") + "?" + query;
		logger.info("request fired: " + tUrl);
		
		URL url = new URL(tUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("X-ApiKey", X_API_KEY);
		return con;
	}
	
	public XivelyRequest setTargetFeed(String feedId) {
		this.setFeedId(feedId);
		return this;
	}
	public String getURL() {
		return URL;
	}
	public String getX_API_KEY() {
		return X_API_KEY;
	}
	public String getDeviceId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
}
