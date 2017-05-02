package ac.ictwsn.sensorfinder.task;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import ac.ictwsn.sensorfinder.entities.Datapoint;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorStatRepository;
import ac.ictwsn.sensorfinder.utils.XivelyUtils;
import ac.ictwsn.sensorfinder.web.model.xively.DataPointVO;
import ac.ictwsn.sensorfinder.web.model.xively.DatastreamVO;
import ac.ictwsn.sensorfinder.web.model.xively.FeedVO;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyRequest;

public class SensorUpdateTask implements Runnable{
	
	private static final Logger logger = Logger.getLogger(SensorUpdateTask.class);
	private final long PER_FETCH_DELAY = 1500L;
	private final long BREAKDOWN_DELAY = 5000L;
	
	volatile boolean shutdown = false;
	private Long currentDatastream;
	private Long startAt;
	private Integer progress;
	private FeedRepository feedRepo;
	private SensorStatRepository sensorStatRepo;
	
	public SensorUpdateTask(Long startAt, FeedRepository feedRepo, SensorStatRepository sensorStatRepo){
		this.currentDatastream = 0L;
		this.startAt = startAt;
		this.progress = 0;
		this.feedRepo = feedRepo;
		this.sensorStatRepo = sensorStatRepo;
	}
	
	public Long shutdown(){
		this.shutdown = true;
		return this.currentDatastream;
	}
	
	public Integer getProgress(){
		return this.progress;
	}
	
	@Override
	public void run() {
		
//		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
//		DateFormat iosdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
//		Date yearQueryStartTime = fmt.parse("2015-08-01");
//		Date start = iosdf.parse("2016-08-01T00:00Z");
		
//		String durations[] = {"1minute","1hour", "1day", "1week", "1month", "1year"};
//		String intervals[] = {"0", "30", "900", "3600", "10800", "86400"};
		
//		String durations[] = {"2days", "1week", "1year"};
//		String intervals[] = {"900", "3600", "86400"};
		
		String durations[] = {"2days"};
		String intervals[] = {"900"};
		
		//FIXME 可能会引起数据的截断
		logger.error("THIS IS A BUGGY METHOD!!!!");
		logger.info("New thread created for crawling datastreams = " + startAt);
		
		List<Feed> feedList = (List<Feed>) feedRepo.findAlive();
		Integer total = 1 + feedList.size(); //FIXME hot null hack
		Integer counter = 0;
		logger.info("The count of alive feeds are: " + total);
		// 599262710
		
		for(Feed feed : feedList){
			
			logger.info("Start collect feedId = " + feed.getId());
			counter ++;
			Long feedId = feed.getId();
			for(int i=0; i<durations.length; i++){
				XivelyRequest request = new XivelyRequest();
				request.addUrl("" + feedId + ".json")
					.config("duration", durations[i])
					.config("interval", intervals[i]);
				
				// get datastream for each feed
				FeedVO response = null;
				try {
					response = (FeedVO)XivelyUtils.sendRequestToXively(request, FeedVO.class);
					Thread.sleep(PER_FETCH_DELAY);
				} catch (IOException e) {
					logger.error("Broken IO detected, retry.");
					try {
						Thread.sleep(BREAKDOWN_DELAY);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					continue;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				for(DatastreamVO ds : response.getDataStreams()){
					//set datastream name
					if(ds.getDatapoints() == null){
						logger.info("Null datapoints for " + ds.getId());
						continue;
					}
					for(DataPointVO point : ds.getDatapoints()){
						Datapoint dss = new Datapoint();
						dss.setDatastreamId(ds.getId());
						dss.setDuration(durations[i]);
						dss.setFeedId(feedId);
						dss.setTimestamp(XivelyUtils.parseTime(point.getAt()));
						dss.setVal(point.getValue());
						sensorStatRepo.save(dss);
					}
				}
			}
			currentDatastream = feedId;
			progress = 100 * counter / total;  	
			if(shutdown) break;
		}
		
		logger.info("Datastream Update Done!");
	}
}
