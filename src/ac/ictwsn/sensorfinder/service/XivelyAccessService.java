package ac.ictwsn.sensorfinder.service;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.ictwsn.sensorfinder.entities.Datapoint;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.repositories.SensorStatRepository;
import ac.ictwsn.sensorfinder.task.FeedUpdateTask;
import ac.ictwsn.sensorfinder.task.SensorUpdateTask;
import ac.ictwsn.sensorfinder.utils.XivelyUtils;
import ac.ictwsn.sensorfinder.web.model.xively.DataPointVO;
import ac.ictwsn.sensorfinder.web.model.xively.DatastreamVO;
import ac.ictwsn.sensorfinder.web.model.xively.FeedVO;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyFeedResponse;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyRequest;

/**
 * 
 * @author limeng
 *
 */
@Service
@Transactional
public class XivelyAccessService {

	private static final Logger logger = Logger.getLogger(XivelyAccessService.class);
	
	@Autowired
	private FeedRepository feedRepo;
	@Autowired
	private SensorRepository datastreamRepo;
	@Autowired
	private SensorStatRepository datastreamstatRepo;
	
	//FIXME if a new thread added before the old one is done
	// then the old one can never be stopped by interrupt function
	// Maybe a ThreadPoolExecutor can solve
	private FeedUpdateTask feedTask;
	private SensorUpdateTask datastreamTask;
	
	/**
	 * Count devices registered on Xively
	 * @return
	 */
	public int countFeeds(boolean alive){
		XivelyRequest request = new XivelyRequest();
		request.config("page", "1").config("per_page", "2").config("content", "summary");
		if(alive)
			request.config("status", "live");
		
		XivelyFeedResponse response = null;
		try {
			response = (XivelyFeedResponse) 
					XivelyUtils.sendRequestToXively(request, XivelyFeedResponse.class);
		} catch (IOException e) {
			logger.error("Broken IO detected, retry.");
			e.printStackTrace();
			return -1;
		}
		int total = response.getTotalResults(); 
		logger.debug("Total Result = " + total);
		return total;
	}
	
	/**
	 * Asynchronised method for batch update feeds
	 * Download all the summary of Metadata from Xively
	 * 
	 * Using the following method to stop this thread: http://stackoverflow.com/a/10635688/883290
	 */
	public void asyncUpdateAllFeeds(String start){
		
		logger.info("A new thread will be created to update feeds");
		
		if(start== null || start.equals(""))
			start = "0";
		Long startAt = Long.parseLong(start);
		Integer total = countFeeds(false);
		
		feedTask = new FeedUpdateTask(startAt, total, datastreamRepo, feedRepo); 
		(new Thread(feedTask)).start();
	}
	
	/**
	 * For junit test 
	 */
	public void syncUpdateAllFeeds(){
		logger.info("A new thread will be created to update feeds");
		Integer total = countFeeds(false);
		feedTask = new FeedUpdateTask(0L, total, datastreamRepo, feedRepo); 
		feedTask.run();
	}
	
	/**
	 * Progress bar of feeds update method
	 * @return
	 */
	public Integer getFeedProgress(){
		Integer progress = feedTask.getProgress();
		logger.info("Get feed progress = " + progress);
		return progress;
	}
	
	//TODO add 'CRAWL DONE' message in the front-end respectively 
	
	/**
	 * @return Id where stops
	 */
	public String abortUpdateFeeds() {
		logger.info("Trying to interrupt updating process...");
		Long lastId = feedTask.shutdown();
		return "" + lastId;
	}
	
	/**
	 * Batch update datastream for each feed
	 * The following steps are taken:
	 * 1. Fetch all listed feeds from the repository
	 * 2. For each feeds, run the following query to Xively API: 
	 * https://api.xively.com/v2/feeds/{feed_id}.json?duration=1days&interval=900
	 */
	public void asyncUpdateAllDatastreams(final String startAt){
		Long startId = Long.parseLong(startAt);
		datastreamTask = new SensorUpdateTask(startId, feedRepo, datastreamstatRepo);
		(new Thread(datastreamTask)).start();
	}
	
	/**
	 * Progress bar of datastreams update method
	 * @return
	 */
	public Integer getDatastreamsProgress(){
		return datastreamTask.getProgress();
	}
	
	/**
	 * @return Id where stops
	 */
	public String abortUpdateStreams() {
		Long end = datastreamTask.shutdown();
		return "" + end;
	}
	
	/**
	 * GET datastream from Xively, and save to datastream_stat_t
	 * interval are minutes, hours, day, weekday, month, year 
	 * 
	 * @param feedId
	 * @param datastreamId
	 * @param duration
	 * @param interval
	 */
	public void updateDatastreams(List<String> feedslist) {
		for(String feedIdStr : feedslist){
			
			Long feedId = Long.parseLong(feedIdStr);
			
			String durations[] = {"2days", "1week", "1year"};
			String intervals[] = {"900", "3600", "86400"};
			
//			String durations[] = {"1minute","1hour", "1day", "1week", "1month", "1year"};
//			String intervals[] = {"0", "30", "900", "3600", "10800", "86400"};
			
			for(int i=0; i<durations.length; i++){
				XivelyRequest request = new XivelyRequest();
				request.addUrl("" + feedId + ".json?")
						.config("duration", durations[i])
						.config("interval", intervals[i]);
				
				FeedVO response = null;
				try {
					response = (FeedVO)XivelyUtils.sendRequestToXively(request, FeedVO.class);
				} catch (IOException e) {
					logger.error("Broken IO detected, retry.");
					return ;
				}
				for(DatastreamVO ds : response.getDataStreams()){
					for(DataPointVO point : ds.getDatapoints()){
						Datapoint dss = new Datapoint();
						dss.setDatastreamId(ds.getId());
						dss.setDuration(durations[i]);
						dss.setFeedId(feedId);
						dss.setTimestamp(XivelyUtils.parseTime(point.getAt()));
						dss.setVal(point.getValue());
						datastreamstatRepo.save(dss);
					}
				}
			}
		}
	}
	
}
