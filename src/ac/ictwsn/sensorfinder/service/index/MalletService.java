package ac.ictwsn.sensorfinder.service.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.ictwsn.sensorfinder.dto.ResultDTO;
import ac.ictwsn.sensorfinder.dto.SensorDocument;
import ac.ictwsn.sensorfinder.entities.Sensor;
import ac.ictwsn.sensorfinder.repositories.FeatureRepository;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.task.MalletIndexingTask;
import ac.ictwsn.sensorfinder.task.TaskState;
import ac.ictwsn.sensorfinder.utils.IndexUtil;
import ac.ictwsn.sensorfinder.web.model.D3Response;

@Service
@Transactional
@PropertySource("classpath:indexing.properties")
public class MalletService {
	
	private static final Logger logger = Logger.getLogger(MalletService.class);
	
	private final String DMR_DUMP_FILE = "dump.txt";
	private final String THETA_FILE = "dmr-topics.txt";
	private final String Z_FILE = "dmr.state.gz";
	private final String ID_FILE = "id.txt";
	
	@Autowired
	private FeatureRepository featureRepo;
	@Autowired
	private SensorRepository sensorRepo;

	private MalletIndexingTask malletIndexingTask;
	
	@Value("${mallet.index.location}")
	private String indexPath;
	
	// In-memory index of mallet dmr result
	//FIXME dirty code
	private HashMap<String, Double[]> sensorTopics; // topics per each sensor
	private HashMap<String, Double[]> wordTopics; // topic per each word
	private List<String> sensorNameList;
	private int K; // topic number
	
	public List<String> getSensorNames(){
		return this.sensorNameList;
	}
	
	public HashMap<String, Double[]> getSensorTopics(){
		return this.sensorTopics;
	}
	
	public int getK(){
		return this.K;
	}
	
	public List<String> getSensorNameList(){
		return this.sensorNameList;
	}
	
	
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
			
			// Load topic vector theta
			logger.info("Prepare in-memory DMR index...");
			FileReader fr = new FileReader(indexPath + "/" + this.THETA_FILE);
			BufferedReader br = new BufferedReader(fr);
			sensorTopics = new HashMap<String, Double[]>();
			
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
				
				Double[] topicArray = new Double[K];
				sensorTopics.put(feedstream, topics.toArray(topicArray));
			}
			br.close();
			fr.close();
			
			// Load topic metadata
			fr = new FileReader(indexPath + "/" + this.DMR_DUMP_FILE);
			br = new BufferedReader(fr);
			br.readLine(); // start time
			String topicNumStr = br.readLine(); // topic number
			this.K = Integer.parseInt(topicNumStr.split("=")[1]);
			br.close();
			fr.close();
			
			// Load topic-word relationship of original documents z
			this.wordTopics = new HashMap<String, Double[]>();
			InputStream fileStream = new FileInputStream(indexPath + "/" + this.Z_FILE);
			Reader decoder = new InputStreamReader(new GZIPInputStream(fileStream), StandardCharsets.UTF_8);
			BufferedReader buffered = new BufferedReader(decoder);
			
			/**
			 * Count topic occurrence for each word 
			 * 
			 * #doc source pos typeindex type topic
			 * 0 NA 0 0 addit 6
			 * 0 NA 1 1 monitoring 5
			 */
			buffered.readLine(); // skip first line
			String topicItem = "";
			while((topicItem = buffered.readLine()) != null){
				String[] record = topicItem.split(" ");
				if(!wordTopics.containsKey(record[4]))
					wordTopics.put(record[4], new Double[this.K]);
				Double[] vector = wordTopics.get(record[4]);
				if(vector[Integer.parseInt(record[5])] == null){
					vector[Integer.parseInt(record[5])] = 1.0;
				}else{
					vector[Integer.parseInt(record[5])] += 1.0;
				}
				
			}
			//For each word
			for(Entry<String, Double[]> entry : this.wordTopics.entrySet()){
				Double[] vector = entry.getValue();
				
				for(int i=0; i<this.K; i++)
					if(vector[i] == null) vector[i] = 0.0;
				
				double sum = 0.0;
				for(int i=0; i<this.K; i++)
					sum += vector[i];
				for(int i=0; i<this.K; i++)
					vector[i] /= sum;
			}
			buffered.close();
			decoder.close();
			fileStream.close();
			
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
		FileReader fr = new FileReader(indexPath + "/" + this.ID_FILE);
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
	
	public List<String> getNameList(){
		return this.sensorNameList;
	}
	
	
	
	/**
	 * Get documents with topnum largest DMR scores
	 *  
	 * @param queryStr
	 * @param topnum
	 * @return {feedid + ',' + streamid : similarity}
	 */
	public ResultDTO computeDMRScore(String queryStr){
		
		// pre-process queryStr
		Double alpha = 1e-6;
		String[] qList = queryStr
				.replaceAll("\n", " ")
				.replaceAll("^(https?|ftp)://.*$", " ")
				.replaceAll("^[0-9]+$", " ")
				.replaceAll("[^a-zA-Z]", " ")
				.toLowerCase().split(" ");
		
		// compute bow for vector
		Double[] qvec = new Double[K]; // topic vector of query
		for(String q : qList){
			if(!this.wordTopics.containsKey(q)) continue;
			for(int i=0; i<K; i++){
				if(qvec[i] == null) qvec[i] = 0.0;
				qvec[i] += this.wordTopics.get(q)[i];
			}
		}
		qvec = IndexUtil.normalization(qvec);
		List<SensorDocument> docList = new ArrayList<SensorDocument>();
		
		// compute vector similarity
		for(Entry<String, Double[]> entry : sensorTopics.entrySet()){
			String feedStream = entry.getKey();
			Double distance = 0.0;
			Double[] dvec = entry.getValue();
			for(int i=0; i<K; i++){
				if(dvec[i] == null) dvec[i] = 0.0;
				dvec[i] += alpha;
				qvec[i] += alpha;
				distance += (qvec[i] - dvec[i]) * Math.log(qvec[i]/dvec[i]);
			}
			SensorDocument doc = new SensorDocument();
			doc.setFeedid(Long.parseLong(feedStream.split(",")[0]));
			doc.setSensorid(feedStream.split(",")[1]);
			doc.setScore(distance);
			docList.add(doc);
		}
		
		Collections.sort(docList, new Comparator<SensorDocument>(){
			public int compare(SensorDocument arg0, SensorDocument arg1) {
				return Double.compare(arg0.getScore(), arg1.getScore());
			}
		});
		
		ResultDTO result = new ResultDTO();
		result.setItemlist(docList);
		
		return result;
	}
	
	/**
	 * Show sensors whose topic vector is similar to the input as a D3 graph, in which
	 * 		1. Nodes are different sensors, which are top N similar to input sensor.
	 * 		2. Nodes' color are related to sensor's dominate topic.
	 * 		3. Weight of links are sensors' similarity(reciprocal distance).
	 * 
	 * @param feedid
	 * @param streamid
	 * @param topSensorNum
	 * @return
	 */
	public D3Response visualQueryForTopic(Long feedid, String streamid, Integer topSensorNum){
		logger.info("Gather topic information for graphical show ...");
		if(this.sensorTopics == null)
			loadDMRIndex();
		
		List<String> nameList = this.sensorNameList; // load from MalletIndex.java
		Sensor tsensor = sensorRepo.findByFeedAndStreamid(feedid, streamid);
		String targetSensorName = feedid + "," + streamid;
		logger.info("Sensor found at datastream_t for id = " + tsensor.getId());
		
		// find candidates sensors
		SensorDocument[] topicRank = rankTopic(nameList, sensorTopics, targetSensorName);
		Arrays.sort(topicRank);
		
		// compute relationship between candidates
		D3Response vresult = new D3Response();
		Integer threshold = 9;
		
		for(int i=0; i<topSensorNum; i++){
			String csensor0 = topicRank[i].getFeedSensorStr();
			Integer maxTopicIndex = 0; // find the index of topic with smallest distance
			Double maxProb = 0.0;
			Double[] ctopic0 =  sensorTopics.get(csensor0);
			for(int j = 0; j < K; j++){
				if(ctopic0[j] > maxProb){
					maxProb = ctopic0[j];
					maxTopicIndex = j;
				}
			}
			vresult.addNode(csensor0, maxTopicIndex);
			int linkWeight = IndexUtil.getLinkWeight(ctopic0, sensorTopics.get(targetSensorName));
			if(linkWeight > threshold && !targetSensorName.equals(csensor0)) 
				vresult.addLink(targetSensorName, csensor0, linkWeight - threshold);
			
			for(int j=0; j<topSensorNum; j++){
				if(i == j) continue;
				String csensor1 = topicRank[j].getFeedSensorStr();
				Double[] ctopic1 = sensorTopics.get(csensor1);
				linkWeight = IndexUtil.getLinkWeight(ctopic0, ctopic1);
				if(linkWeight > threshold) 
					vresult.addLink(csensor0, csensor1, linkWeight - threshold);
			}
		}
		return vresult;
	}
	

	public SensorDocument[] rankTopic(
			List<String> sensorNameList, 
			HashMap<String, Double[]> sensorTopics, 
			String feedstream){
		logger.info("Ranking topic similarity for all sensors ...");
		logger.info("size of topic list = " + sensorTopics.size());
		logger.info("size of sensor list = " + sensorNameList.size());
		
		List<SensorDocument> topiclist = new ArrayList<SensorDocument>();
		for(int i=0; i<sensorNameList.size(); i++){
			Double dist = 0.0;
			Double[] t0 = sensorTopics.get(sensorNameList.get(i));
			Double[] t1 = sensorTopics.get(feedstream); 
			for(int j=0; j<t0.length; j++)
				dist += (t0[j] - t1[j]) * (t0[j] - t1[j]);
			topiclist.add(new SensorDocument(sensorNameList.get(i), dist));
		}
		return IndexUtil.normalization(topiclist);
	}
	
	/**
	 * =======================
	 * check:
	 * 		topic similarity
	 * 		doc similarity
	 * 		spatial similarity
	 * 		temporal similarity
	 */
	
	/**
	 * Similarity between topic vectors, using topicMap
	 * @param a subset chosen using different ranking schema
	 * @return distance matrix between sensors
	 */
	public Double[][] topicSimilarity(List<String> selectedSensors){
		int setSize = selectedSensors.size();
		Double[][] matrix = new Double[setSize][setSize];
		Double minSim = Double.MAX_VALUE;
		Double maxSim = Double.MIN_VALUE;
		
		for(int i=0; i<setSize; i++){
			Double[] v0 = sensorTopics.get(selectedSensors.get(i));
			for (int j = 0; j < matrix.length; j++) {
				if(i==j) continue;
				Double[] v1 = sensorTopics.get(selectedSensors.get(j));
				matrix[i][j] = 0.0;
				for (int k = 0; k < v1.length; k++)
					matrix[i][j] += (v0[i] - v1[j]) * (v0[i] - v1[j]);
				minSim = (matrix[i][j] < minSim)? matrix[i][j] : minSim;
				maxSim = (matrix[i][j] > maxSim)? matrix[i][j] : maxSim;
			}
		}
		// Normalize
		for(int i=0;i<setSize; i++)
			for (int j = 0; j < matrix.length; j++) 
				matrix[i][j] = (matrix[i][j] - minSim)/(maxSim - minSim);
		return matrix;
	}
	

	Map<String, Integer> getTermFrequencies(IndexReader reader, int docId)
            throws IOException {
        Terms vector = reader.getTermVector(docId, "content");
        TermsEnum termsEnum = vector.iterator();
        Map<String, Integer> frequencies = new HashMap<>();
        
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int freq = (int) termsEnum.totalTermFreq();
            frequencies.put(term, freq);
        }
        return frequencies;
    }
	
	/**
	 * check end
	 * =======================
	 */

}
