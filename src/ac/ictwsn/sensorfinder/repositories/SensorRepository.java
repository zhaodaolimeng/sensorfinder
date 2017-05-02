package ac.ictwsn.sensorfinder.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;

public interface SensorRepository 
	extends JpaRepository<Sensor, Long>, SensorRepositoryCustom{
	
	public List<Sensor> findByFeed(Feed feed);
	
	@Query("select s from Sensor s where s.feed.id=:fid and s.streamId=:sid")
	public Sensor findByFeedAndStreamid(
			@Param("fid") Long feedid, 
			@Param("sid") String streamid);
}
