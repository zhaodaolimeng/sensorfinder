package ac.ictwsn.sensorfinder.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ac.ictwsn.sensorfinder.entities.Feature;
import ac.ictwsn.sensorfinder.repositories.FeatureRepository;
import cc.mallet.topics.DMRTopicModel;
import cc.mallet.topics.tui.DMRLoader;
import cc.mallet.types.InstanceList;

/**
 * 
 * 该任务顺序地将datastream_t中的设备读出之后，执行文本清理操作
 * 生成Mallet中DMR方法的输入文件feature.txt和text.txt
 * 执行DMR方法，生成主题索引文件
 *  
 * @author limeng
 *
 */
public class MalletIndexingTask implements Runnable{
	
	private static final Logger logger = Logger.getLogger(MalletIndexingTask.class);
	private final int numTopics = 20;
	private final int FREQUENCE_LIMIT = 2;
	private final int WORD_LENGTH_LIMIT = 2;
	private final int DOC_WORD_NUM_LIMIT = 2;
	
	private String indexPath; // temporary directory 
	private TaskState state;
	
	private final String FEATURE_FILE = "feature.txt";
	private final String TEXT_FILE = "text.txt";
	private final String ID_FILE = "id.txt";
	private final String DICT_FILE = "dictionary.txt";
	private final String DUMP_FILE = "dump.txt"; // save additional output
	private final String TOPIC_WORDS = "topic-words.txt"; // top words in a topics, used for debug
	
	private final String MALLET_INSTANCE = "instance.mallet";
	private final String DMR_PARAMETER = "dmr.parameters";
	private final String DMR_STATE = "dmr.state.gz";
	private final String DMR_THETA = "dmr-topics.txt";
	
	private FeatureRepository featureRepo;
	
	public MalletIndexingTask(String indexPath, FeatureRepository featureRepo){
		this.indexPath = indexPath;
		this.featureRepo = featureRepo;
		this.state = TaskState.PROCESSING;
	}
	
	public TaskState getState(){
		return this.state;
	}

	@Override
	public void run() {
		try {
			logger.info("Preprocessing...");
			prepareInputFile();
			logger.info("Training model...");
			trainingMallet();
			logger.info("Mallet Indexing Done!");
			this.state = TaskState.DONE;
		} catch (IOException e) {
			e.printStackTrace();
			this.state = TaskState.FAILING;
		}
	}
	
	private void prepareInputFile() throws FileNotFoundException {
		
		File df = new File(indexPath);
		if(!df.exists() || !df.isDirectory()) df.mkdir();
		
		// Extract documents from database
		List<Feature> sensorList = featureRepo.findAll();
		List<String> idlist = new ArrayList<String>();
		List<String> featurelist = new ArrayList<String>();
		List<String> doclist = new ArrayList<String>();

		// not a good idea to directly print result
		int locCnt = 0; 
		Date startDate = featureRepo.findMinCreated();
		PrintWriter pw = new PrintWriter(indexPath + "/" + this.DUMP_FILE);
		pw.println("mallet.start_time=" + startDate.getTime());
		pw.println("mallet.topic_num=" + this.numTopics);
		pw.close();
		
		HashMap<String, Integer> locationType = new HashMap<String, Integer>();
		for(Feature s : sensorList){
			// Remove url and stopwords to build text input
			String snapshot = s.getDoc()
					.replaceAll("\n", " ")
					.replaceAll("^(https?|ftp)://.*$", " ")
					.replaceAll("^[0-9]+$", " ")
					.replaceAll("[^a-zA-Z]", " ")
					.toLowerCase();
			
			// lat, lng and created time to feature
			// pay attention to value overflow
			String locKey = s.getLocationType();
			if(!locationType.containsKey(locKey))
				locationType.put(locKey, locCnt++);
			Long timeInterval = s.getCreated().getTime() - startDate.getTime();
			
			String featureStr = "loc_type=" + locationType.get(locKey) + ", lbtime=" + (timeInterval/1000/(3600*24*30*6));
			idlist.add(s.getFeedid() + "," + s.getStreamid());
			doclist.add(snapshot);
			featurelist.add(featureStr);
		}
		
		// store in file
		PrintWriter ipw = new PrintWriter(indexPath + '/' + this.ID_FILE);
		PrintWriter fpw = new PrintWriter(indexPath + '/' + this.FEATURE_FILE);
		PrintWriter tpw = new PrintWriter(indexPath + '/' + this.TEXT_FILE);
		
		// create dictionary
		HashMap<String, Integer> wordCnt = new HashMap<String, Integer>();
		for(int i = 0; i<idlist.size(); i++){
			for(String s : doclist.get(i).split(" ")){
				if(s.length() < WORD_LENGTH_LIMIT) continue;
				if(!wordCnt.containsKey(s))
					wordCnt.put(s, 0);
				wordCnt.put(s, 1 + wordCnt.get(s));
			}
		}
		
		// save dictionary
		PrintWriter dictpw = new PrintWriter(indexPath + '/' + this.DICT_FILE);
		for(Entry<String, Integer> entry : wordCnt.entrySet())
			if(entry.getValue() >= this.FREQUENCE_LIMIT)
				dictpw.write(entry.getKey());
		dictpw.close();
		
		// save corpus
		for(int i = 0; i<idlist.size(); i++){
			String rstr = "";
			for(String s : doclist.get(i).split(" ")){
				if((!wordCnt.containsKey(s)) || wordCnt.get(s) < FREQUENCE_LIMIT) continue;
				rstr += (s + " "); 
			}
			if(rstr.split(" ").length < DOC_WORD_NUM_LIMIT) continue;
			ipw.println(idlist.get(i));
			fpw.println(featurelist.get(i));
			tpw.println(rstr);
		}
		tpw.close();
		fpw.close();
		ipw.close();
	}
	
	/**
	 * Call DMR method to generate the following files in indexing directory 
	 * 		dmr.parameter		Feature weight generated by dmr method 
	 * 		dmr.state.gz		Topic Allocation for each words in documents
	 * 		dmr-topics.txt		Topic Allocation for each documents
	 * 
	 * @throws IOException		
	 */
	private void trainingMallet() throws FileNotFoundException, IOException{
		// BFGS-L
		File wordsFile = new File(indexPath + '/' + this.TEXT_FILE);
		File featuresFile = new File(indexPath + '/' + this.FEATURE_FILE);
		File instancesFile = new File(indexPath + '/' + this.MALLET_INSTANCE);
		DMRLoader loader = new DMRLoader();
		loader.load(wordsFile, featuresFile, instancesFile);
		
		InstanceList training = InstanceList.load(new File(indexPath + '/' + this.MALLET_INSTANCE));
		DMRTopicModel lda = new DMRTopicModel(numTopics);
		lda.setOptimizeInterval(100);
		lda.setTopicDisplay(100, 10);
		lda.setRandomSeed(10000);
		lda.addInstances(training);
		lda.estimate();

		// All the parameters should be read from files
		lda.writeParameters(new File(indexPath + "/" + this.DMR_PARAMETER));
		lda.printState(new File(indexPath + "/" + this.DMR_STATE));
		lda.printDocumentTopics(new File(indexPath + "/" + this.DMR_THETA));  // document topics vector
		lda.printTopWords(new File(indexPath + "/" + this.TOPIC_WORDS), 10, true); // print top 10 words for each topic
	}
	

}
