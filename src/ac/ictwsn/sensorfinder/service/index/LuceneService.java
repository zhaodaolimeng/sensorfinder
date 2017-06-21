package ac.ictwsn.sensorfinder.service.index;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.ictwsn.sensorfinder.dto.ResultDTO;
import ac.ictwsn.sensorfinder.dto.SensorDocument;
import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.entities.Sensor;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;
import ac.ictwsn.sensorfinder.task.LuceneIndexingTask;
import ac.ictwsn.sensorfinder.task.TaskState;
import ac.ictwsn.sensorfinder.utils.IndexUtil;

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
	public void startBuildLuceneIndex() {
		logger.info("Ready to index ... ");
		try {
			luceneIndexingTask = new LuceneIndexingTask(sensorRepo, indexPath);
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
	
		int RESULT_SIZE = 20;
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
				item.setFeedUrl(feed.getFeedUrl());
				
				if(feed.getLat() != null && !feed.getLat().equals(""))
					item.setLat(Double.parseDouble(feed.getLat()));
				if(feed.getLng() != null && !feed.getLng().equals(""))
					item.setLng(Double.parseDouble(feed.getLng()));
				
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
	
	public ResultDTO computeLuceneScore(String queryStr, int sensorNum){
		
		
		List<SensorDocument> doclist = new ArrayList<SensorDocument>();
		try {
			CJKAnalyzer analyzer = new CJKAnalyzer();
			QueryParser queryParse = new QueryParser("content", analyzer);
			Query query = queryParse.parse(queryStr);
			
			Directory luceneDirectory = FSDirectory.open(Paths.get(indexPath));
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(luceneDirectory));
			TopDocs hits=searcher.search(query, sensorNum);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs){
				Document doc = searcher.doc(scoreDoc.doc);
				Long tfeedid = Long.parseLong(doc.get("feedid"));
				String sensorid = doc.get("sensorid");
				SensorDocument sd = new SensorDocument(tfeedid, sensorid, 1.0*scoreDoc.score); 
				doclist.add(sd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ResultDTO result = new ResultDTO();
		result.setItemlist(Arrays.asList(IndexUtil.normalization(doclist)));
		return result;
	}
	
	
	/**
	 * 
	 * @param sstr sensor id string in format: feedid+","+streamid
	 * @return
	 */
	public Double[][] docSimilarity(List<String> sstr) {
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
	
	
	/**
	 * Only load necessary documents(with a count of topSensorNum)
	 * @param tsensor
	 * @param sensornum
	 * @return
	 */
	public SensorDocument[] rankDocument(Sensor tsensor, Integer sensornum){
		logger.info("Ranking document similarity for all sensors ...");
		List<SensorDocument> doclist = new ArrayList<SensorDocument>();
		try {
			Feed tfeed = tsensor.getFeed();
			String queryStr = tfeed.getDescription() + " "
					+ tfeed.getTags() + " " + tfeed.getTitle() + " "
					+ tsensor.getTags() + " " + tsensor.getStreamId();
			CJKAnalyzer analyzer = new CJKAnalyzer();
			QueryParser queryParse = new QueryParser("content", analyzer);		
			Query query = queryParse.parse(queryStr);
			
			Directory luceneDirectory = FSDirectory.open(Paths.get(indexPath));
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(luceneDirectory));
			TopDocs hits=searcher.search(query, sensornum);
			
			for(ScoreDoc scoreDoc : hits.scoreDocs){
				Document doc = searcher.doc(scoreDoc.doc);
				Long tfeedid = Long.parseLong(doc.get("feedid"));
				String sensorid = doc.get("sensorid");
				doclist.add(new SensorDocument(tfeedid, sensorid, 1.0*scoreDoc.score));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return IndexUtil.normalization(doclist);
	}

}
