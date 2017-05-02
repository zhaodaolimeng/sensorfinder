package ac.ictwsn.sensorfinder.task;

import java.io.IOException;

import org.apache.log4j.Logger;

import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.utils.XivelyUtils;
import ac.ictwsn.sensorfinder.web.model.xively.DataUnitVO;
import ac.ictwsn.sensorfinder.web.model.xively.DatastreamVO;
import ac.ictwsn.sensorfinder.web.model.xively.FeedVO;
import ac.ictwsn.sensorfinder.web.model.xively.LocationVO;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyFeedResponse;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyRequest;


public class FeedUpdateTask implements Runnable{
	
	private static final Logger logger = Logger.getLogger(FeedUpdateTask.class);
	volatile boolean shutdown = false;
	
	private Long startAt; 
	private Integer total;
	private Integer progress;
	private Long currentFeed;
	private SensorRepository datastreamRepo;
	private FeedRepository feedRepo;
	
	public FeedUpdateTask(Long startAt, Integer total, SensorRepository datastreamRepo, FeedRepository feedRepo){
		this.startAt = startAt;
		this.total = total;
		this.progress = 0;
		this.datastreamRepo = datastreamRepo;
		this.feedRepo = feedRepo;
	}
	
	public Long shutdown(){
		this.shutdown = true;
		logger.info("Thread is going to close ... ");
		return this.currentFeed;
	}
	
	public Integer getProgress(){
		return this.progress;
	}
	
	@Override
	public void run() {
		logger.info("New thread created for crawling feeds...");
		int start = 0;
		int end = total;
		int currentCnt = 0;
		int perPage = 20;
		logger.info("Count of all the feeds = " + total);
		
		XivelyRequest request = new XivelyRequest();
		if(end<start||perPage<1)
			return ;
		for (int page = 1 + start/perPage; page <= end/perPage; page++){
			
			if(shutdown) break;
			
			request.config("page", "" + page).config("per_page", "" + perPage);
			XivelyFeedResponse response = null;
			try{
				response = (XivelyFeedResponse) 
						XivelyUtils.sendRequestToXively(request, XivelyFeedResponse.class);
			} catch (IOException e){
				logger.info("Broken IO detected, roll back and retry.");
				page --;
				continue;
			}
			
			logger.info("Saving result for page = " + page);
			logger.info("Total result = " + response.getResults().size());
			logger.info("Progress = " + progress);
			
			for (FeedVO vo : response.getResults()) {
				Feed feed = new Feed();
				saveFeedFromVO(vo, feed);
				if(feed.getId() < startAt) continue;
				currentFeed = feed.getId();
				currentCnt ++;
				progress = 100 * currentCnt / end;
			} // end of page 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.progress = 100;
		logger.info("Feed Update Done!");
	}
	
	private void saveFeedFromVO(FeedVO vo, Feed feed){
		feed.setId(vo.getId());
		feed.setTitle(vo.getTitle());
		feed.setPrivacy(vo.getPrivacy());
		feed.setTags(XivelyUtils.concatStringWithComma(vo.getTags()));
		feed.setDescription(vo.getDescription());
		feed.setFeedUrl(vo.getFeed());
		feed.setStatus(vo.getStatus());
		feed.setUpdated(XivelyUtils.parseTime(vo.getUpdateTime()));
		feed.setCreated(XivelyUtils.parseTime(vo.getCreateTime()));
		feed.setCreator(vo.getCreator());
		feed.setVersion(vo.getVersion());
		feed.setWebsite(vo.getWebsite());
		feed.setIcon(vo.getIcon());
		
		LocationVO loc = vo.getLocation();
		if(loc != null){
			feed.setDisposition(loc.getDisposition());
			feed.setDeviceName(loc.getName());
			feed.setEle(loc.getEle());
			feed.setLat(loc.getLat());
			feed.setLng(loc.getLon());
			feed.setExposure(loc.getExposure());
			feed.setDomain(loc.getDomain());
		}
		feedRepo.save(feed);
		if(vo.getDataStreams() == null) return;
		try {
			for(DatastreamVO dvo : vo.getDataStreams()){
				Sensor ds = new Sensor();
				ds.setStreamId(dvo.getId());
				ds.setFeed(feed);
				ds.setUpdated(XivelyUtils.parseTime(dvo.getAt()));
				ds.setTags(XivelyUtils.concatStringWithComma(dvo.getTags()));
				DataUnitVO duvo = new DataUnitVO();
				if(dvo.getUnit()!=null){
					ds.setUnitSymbol(duvo.getSymbol());
					ds.setUnits(duvo.getLabel());
					ds.setUnitType(duvo.getType());
				}
				ds.setMinValue(dvo.getMin_value());
				ds.setMaxValue(dvo.getMax_value());
				ds.setCurrentValue(dvo.getCurrent_value());
				datastreamRepo.save(ds);
			} // extract datastream from feed
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
