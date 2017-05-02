package ac.ictwsn.sensorfinder.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ac.ictwsn.sensorfinder.entities.Feed;

@Repository
public interface FeedRepository extends DataTablesRepository<Feed, Long> {
	
	public List<Feed> findAfterIdOrderById(Long id);
	
	@Query("select f from Feed f where f.status='live'")
	public List<Feed> findAlive();
	
	@Query("select f from Feed f where f.status='live' and f.created<:created_time")
	public List<Feed> findAliveAndStable(@Param("created_time")Date t);
	
	@Query("select f from Feed f where f.id=:id")
	public Feed findById(@Param("id")Long id);
	
}
