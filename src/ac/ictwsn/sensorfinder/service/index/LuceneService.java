package ac.ictwsn.sensorfinder.service.index;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

}
