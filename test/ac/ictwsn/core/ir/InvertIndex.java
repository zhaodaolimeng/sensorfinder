package ac.ictwsn.core.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Same class can be found at cc.mallet.types.InvertedIndex, 
 * but we need to save the index to files. 
 * 
 * @author limeng
 *
 */
public class InvertIndex {
	
	final String targetFileName = "";
	final Integer rankLength = 10;
	
	List<HashMap<String, Integer>> docList; // bow for each document 
	HashMap<String, HashSet<Integer>> invertTerm; // document set for each word
	HashMap<String, Integer> strCnt;
	
	public InvertIndex(){
		this.docList = new ArrayList<HashMap<String, Integer>>();
		this.invertTerm = new HashMap<String, HashSet<Integer>>(); 
		this.strCnt = new HashMap<String, Integer>();
	}
	
	/**
	 * Each line contains words from one document
	 */
	public void build(File srcFileName) {
		
		try {
			FileInputStream fis = new FileInputStream(srcFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			// build dictionary for all file
			String line;
			while((line = br.readLine()) != null){
				HashMap<String, Integer> dict = new HashMap<String, Integer>();
				for(String w : line.split(" ")){
					if(dict.containsKey(dict))
						dict.put(w, dict.get(w) + 1);
					else
						dict.put(w, 1);
					
					if(strCnt.containsKey(w))
						strCnt.put(w, strCnt.get(w) + 1);
					else
						strCnt.put(w, 1);
					
					HashSet<Integer> docSet = this.invertTerm.get(w);
					docSet.add(docList.size());
					this.invertTerm.put(w, docSet);
				}
				docList.add(dict);
			}
			br.close();
			fis.close();
			
			// save dictionary to file, with jackson
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(new File(targetFileName), this.docList);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void reloadFromFile(File file){
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.docList = mapper.readValue(file, this.docList.getClass());
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * rank a simple query, return rank score for a particular document
	 * 
	 */
	public double computRankScore(Integer docIdx, String[] query){
		
		int totalTerms = 0;
		int totalDocs = 0;
		double totalTfidf = 0;
		
		HashMap<String, Integer> docMap = this.docList.get(docIdx);
		for(Entry<String, Integer> entry : docMap.entrySet())
			totalTerms += entry.getValue();
		for(Entry<String, HashSet<Integer>> entry : this.invertTerm.entrySet())
			totalDocs += entry.getValue().size();
		
		for(String q : query){
			// term frequency
			double tf = 1.0 * docMap.get(q)/totalTerms;
			
			// inverted document frequency
			// idf = log(doc_cnt/(doc_with_term+1))
			int docCnt = totalDocs;
			int docWithTerm = 0;
			if(this.invertTerm.containsKey(q))
				docWithTerm = invertTerm.get(q).size();
			double idf = Math.log(1.0 * docCnt / (docWithTerm + 1));
			totalTfidf += tf * idf;
		}
		return totalTfidf;
	}
	
	public static void main(String[] args) {
		
	}
	
	
}
