package ac.ictwsn.sensorfinder.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import ac.ictwsn.sensorfinder.dto.SensorDocument;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;

public class SensorRepositoryImpl implements SensorRepositoryCustom{
	
	private static final Logger logger = Logger.getLogger(SensorRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<SensorDocument> fastFindAll() {
		
		// The join operation of MySQL is stupid, use a map instead.
		Session session = entityManager.unwrap(Session.class);

		List<Feed> feedlist = session.createQuery("from Feed").list();
		HashMap<Long, Feed> feedMap = new HashMap<Long, Feed>();
		for(Feed f : feedlist)
			feedMap.put(f.getId(), f);
		
		logger.info("Query for list all sensor started");
		List<Sensor> sensorlist = session.createQuery("from Sensor").list();
		List<SensorDocument> result = new ArrayList<SensorDocument>();
		for(Sensor s : sensorlist){
			Feed f = feedMap.get(s.getFeedid()); // DO NOT USE ENTITY FEED FOR EFFICIENCY
			SensorDocument sd =new SensorDocument(
					f.getId(), f.getTitle(), f.getDescription(), f.getTags(),  
					s.getStreamId(), s.getTags());
			result.add(sd);
		}
		logger.info("Sensor fetch done!");
		return result;
	}
}
