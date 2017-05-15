package ac.ictwsn.sensorfinder.service;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ac.ictwsn.sensorfinder.config.AppConfig;
import ac.ictwsn.sensorfinder.entities.Feature;
import ac.ictwsn.sensorfinder.repositories.FeatureRepository;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
@WebAppConfiguration
public class SearchServiceTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	
	@Autowired
	private SearchService searchService;
	@Autowired
	private FeatureRepository featureRepo;
	@Autowired
	private FeedRepository feedRepo;
	
	
	@Test
	public void unitTest(){
		System.out.println("Hello");
	}
	
	/**
	 * 计算每次查询的时延
	 * 查询数据为从feature_t中tags关键位的词语
	 * @throws InvalidTokenOffsetsException 
	 * @throws IOException 
	 * 
	 */
	@Test
	public void searchTest() throws IOException, InvalidTokenOffsetsException{
		
		System.out.println("In search test...");
		
		try{
			String filePath = "D:\\Desktop\\time.txt";
		    PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		    
			int cnt = 0;
			for(Feature feature : featureRepo.findAll()){
				long startTime = System.currentTimeMillis();
				String tags = feedRepo.findById(feature.getFeedid()).getTags();
				
				if(tags.length() == 0) 
					continue;
				
				searchService.searchByLuceneAndTopic(tags);
				long endTime   = System.currentTimeMillis();
				writer.println(tags.length() + "\t" + (endTime - startTime));
				cnt++;
				
				if(cnt % 10 == 0)
					System.out.println(cnt);
			}
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
