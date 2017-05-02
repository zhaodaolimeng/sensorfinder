package ac.ictwsn.sensorfinder.task;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ac.ictwsn.core.util.TimeHelper;
import ac.ictwsn.sensorfinder.entities.Datapoint;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.repositories.SensorStatRepository;
import ac.ictwsn.sensorfinder.service.XivelyAccessService;
import ac.ictwsn.sensorfinder.utils.XivelyUtils;
import ac.ictwsn.sensorfinder.web.model.xively.DataPointVO;
import ac.ictwsn.sensorfinder.web.model.xively.DatastreamVO;
import ac.ictwsn.sensorfinder.web.model.xively.FeedVO;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyRequest;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes={AppConfig.class})
public class CrawlTaskTest {
	
	private static final Logger logger = Logger.getLogger(CrawlTaskTest.class);
	
	@Autowired
	XivelyAccessService xas;
	@Autowired
	FeedRepository feedRepo;
	@Autowired
	SensorRepository dsRepo;
	@Autowired
	SensorStatRepository ssRepo;
	
	/**
	 * 爬取数据：
	 * 由于传输受限，使用切片请求的方法
	 * 一次请求要少于400个，但每个设备可能会有多个数据序列
	 * 
	 * Modified: 2016-12-08 爬取得到将两天的数据，存入到datapoint_t
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	@Test
//	@Rollback(false)
	public void crawlDataForPerDay() throws ParseException, InterruptedException{
		final long PER_FETCH_DELAY = 2000L;
		final long BREAKDOWN_DELAY = 5000L;
		final int LIMIT = 1000;
		
		DateFormat iosdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		Date startTime = iosdf.parse("2016-12-04T00:00Z");
		Date endTime = iosdf.parse("2016-12-06T00:00Z"); // 捕获两天的数据
		
		Long MIN_ID = 749343757L;
		Long MIN_PARTS = 12L;
		
		for(Feed feed : (List<Feed>) feedRepo.findAliveAndStable(startTime)){
			
			if(feed.getId() < MIN_ID)
				continue;
			
			List<Sensor> dslist =  dsRepo.findByFeed(feed);
			int parts = dslist.size() * (2*24*60) / LIMIT + 1;
			int stepLength = (int)(24.0*60/parts); // 切片请求，每次请求一个步长
			Date dateStart = startTime;
			
			logger.info("===========================");
			logger.info("Step length = " + stepLength);
			List<Datapoint> dplist = new ArrayList<Datapoint>();
			
			while(dateStart.getTime() < endTime.getTime()){
				
				XivelyRequest request = new XivelyRequest();
				request.addUrl("" + feed.getId() + ".json")
					.config("start", iosdf.format(dateStart))
					.config("duration", stepLength + "minutes")
					.config("limit", "" + LIMIT)
					.config("interval", "60");
				
				FeedVO response = null;
				boolean done = false;
				while(!done){
					try {
						response = (FeedVO)XivelyUtils.sendRequestToXively(request, FeedVO.class);
						Thread.sleep(PER_FETCH_DELAY);
					} catch (IOException e) {
						logger.error("Broken IO detected, retry.");
						Thread.sleep(BREAKDOWN_DELAY);
						continue; // 如果爬取失败，则进行重试
					}
					done = true;
				}
				
				int nullCnt = 0;
				for(DatastreamVO ds : response.getDataStreams()){
					if(ds.getDatapoints() == null){
						logger.info("Null datapoints for " + ds.getId()); // 重新计算爬取步长
						nullCnt++;
						continue;
					}
					logger.info("Datastream = " + ds.getId() + ", Count = " + ds.getDatapoints().size());
					for(DataPointVO point : ds.getDatapoints()){
						Datapoint dss = new Datapoint();
						dss.setFeedId(feed.getId());
						dss.setDatastreamId(ds.getId());
						dss.setTimestamp(XivelyUtils.parseTime(point.getAt()));
						dss.setVal(point.getValue());
						dplist.add(dss);
					}
				}
				parts = Math.max(1, (dslist.size() - nullCnt) * (24*60) / LIMIT) + 1;
				stepLength = (int)(24.0*60/Math.min(MIN_PARTS, parts));
				dateStart = TimeHelper.timeTravel(dateStart, Calendar.MINUTE, stepLength);
			} // end part
			ssRepo.save(dplist);
			dsRepo.flush();
			dplist = new ArrayList<Datapoint>();
			logger.info("Saved!");
		}
	}

}
