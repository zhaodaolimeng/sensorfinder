package ac.ictwsn.sensorfinder.service.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.task.LuceneIndexingTask;
import ac.ictwsn.sensorfinder.task.TaskState;

@Service
@Transactional
@PropertySource("classpath:indexing.properties")
public class LuceneService {

	private static final Logger logger = Logger.getLogger(LuceneService.class);

	private LuceneIndexingTask luceneIndexingTask; 
	private Directory directory;
	

	@Autowired
	private SensorRepository sensorRepo;
	
	@Value("${lucene.index.location}")
	private String indexPath;

	@PostConstruct
	private void init() {
		try {
			directory = FSDirectory.open(Paths.get(indexPath));
			DirectoryReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error creating FSDirectory");
		}
	}

	/**
	 * Concatenate title description and sensor tags as the document for indexing 
	 * Create a new thread to index
	 * @param options
	 */
	public void startBuildLuceneIndex(ArrayList<String> options) {
		logger.info("Ready to index ... ");
		try {
			luceneIndexingTask = new LuceneIndexingTask(options, sensorRepo, indexPath);
			Thread indexThread = new Thread(luceneIndexingTask);
			indexThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TaskState checkIndexingState(){
		if(this.luceneIndexingTask == null)
			return TaskState.NOT_STARTED;
		else 
			return luceneIndexingTask.getState();
	}

}
