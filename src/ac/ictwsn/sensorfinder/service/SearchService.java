package ac.ictwsn.sensorfinder.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ac.ictwsn.core.util.Pair;
import ac.ictwsn.sensorfinder.dto.ResultDTO;
import ac.ictwsn.sensorfinder.dto.SensorDocument;
import ac.ictwsn.sensorfinder.entities.Feature;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;
import ac.ictwsn.sensorfinder.repositories.FeatureRepository;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.service.index.LuceneService;
import ac.ictwsn.sensorfinder.service.index.MalletService;
import ac.ictwsn.sensorfinder.utils.IndexUtil;
import ac.ictwsn.sensorfinder.web.model.D3Response;

@Service
public class SearchService {
	
	private static final Logger logger = Logger.getLogger(SearchService.class);
	
	@Autowired
	private MalletService ms;
	@Autowired
	private LuceneService ls;
	
	@Value("${lucene.index.location}")
	private String luceneIndexPath;
	@Value("${mallet.index.location}")
	private String malletIndexPath;
	
	@Autowired
	private SensorRepository sensorRepo;
	@Autowired
	private FeedRepository feedRepo;
	@Autowired
	private FeatureRepository featureRepo;
	
	// Cache
	private Directory directory;
	private HashMap<String, String> spatialMap;
	private HashMap<String, Date> temporalMap;
	
	@PostConstruct
	private void init() {
		try {
			spatialMap = new HashMap<String, String>();
			temporalMap = new HashMap<String, Date>();
			
			logger.info("Load lucene index...");
			directory = FSDirectory.open(Paths.get(luceneIndexPath));
			DirectoryReader.open(directory);
			
			logger.info("Load sensor temporal-spartial message...");
			HashMap<Long, String> feedSpatialMap = new HashMap<Long, String>();
			HashMap<Long, Date> feedTemporalMap = new HashMap<Long, Date>();
			for(Feed feed : feedRepo.findAll()){
				if(feed.getLat()==null || feed.getLng()==null)
					continue;
				feedSpatialMap.put(feed.getId(), feed.getLat() + "," + feed.getLng());
				feedTemporalMap.put(feed.getId(), feed.getCreated());
			}
			for(Feature feature : featureRepo.findAll()){
				String fsStr = feature.getFeedid() + "," + feature.getStreamid();
				spatialMap.put(fsStr, feedSpatialMap.containsKey(feature.getFeedid()) ? 
										feedSpatialMap.get(feature.getStreamid()) : null);
				temporalMap.put(fsStr, feedTemporalMap.containsKey(feature.getFeedid()) ? 
										feedTemporalMap.get(feature.getStreamid()) : null);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error creating FSDirectory");
		}
	}
	
	
	/**
	 * Search with Lucene rank and DMR rank
	 * 
	 * @param queryStr
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public ResultDTO searchByLuceneAndTopic(String queryStr, int resultNum) 
			throws IOException, InvalidTokenOffsetsException {
		
		final double beta = 0.4;
		
		ResultDTO dmrScore = ms.computeDMRScore(queryStr);
		ResultDTO luceneScore = ls.computeLuceneScore(queryStr, ms.getSensorNames().size());
		
		List<SensorDocument> dmrList = dmrScore.getItemlist();
		List<SensorDocument> lucList = luceneScore.getItemlist();
		HashMap<Pair<Long, String>, SensorDocument> dmrRank = new HashMap<Pair<Long, String>, SensorDocument>();
		HashMap<Pair<Long, String>, SensorDocument> lucRank = new HashMap<Pair<Long, String>, SensorDocument>();
		
		for(SensorDocument sd : dmrList){
			Pair<Long, String> pair = new Pair<Long, String>(sd.getFeedid(), sd.getSensorid()); 
			dmrRank.put(pair, sd);			
		}
		for(SensorDocument sd : lucList){
			Pair<Long, String> pair = new Pair<Long, String>(sd.getFeedid(), sd.getSensorid());
			if(dmrRank.containsKey(pair))
				lucRank.put(pair, sd);
		}
		HashMap<Pair<Long, String>, Double> mergedRank = new HashMap<Pair<Long, String>, Double>();
		List<SensorDocument> docList = new ArrayList<SensorDocument>(); 
		
		// intersection set of dmr and lucene
		for(Entry<Pair<Long, String>, SensorDocument> entry : dmrRank.entrySet()){
			Pair<Long, String> pair = entry.getKey();
			if(lucRank.containsKey(entry.getKey())){
				mergedRank.put(entry.getKey(), dmrRank.get(entry.getKey()).getScore());
				Double lrank = - Math.log(lucRank.get(pair).getScore());
				Double drank = Math.log(entry.getValue().getScore());
				SensorDocument sensor = new SensorDocument(
						pair.getFirst(), pair.getSecond(), (1-beta) * lrank + beta * drank); 
				docList.add(sensor);
			}
		}
		
		Collections.sort(docList, new Comparator<SensorDocument>(){
			@Override
			public int compare(SensorDocument arg0, SensorDocument arg1) {
				return Double.compare(arg0.getScore(), arg1.getScore());
			}
		});
		
		ResultDTO result = new ResultDTO();
		if(resultNum != -1)
			result.setItemlist(docList.subList(0, resultNum));
		
		for(SensorDocument sd : docList){
			Pair<Long, String> pair = new Pair<Long, String>(sd.getFeedid(), sd.getSensorid());
			String lucFeedDesc = lucRank.get(pair).getFeedDescription();
			String lucSensorDesc = lucRank.get(pair).getSensorDescription();
			sd.setFeedDescription(lucFeedDesc);
			sd.setSensorDescription(lucSensorDesc);
		}
		result.setItemlist(docList);
		return result;
	}
	
	
	/**
	 * Show sensors' document TFIDF relationship, similar to topic vector
	 * 
	 * @param feedid
	 * @param streamid
	 * @param topSensorNum
	 * @return
	 */
	public D3Response visualQueryForDoc(Long feedid, String streamid, Integer topSensorNum) {
		logger.info("Gather document information for graphical show ...");
		if(ms.getSensorTopics() == null) ms.loadDMRIndex();
		
		// candidate sensors from tfidf rank
		Sensor tsensor = sensorRepo.findByFeedAndStreamid(feedid, streamid);
		SensorDocument[] docRank = ls.rankDocument(tsensor, topSensorNum);
		Arrays.sort(docRank);
		
		// draw graph using topic relationships  
		D3Response vresult = new D3Response();
		Integer threshold = 4;
		String targetSensorName = feedid + "," + streamid;
		
		for(int i=0; i<topSensorNum; i++){
			String csensor0 = docRank[i].getFeedSensorStr();
			Integer maxTopicIndex = 0; // find the index of topic with smallest distance
			Double maxProb = 0.0;
			Double[] ctopic0 = ms.getSensorTopics().get(csensor0);
			if(ctopic0 == null) continue;
			
			for(int j = 0; j < ms.getK(); j++){
				if(ctopic0[j] > maxProb){
					maxProb = ctopic0[j];
					maxTopicIndex = j;
				}
			}
			vresult.addNode(csensor0, maxTopicIndex);
			
			//FIXME possible null pointer if the count of document vector is larger than topic vector
			int linkWeight = IndexUtil.getLinkWeight(ctopic0, ms.getSensorTopics().get(targetSensorName));
			if(!targetSensorName.equals(csensor0)) 
				vresult.addLink(targetSensorName, csensor0, Math.max(1, linkWeight - threshold));
			
			for(int j=0; j<topSensorNum; j++){
				if(i == j) continue;
				String csensor1 = docRank[j].getFeedSensorStr();
				Double[] ctopic1 = ms.getSensorTopics().get(csensor1);
				if(ctopic1 == null) continue;
				linkWeight = IndexUtil.getLinkWeight(ctopic0, ctopic1); 
				if(linkWeight > threshold) 
					vresult.addLink(csensor0, csensor1, linkWeight - threshold);
			}
		}
		return vresult;
	}


	/**
	 * Read sensor id, return graph method:
	 * 
	 * 1. Search top 10 similar nodes
	 * 	- Takes sensor's description as query for tf-idf score
	 * 	- Sensor's Topic similarity score
	 *  - Deployment time relation
	 *  - Location similarity
	 * 
	 * 2. For each sensor, list top 10 similar sensors using the same routine.
	 *    The nodes' color is based on the highest proportion of topics.
	 *      
	 * 3. For each sensor pair, compute the similarity between them.
	 *    These similarity is then used as link weights.
	 *  
	 * @param feedid
	 * @param streamid
	 * @param topSensorNum	
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public D3Response visualQueryById(Long feedid, String streamid, Integer topSensorNum) {
		
		logger.info("Searching for related sensors for feedid = " + feedid + ", and streamid=" + streamid);
		if(ms.getSensorTopics() == null) ms.loadDMRIndex();
		
		Sensor tsensor = sensorRepo.findByFeedAndStreamid(feedid, streamid);
		logger.info("Sensor found at datastream_t for id = " + tsensor.getId());
		
		SensorDocument[] topicRank = ms.rankTopic(ms.getSensorNameList(), ms.getSensorTopics(), feedid + "," + streamid);
		SensorDocument[] docRank = ls.rankDocument(tsensor, topSensorNum);
		SensorDocument[] spatialRank = rankSpatial(tsensor);
		SensorDocument[] temporalRank = rankTemporal(tsensor);
		Arrays.sort(topicRank);
		Arrays.sort(docRank);
		Arrays.sort(spatialRank);
		Arrays.sort(temporalRank);
		
		logger.info("Sensor loaded, with length = " + 
				topicRank.length + ", " + docRank.length + ", " + 
				spatialRank.length + ", " + temporalRank.length);
		
		// For each list, select the top sensors
		// create a linked graph between these sensors
		HashMap<String, Double> sensorMap = new HashMap<String, Double>();
		for(int i=0; i < topSensorNum; i++){
			SensorDocument[] candidatePair = {topicRank[i], docRank[i], spatialRank[i], temporalRank[i]};
			for(SensorDocument c : candidatePair){
				if(!sensorMap.containsKey(c.getFeedSensorStr()))
					sensorMap.put(c.getFeedSensorStr(), Double.MAX_VALUE);
				double val = c.getScore() < sensorMap.get(c.getFeedSensorStr()) ? 
						c.getScore() : sensorMap.get(c.getScore()); 
				sensorMap.put(c.getFeedSensorStr(), val);
			}
		}
		logger.info("sensor candidates selected!");
		
		// For every 2 pair of these sensors, compute the relative link weights
		List<String> slist = new ArrayList<String>();
		List<Integer> importance = new ArrayList<Integer>();
		for(Entry<String, Double> entry : sensorMap.entrySet()){
			slist.add(entry.getKey());
			importance.add((int)(entry.getValue() * 10)); 
		}
		Double[][] topicMatrix = ms.topicSimilarity(slist);
		Double[][] docMatrix = ls.docSimilarity(slist);
		Double[][] timeMatrix = timeSimilarity(slist);
		Double[][] locMatrix = locationSimilarity(slist);
		Double[][] matrix = new Double[docMatrix.length][docMatrix.length];
		for (int i = 0; i < locMatrix.length; i++) {
			for (int j = 0; j < locMatrix.length; j++) {
				matrix[i][j] = topicMatrix[i][j] * 0.25 +
						docMatrix[i][j] * 0.25 +
						timeMatrix[i][j] * 0.25 +
						locMatrix[i][j] * 0.25;
			}
		}
		logger.info("Distance matrix computed.");
		D3Response vresult = new D3Response();
		// node wrapper 
		for(int i=0; i<ms.getSensorNameList().size(); i++)
			vresult.addNode(ms.getSensorNameList().get(i), importance.get(i));
		
		//link wrapper
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if(i==j) continue;
				vresult.addLink(
						ms.getSensorNameList().get(i), 
						ms.getSensorNameList().get(j), (int)(10 * matrix[i][j]));
			}
		}
		return vresult;
	}
	
	private SensorDocument[] rankSpatial(Sensor tsensor){
		logger.info("Ranking sensor spatial similarity for all sensors ...");
		
		Feed tfeed = tsensor.getFeed();
		Double tlat = tfeed.getLat() == null ? 0.0 : Double.parseDouble(tfeed.getLat());
		Double tlng = tfeed.getLng() == null ? 0.0 : Double.parseDouble(tfeed.getLng());
		List<SensorDocument> spatiallist = new ArrayList<SensorDocument>();
		HashMap<Long, String> feedPositionMap = new HashMap<Long, String>();
		
		for(Feed feed : feedRepo.findAll()){
			if(feed.getLat()==null || feed.getLng()==null)
				continue;
			feedPositionMap.put(feed.getId(), feed.getLat() + "," + feed.getLng());
		}
		
		for(Feature feature : featureRepo.findAll()){
			String[] latlng = feedPositionMap.get(feature.getFeedid()).split(",");
			Double lat = Double.parseDouble(latlng[0]);
			Double lng = Double.parseDouble(latlng[1]);
			
			// geo distance
			Double distance = (lat - tlat)*(lat - tlat) + (lng - tlng)*(lng - tlng);
			spatiallist.add(new SensorDocument(tfeed.getId(), tsensor.getStreamId(), distance));
		}
		return IndexUtil.normalization(spatiallist);
	}
	
	private SensorDocument[] rankTemporal(Sensor tsensor){
		logger.info("Ranking temporal similarity for all sensors ...");
		
		List<SensorDocument> temporallist = new ArrayList<SensorDocument>();
		HashMap<Long, Date> feedTimeMap = new HashMap<Long, Date>();
		for(Feed feed : feedRepo.findAll())
			feedTimeMap.put(feed.getId(), feed.getCreated());
		
		for(Feature feature : featureRepo.findAll()){
			Date date = feedTimeMap.get(feature.getFeedid());
			Double distance = (double)Math.abs(date.getTime() - tsensor.getFeed().getCreated().getTime());
			temporallist.add(new SensorDocument(tsensor.getFeed().getId(), tsensor.getStreamId(), distance));
		}
		return IndexUtil.normalization(temporallist);
	}
	
	private Double[][] locationSimilarity(List<String> selectedSensors) {
		int setSize = selectedSensors.size();
		Double minSim = Double.MAX_VALUE;
		Double maxSim = Double.MIN_VALUE;
		Double[][] matrix = new Double[setSize][setSize];
		for(int i=0;i<setSize; i++){
			for (int j = 0; j < matrix.length; j++) {
				if(i==j) continue;
				String[] loc0 = spatialMap.get(i).split(",");
				String[] loc1 = spatialMap.get(j).split(",");
				Double lat0 = Double.parseDouble(loc0[0]), lng0 = Double.parseDouble(loc0[1]);
				Double lat1 = Double.parseDouble(loc1[0]), lng1 = Double.parseDouble(loc1[1]);
				matrix[i][j] = (lat0 - lat1)*(lat0 - lat1) + (lng0 - lng1)*(lng0 - lng1);
				minSim = (matrix[i][j]<minSim)?matrix[i][j]:minSim;
				maxSim = (matrix[i][j]>maxSim)?matrix[i][j]:maxSim;
			}
		}
		// Normalize
		for(int i=0;i<setSize; i++)
			for (int j = 0; j < matrix.length; j++) 
				matrix[i][j] = (matrix[i][j] - minSim)/(maxSim - minSim);
		return matrix;
	}

	private Double[][] timeSimilarity(List<String> selectedSensors) {
		int setSize = selectedSensors.size();
		Double[][] matrix = new Double[setSize][setSize];
		for(int i=0;i<setSize; i++){
			for (int j = 0; j < matrix.length; j++) {
				if(i==j) continue;
				Double t0 = (double)temporalMap.get(i).getTime()/3600/24;
				Double t1 = (double)temporalMap.get(j).getTime()/3600/24;
				matrix[i][j] = (t0-t1)*(t0-t1);
			}
		}
		return matrix;
	}

	/**
	 * This is used to test the front-end visualization service
	 * @return
	 */
	public D3Response fakeDataGenerate() {
		D3Response result = new D3Response();
		result.addNode("Napoleon", 1);
		result.addNode("Mlle.Baptistine", 1);
		result.addNode("Myriel", 1);
		result.addLink("Napoleon", "Myriel", 1);
		result.addLink("Mlle.Baptistine", "Myriel", 8);
		return result;
	}


}

