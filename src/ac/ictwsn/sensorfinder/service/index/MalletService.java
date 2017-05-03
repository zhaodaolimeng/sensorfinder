package ac.ictwsn.sensorfinder.service.index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.ictwsn.sensorfinder.repositories.FeatureRepository;
import ac.ictwsn.sensorfinder.task.MalletIndexingTask;
import ac.ictwsn.sensorfinder.task.TaskState;

@Service
@Transactional
@PropertySource("classpath:indexing.properties")
public class MalletService {
	
	private static final Logger logger = Logger.getLogger(MalletService.class);
	
	@Autowired
	private FeatureRepository featureRepo;

	private MalletIndexingTask malletIndexingTask;
	
	@Value("${mallet.index.location}")
	private String indexPath;
	
	// In-memory index of mallet dmr result
	HashMap<String, List<Double>> sensorTopics; // topics per each sensor
	List<String> sensorNameList;
	List<String> wordDict;
	HashMap<String, Double[]> wordTopics; //phi, will be used for query, need pre-load
	
	/**
	 * Check and register mallet index to memory before service is called.
	 */
	@PostConstruct
	private void init() {
		logger.info("Initializing MalletService...");
		loadDMRIndex();
	}
	
	/**
	 * Service uses two different data structure to index topics:
	 * sensorNameList contains string of "feedid,streamid" pairs
	 * sensorTopics contains a {"feedid,streamid" : topic vector}
	 */
	public void loadDMRIndex(){
		try {
			sensorNameList = buildSensorMap();
			
			// Load dictionary
			logger.info("Prepare word dictionary for query filtering ...");
			FileReader fr = new FileReader(indexPath + "/" + "dictionary.txt");
			BufferedReader br = new BufferedReader(fr);
			String word = "";
			wordDict = new ArrayList<String>();
			while((word = br.readLine()) != null)
				this.wordDict.add(word);
			br.close();
			fr.close();
			
			// Load topic vector theta
			logger.info("Prepare in-memory DMR index...");
			fr = new FileReader(indexPath + "/" + "dmr-topics.txt");
			br = new BufferedReader(fr);
			sensorTopics = new HashMap<String, List<Double>>();
			String line = null;
			String[] topicstr = null;
			br.readLine(); // Jump first line
			int cnt = 0;
			while((line = br.readLine()) != null){
				List<Double> topics = new ArrayList<Double>();
				topicstr = line.split(" ");
				HashMap<Integer, Double> topicMap = new HashMap<Integer, Double>();
				for(int i = 2; i < topicstr.length; i += 2)
					topicMap.put(Integer.parseInt(topicstr[i]), Double.parseDouble(topicstr[i + 1]));
				for(int num = 0; topicMap.containsKey(num); num ++)
					topics.add(topicMap.get(num));
				String feedstream = sensorNameList.get(cnt++);
				sensorTopics.put(feedstream, topics);
			}
			br.close();
			fr.close();
			
			// Load topic-word relationship of original documents z
			
			
		} catch (FileNotFoundException f404){
			f404.printStackTrace();
			logger.error("Mallet index not found!");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error creating FSDirectory");
		}
	}
	
	private List<String> buildSensorMap() throws IOException{
		logger.info("Loading sensor topic vector...");
		List<String> result = new ArrayList<String>();
		FileReader fr = new FileReader(indexPath + "/" + "id.txt");
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine()) != null)
			result.add(line);
		fr.close();
		br.close();
		logger.info("Sensor topic vector loading done!");
		return result;
	}
	
	/**
	 * Use Mallet plugin for Topic extraction
	 * @param options
	 * @throws IOException 
	 */
	public void startBuildTopicIndex(ArrayList<String> options) {
		logger.info("Start building mallet index.");
		malletIndexingTask = new MalletIndexingTask(indexPath, featureRepo);
		Thread indexThread = new Thread(malletIndexingTask);
		indexThread.start();
	}
	
	/**
	 * check indexing state
	 * @return
	 */
	public TaskState checkIndexingState() {
		if(malletIndexingTask == null)
			return TaskState.NOT_STARTED;
		return malletIndexingTask.getState();
	}
	
	public HashMap<String, List<Double>> getTopicVector(){
		return this.sensorTopics;
	}
	
	public List<String> getNameList(){
		return this.sensorNameList;
	}
	
	public List<String> getDictionary(){
		return this.wordDict;
	}
	
}
