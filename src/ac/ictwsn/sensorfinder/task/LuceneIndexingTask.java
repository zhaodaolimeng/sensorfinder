package ac.ictwsn.sensorfinder.task;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ac.ictwsn.sensorfinder.dto.SensorDocument;
import ac.ictwsn.sensorfinder.repositories.SensorRepository;

public class LuceneIndexingTask implements Runnable{
	
	private static final Logger logger = Logger.getLogger(LuceneIndexingTask.class);
	
	private IndexWriter indexWriter;
	private TaskState state;
	
	SensorRepository sensorRepo;
	
	ArrayList<String> options;
	
	private String indexPath;
	
	/**
	 * Create Indexing Instance 
	 * @param options
	 * @throws IOException 
	 */
	public LuceneIndexingTask(ArrayList<String> options, SensorRepository sensorRepo, String indexPath) 
			throws IOException{
		this.sensorRepo = sensorRepo; //autowire may cause problem
		this.options = options;
		this.indexPath = indexPath;
		this.state = TaskState.PROCESSING;
	}
	
	public TaskState getState(){
		return this.state;
	}

	/**
	 * To check how to store id as a index, please check the following link: 
	 * http://codepub.cn/2016/05/20/Lucene-6-0-in-action-2-All-kinds-of-Field-and-sort-operations/
	 */
	@Override
	public void run() {
		Directory directory;
		try {
			logger.info("Lucene index will be created at:" + indexPath);
			directory = FSDirectory.open(Paths.get(indexPath));
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new CJKAnalyzer());
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			
			List<SensorDocument> sensorList = sensorRepo.fastFindAll();
			logger.info("Query done. Start to build index.");
			
			for(SensorDocument sensor : sensorList){
				Document doc = new Document();
				Field feedidField = new StoredField("feedid", sensor.getFeedid());
				Field sensoridField = new StoredField("sensorid", sensor.getSensorid());
				Field contentField = new TextField("content", sensor.getSnapshotWithUpdate(), Field.Store.YES);
				doc.add(feedidField);
				doc.add(sensoridField);
				doc.add(contentField);
				indexWriter.addDocument(doc);
			}
			indexWriter.commit();
			logger.info("Complete lucene index!");
			this.state = TaskState.DONE;
		} catch (Exception e) {
			e.printStackTrace();
			this.state = TaskState.FAILING;
		}
	}
}
