package ac.ictwsn.sensorfinder.repositories;

import java.util.List;

import ac.ictwsn.sensorfinder.dto.SensorDocument;

public interface SensorRepositoryCustom {
	
	public List<SensorDocument> fastFindAll();
	
}
