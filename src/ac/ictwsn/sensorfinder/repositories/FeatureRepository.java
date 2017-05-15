package ac.ictwsn.sensorfinder.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ac.ictwsn.sensorfinder.entities.Feature;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
	
	public Feature findByFeedidAndStreamid(Long feedid, String streamid);
	
	
	@Query("select min(f.created) from Feature f")
	public Date findMinCreated();
}
