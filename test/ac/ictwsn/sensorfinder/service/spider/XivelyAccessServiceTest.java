package ac.ictwsn.sensorfinder.service.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ac.ictwsn.sensorfinder.config.AppConfig;
import ac.ictwsn.sensorfinder.entities.Datapoint;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorStatRepository;
import ac.ictwsn.sensorfinder.service.XivelyAccessService;
import ac.ictwsn.sensorfinder.utils.XivelyUtils;
import ac.ictwsn.sensorfinder.web.model.xively.DataPointVO;
import ac.ictwsn.sensorfinder.web.model.xively.DatastreamVO;
import ac.ictwsn.sensorfinder.web.model.xively.FeedVO;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class XivelyAccessServiceTest extends AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	XivelyAccessService xas;
	@Autowired
	FeedRepository fr;
	@Autowired
	SensorStatRepository ssr;
	
	@Test
	@Rollback(true)
	public void updateAllFeedsTest(){
		
		//FIXME A concurrent method should be used
		// https://github.com/jhalterman/concurrentunit
		// https://github.com/junit-team/junit4/wiki/multithreaded-code-and-concurrency
		xas.syncUpdateAllFeeds();
	}
	
	@Test
	public void saveSingleDataStreamTest(){
		List<String> feeds = new ArrayList<String>();
		feeds.add("1103963078");
		xas.updateDatastreams(feeds);
	}
	
	@Test
	public void batchSaveSensorTest(){
		
		final long PER_FETCH_DELAY = 1500L;
		final long BREAKDOWN_DELAY = 5000L;
		
		String duriation = "2days";
		String interval = "900";
		
		List<Feed> feedlist = fr.findAlive();
		for(Feed f : feedlist){
			
			Long feedId = f.getId();
			XivelyRequest request = new XivelyRequest();
			request.addUrl("" + feedId + ".json")
				.config("duration", duriation)
				.config("interval", interval);
			
			// get datastream for each feed
			FeedVO response = null;
			try {
				response = (FeedVO)XivelyUtils.sendRequestToXively(request, FeedVO.class);
				Thread.sleep(PER_FETCH_DELAY);
			} catch (IOException e) {
				System.out.println("Broken IO detected, retry.");
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
					System.out.println("Null datapoints for " + ds.getId());
					continue;
				}
				for(DataPointVO point : ds.getDatapoints()){
					Datapoint dss = new Datapoint();
					dss.setDatastreamId(ds.getId());
					dss.setDuration(duriation);
					dss.setFeedId(feedId);
					dss.setTimestamp(XivelyUtils.parseTime(point.getAt()));
					dss.setVal(point.getValue());
					ssr.save(dss);
				}
			}
		}
	}
}
