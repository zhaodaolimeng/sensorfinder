package ac.ictwsn.sensorfinder.service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ac.ictwsn.sensorfinder.dto.ResultDTO;
import ac.ictwsn.sensorfinder.dto.SensorDocument;
import ac.ictwsn.sensorfinder.entities.Feature;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;
import ac.ictwsn.sensorfinder.exception.MalletIndexException;
import ac.ictwsn.sensorfinder.repositories.FeatureRepository;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.service.index.MalletService;
import ac.ictwsn.sensorfinder.web.model.D3Response;

@Service
public class SearchService {
	
	private static final Logger logger = Logger.getLogger(SearchService.class);
	
	private final int RESULT_SIZE = 10;
	
	@Autowired
	private MalletService ms;
	
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
	private HashMap<String, Double[]> topicMap;
	
	@PostConstruct
	private void init() {
		try {
			spatialMap = new HashMap<String, String>();
			temporalMap = new HashMap<String, Date>();
			topicMap = new HashMap<String, Double[]>();
			
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
			
			logger.info("Load topic index...");
			if(ms.getTopicVector() == null)
				ms.loadDMRIndex();
			if(ms.getTopicVector() == null)
				throw new MalletIndexException();
			
			for(Entry<String, List<Double>> entry : ms.getTopicVector().entrySet()){
				Double[] dlist = new Double[entry.getValue().size()];
				topicMap.put(entry.getKey(), entry.getValue().toArray(dlist));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error creating FSDirectory");
		} catch (MalletIndexException e) {
			e.printStackTrace();
			logger.error("Error creating Mallet index, index file not found...");
		}
	}
	
	
	/**
	 * Invoke Lucene IndexReader
	 * Use CJKAnalyser for query parsing
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException 
	 */
	public ResultDTO search(String queryStr) 
			throws IOException, InvalidTokenOffsetsException {
		
		ResultDTO result = new ResultDTO();
		CJKAnalyzer analyzer = new CJKAnalyzer();
		QueryParser queryParse = new QueryParser("content", analyzer);
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
		
		List<SensorDocument> queryResult = new ArrayList<SensorDocument>();
		try {
			Query query = queryParse.parse(queryStr);
			long start=System.currentTimeMillis();
			TopDocs hits=searcher.search(query, RESULT_SIZE);
			long end=System.currentTimeMillis();
			
			logger.info("Query str = " + queryStr);
			logger.info("Total time = " + (end-start) +"ms");
			logger.info("Record = " + hits.totalHits);
			
			QueryScorer scorer = new QueryScorer(query);
			Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("","");
			Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
			highlighter.setTextFragmenter(fragmenter);
			
			for(ScoreDoc scoreDoc:hits.scoreDocs){
				Document doc = searcher.doc(scoreDoc.doc);
				SensorDocument item = new SensorDocument();
				String desc = doc.get("content");
				if(desc != null){
					TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(desc));
					item.setSnapshot(highlighter.getBestFragment(tokenStream, desc));
				}
				Long feedid = Long.parseLong(doc.get("feedid"));
				String sensorid = doc.get("sensorid");
				item.setFeedid(feedid);
				item.setSensorid(sensorid);
				item.setScore((double)scoreDoc.score);
				
				// check datastream_t
				Sensor sensor = sensorRepo.findByFeedAndStreamid(feedid, sensorid);
				Feed feed = sensor.getFeed();
				item.setSensorLabel(sensor.getLabel());
				item.setFeedTitle(feed.getTitle());
				item.setFeedDescription(feed.getDescription());
				item.setFeedTags(feed.getTags());
				
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				item.setCreatedTime(df.format(feed.getCreated()));
				
				queryResult.add(item);
			}
			result.setTimeUsed(1.0*(end-start)/1000);
			result.setTotalHits(hits.totalHits);
			result.setItemlist(queryResult);
			
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("Error parsing query ... ");
		} 
		return result;
	}
	
	
	/**
	 * 加权的打分方法
	 * 
	 * @param queryStr
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	public ResultDTO searchWithTopic(String queryStr) 
			throws IOException, InvalidTokenOffsetsException {
		
		
		
		return null;
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
		if(ms.getTopicVector() == null)
			ms.loadDMRIndex();
		
		HashMap<String, List<Double>> sensorTopics = ms.getTopicVector();
		List<String> nameList = ms.getNameList(); // load from MalletIndex.java
		Sensor tsensor = sensorRepo.findByFeedAndStreamid(feedid, streamid);
		String targetSensorName = feedid + "," + streamid;
		logger.info("Sensor found at datastream_t for id = " + tsensor.getId());
		
		// find candidates sensors
		SensorPair[] topicRank = rankTopic(nameList, sensorTopics, targetSensorName);
		Arrays.sort(topicRank);
		
		// compute relationship between candidates
		D3Response vresult = new D3Response();
		Integer threshold = 9;
		
		for(int i=0; i<topSensorNum; i++){
			String csensor0 = topicRank[i].feedstream;
			Integer maxTopicIndex = 0; // find the index of topic with smallest distance
			Double maxProb = 0.0;
			List<Double> ctopic0 =  sensorTopics.get(csensor0);
			for(int j = 0; j < ctopic0.size(); j++){
				if(ctopic0.get(j) > maxProb){
					maxProb = ctopic0.get(j);
					maxTopicIndex = j;
				}
			}
			vresult.addNode(csensor0, maxTopicIndex);
			int linkWeight = getLinkWeight(ctopic0, sensorTopics.get(targetSensorName));
			if(linkWeight > threshold && !targetSensorName.equals(csensor0)) 
				vresult.addLink(targetSensorName, csensor0, linkWeight - threshold);
			
			for(int j=0; j<topSensorNum; j++){
				if(i == j) continue;
				String csensor1 = topicRank[j].feedstream;
				List<Double> ctopic1 = sensorTopics.get(csensor1);
				linkWeight = getLinkWeight(ctopic0, ctopic1);
				if(linkWeight > threshold) 
					vresult.addLink(csensor0, csensor1, linkWeight - threshold);
			}
		}
		return vresult;
	}
	
	private int getLinkWeight(List<Double> l0, List<Double> l1){
		double distance = 0.0;
		for(int k = 0; k < l0.size(); k++)
			distance += (l0.get(k) - l1.get(k)) * (l0.get(k) - l1.get(k)); 
		return (int)(10 / (1 + distance));
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
		if(ms.getTopicVector() == null)
			ms.loadDMRIndex();
		
		// candidate sensors from tfidf rank
		Sensor tsensor = sensorRepo.findByFeedAndStreamid(feedid, streamid);
		SensorPair[] docRank = rankDocument(tsensor, topSensorNum);
		Arrays.sort(docRank);
		
		// draw graph using topic relationships  
		D3Response vresult = new D3Response();
		Integer threshold = 4;
		String targetSensorName = feedid + "," + streamid;
		HashMap<String, List<Double>> sensorTopics = ms.getTopicVector();
		
		for(int i=0; i<topSensorNum; i++){
			String csensor0 = docRank[i].feedstream;
			Integer maxTopicIndex = 0; // find the index of topic with smallest distance
			Double maxProb = 0.0;
			List<Double> ctopic0 = sensorTopics.get(csensor0);
			if(ctopic0 == null) continue;
			
			for(int j = 0; j < ctopic0.size(); j++){
				if(ctopic0.get(j) > maxProb){
					maxProb = ctopic0.get(j);
					maxTopicIndex = j;
				}
			}
			vresult.addNode(csensor0, maxTopicIndex);
			
			//FIXME possible null pointer if the count of document vector is larger than topic vector
			int linkWeight = getLinkWeight(ctopic0, sensorTopics.get(targetSensorName));
			if(!targetSensorName.equals(csensor0)) 
				vresult.addLink(targetSensorName, csensor0, Math.max(1, linkWeight - threshold));
			
			for(int j=0; j<topSensorNum; j++){
				if(i == j) continue;
				String csensor1 = docRank[j].feedstream;
				List<Double> ctopic1 = sensorTopics.get(csensor1);
				if(ctopic1 == null) continue;
				linkWeight = getLinkWeight(ctopic0, ctopic1); 
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
		if(ms.getTopicVector() == null)
			ms.loadDMRIndex();
		
		HashMap<String, List<Double>> sensorTopics = ms.getTopicVector();
		List<String> sensorNameList = ms.getNameList(); // sensorNameList in MalletIndex
		Sensor tsensor = sensorRepo.findByFeedAndStreamid(feedid, streamid);
		logger.info("Sensor found at datastream_t for id = " + tsensor.getId());
		
		SensorPair[] topicRank = rankTopic(sensorNameList, sensorTopics, feedid + "," + streamid);
		SensorPair[] docRank = rankDocument(tsensor, topSensorNum);
		SensorPair[] spatialRank = rankSpatial(tsensor);
		SensorPair[] temporalRank = rankTemporal(tsensor);
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
			SensorPair[] candidatePair = {topicRank[i], docRank[i], spatialRank[i], temporalRank[i]};
			for(SensorPair c : candidatePair){
				if(!sensorMap.containsKey(c.feedstream))
					sensorMap.put(c.feedstream, Double.MAX_VALUE);
				double val = c.similarity < sensorMap.get(c.feedstream) ? c.similarity : sensorMap.get(c.feedstream); 
				sensorMap.put(c.feedstream, val);
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
		
		Double[][] topicMatrix = topicSimilarity(slist);
		Double[][] docMatrix = docSimilarity(slist);
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
		for(int i=0; i<sensorNameList.size(); i++)
			vresult.addNode(sensorNameList.get(i), importance.get(i));
		
		//link wrapper
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if(i==j) continue;
				vresult.addLink(
						sensorNameList.get(i), 
						sensorNameList.get(j), (int)(10 * matrix[i][j]));
			}
		}
		return vresult;
	}
	
	private SensorPair[] rankTopic(
			List<String> sensorNameList, 
			HashMap<String, List<Double>> sensorTopics, 
			String feedstream){
		logger.info("Ranking topic similarity for all sensors ...");
		logger.info("size of topic list = " + sensorTopics.size());
		logger.info("size of sensor list = " + sensorNameList.size());
		List<SensorPair> topiclist = new ArrayList<SensorPair>();
		for(int i=0; i<sensorNameList.size(); i++){
			Double dist = 0.0;
			List<Double> t0 = sensorTopics.get(sensorNameList.get(i));
			List<Double> t1 = sensorTopics.get(feedstream); 
			for(int j=0; j<t0.size(); j++)
				dist += (t0.get(j) - t1.get(j)) * (t0.get(j) - t1.get(j));
			topiclist.add(new SensorPair(dist, sensorNameList.get(i)));
		}
		return normalization(topiclist);
	}

	/**
	 * Only load necessary documents(with a count of topSensorNum)
	 * @param tsensor
	 * @param sensornum
	 * @return
	 */
	private SensorPair[] rankDocument(Sensor tsensor, Integer sensornum){
		logger.info("Ranking document similarity for all sensors ...");
		List<SensorPair> doclist = new ArrayList<SensorPair>();
		try {
			Feed tfeed = tsensor.getFeed();
			String queryStr = tfeed.getDescription() + " "
					+ tfeed.getTags() + " " + tfeed.getTitle() + " "
					+ tsensor.getTags() + " " + tsensor.getStreamId();
			CJKAnalyzer analyzer = new CJKAnalyzer();
			QueryParser queryParse = new QueryParser("content", analyzer);		
			Query query = queryParse.parse(queryStr);
			
			Directory luceneDirectory = FSDirectory.open(Paths.get(luceneIndexPath));
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(luceneDirectory));
			TopDocs hits=searcher.search(query, sensornum);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs){
				Document doc = searcher.doc(scoreDoc.doc);
				Long tfeedid = Long.parseLong(doc.get("feedid"));
				String sensorid = doc.get("sensorid");
				doclist.add(new SensorPair((double)scoreDoc.score, tfeedid + "," + sensorid));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return normalization(doclist);
	}
	
	private SensorPair[] rankSpatial(Sensor tsensor){
		logger.info("Ranking sensor spatial similarity for all sensors ...");
		
		Feed tfeed = tsensor.getFeed();
		Double tlat = tfeed.getLat() == null ? 0.0 : Double.parseDouble(tfeed.getLat());
		Double tlng = tfeed.getLng() == null ? 0.0 : Double.parseDouble(tfeed.getLng());
		List<SensorPair> spatiallist = new ArrayList<SensorPair>();
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
			spatiallist.add(new SensorPair(distance, tfeed.getId() + "," + tsensor.getStreamId()));
		}
		return normalization(spatiallist);
	}
	
	private SensorPair[] rankTemporal(Sensor tsensor){
		logger.info("Ranking temporal similarity for all sensors ...");
		
		List<SensorPair> temporallist = new ArrayList<SensorPair>();
		HashMap<Long, Date> feedTimeMap = new HashMap<Long, Date>();
		for(Feed feed : feedRepo.findAll())
			feedTimeMap.put(feed.getId(), feed.getCreated());
		
		for(Feature feature : featureRepo.findAll()){
			Date date = feedTimeMap.get(feature.getFeedid());
			Double distance = (double)Math.abs(date.getTime() - tsensor.getFeed().getCreated().getTime());
			temporallist.add(new SensorPair(distance, tsensor.getFeed().getId() + "," + tsensor.getStreamId()));
		}
		return normalization(temporallist);
	}
	
	private SensorPair[] normalization(List<SensorPair> list){
		SensorPair[] sparr = new SensorPair[list.size()];
		Double sum = 0.0;
		for(SensorPair sp : list)
			sum += sp.similarity;
		for(int i=0;i<sparr.length;i++)
			sparr[i]=new SensorPair(list.get(i).similarity/sum, list.get(i).feedstream);
		return sparr;
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
	private Double[][] topicSimilarity(List<String> selectedSensors){
		int setSize = selectedSensors.size();
		Double[][] matrix = new Double[setSize][setSize];
		Double minSim = Double.MAX_VALUE;
		Double maxSim = Double.MIN_VALUE;
		
		for(int i=0; i<setSize; i++){
			Double[] v0 = topicMap.get(selectedSensors.get(i));
			for (int j = 0; j < matrix.length; j++) {
				if(i==j) continue;
				Double[] v1 = topicMap.get(selectedSensors.get(j));
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
	 * 
	 * @param sstr sensor id string in format: feedid+","+streamid
	 * @return
	 */
	private Double[][] docSimilarity(List<String> sstr) {
		int setSize = sstr.size();
		int DEFAULT_RESULT_SET = 10; //result count of exact search should be 1
		
		Double[][] matrix = new Double[setSize][setSize];
		List<RealVector> tlist = new ArrayList<RealVector>();
		final Set<String> terms = new HashSet<>();  // dictionary
		
		try{
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
			// get vector for each feed sensor pair
			for (int i = 0; i < setSize; i++) {
				BooleanQuery q = new BooleanQuery.Builder()
						.add(new TermQuery(new Term("feedid", sstr.get(i))), BooleanClause.Occur.SHOULD)
						.add(new TermQuery(new Term("sensorid", sstr.get(i))), BooleanClause.Occur.SHOULD)
						.build();
				TopDocs hits = searcher.search(q, DEFAULT_RESULT_SET);
				int docid = hits.scoreDocs[0].doc;
				Terms termVector = searcher.getIndexReader().getTermVector(docid, "content");
				
				// get frequencies
				TermsEnum termsEnum = termVector.iterator();
				Map<String, Integer> frequencies = new HashMap<>();
		        BytesRef text = null;
		        while ((text = termsEnum.next()) != null) {
		            String term = text.utf8ToString();
		            int freq = (int) termsEnum.totalTermFreq();
		            frequencies.put(term, freq);
		            terms.add(term);
		        }
		        
		        // change to real vector
		        RealVector vector = new ArrayRealVector(terms.size());
		        int cnt = 0;
		        for (String term : terms) {
		            int value = frequencies.containsKey(term) ? frequencies.get(term) : 0;
		            vector.setEntry(cnt++, value);
		        }
		        tlist.add((RealVector) vector.mapDivide(vector.getL1Norm()));
			}
			
			// use term vector to compute similarity
			for(int i=0; i<setSize; i++){
				for (int j = 0; j < setSize; j++) {
					if(i==j) continue;
					RealVector v0 = tlist.get(i);
					RealVector v1 = tlist.get(j);
					matrix[i][j] = (v0.dotProduct(v1)) / (v0.getNorm() * v1.getNorm());
				}
			}
		}catch(IOException ioe){
			logger.error("Similar search error!");
		}
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

	/**
	 * A pair for sensor similarity and sensor id
	 * @author li
	 */
	class SensorPair implements Comparable<SensorPair>{
		
		public Double similarity;
		public String feedstream;
		
		public SensorPair(Double similarity, String feedstream){
			this.similarity = similarity;
			this.feedstream = feedstream;
		}

		@Override
		public int compareTo(SensorPair sp) {
			return Double.compare(this.similarity, sp.similarity);
		}
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

