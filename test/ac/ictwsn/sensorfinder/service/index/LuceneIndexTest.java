package ac.ictwsn.sensorfinder.service.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ac.ictwsn.sensorfinder.entities.Feed;
import ac.ictwsn.sensorfinder.repositories.FeedRepository;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes={AppConfig.class})
public class LuceneIndexTest {
	
	@Autowired
	FeedRepository feedRepo;
	
	/**
	 * 从数据库中读取每条记录
	 * 建立索引
	 */
	@Test
	public void buildIndexTest(){

		IndexWriter indexWriter;
		String[] strlist = {"desc", "tags", "title"};
		List<String> options = (List<String>) Arrays.asList(strlist);
		Directory directory;
		
		try {
			directory = FSDirectory.open(Paths.get("index"));
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new CJKAnalyzer());
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			
			System.out.println("Begin to fetch from databases");
			ArrayList<Feed>feedList = (ArrayList<Feed>) feedRepo.findAll();
			
			for(Feed feed : feedList){
				System.out.println("End Writing feed = " + feed.getId());
				
				// 从数据库中读取对应的位 
				String content = "";
				for(String str : options){
					if(str.equals("desc"))
						content += feed.getDescription();
					if(str.equals("tags"))
						content += feed.getTags();
					if(str.equals("title"))
						content += feed.getTitle();
				}

				// 合并位，对合并的结果建立索引
				Document doc = new Document();
//				Field idField = new LongPoint("feedid", feed.getId());
				
				Field idField = new StoredField("id", feed.getId());
				Field contentField = new TextField("content", content, Field.Store.YES);
				doc.add(idField);
				doc.add(contentField);
				indexWriter.addDocument(doc);
				
				System.out.println("End Writing feed = " + feed.getId());
			}
			indexWriter.commit();
			
			System.out.println("Commited");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
