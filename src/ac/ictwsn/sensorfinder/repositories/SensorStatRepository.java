package ac.ictwsn.sensorfinder.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.ictwsn.sensorfinder.entities.Datapoint;

public interface SensorStatRepository extends JpaRepository<Datapoint, Long> {
	
	
}
