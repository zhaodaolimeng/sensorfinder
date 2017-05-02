package ac.ictwsn.sensorfinder.service.index;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class LuceneSearchTest {
	
	/**
	 * 根据index目录下的索引，进行搜索测试
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@Test
	public void searchTest1() throws IOException, ParseException{
		
		Directory directory = FSDirectory.open(Paths.get("index"));
		DirectoryReader currentReader = DirectoryReader.open(directory); 
		
		IndexSearcher searcher = new IndexSearcher(currentReader);
		QueryParser queryParse = new QueryParser("content", new CJKAnalyzer());
		
		String queryStr = "radiation";
		Query query = queryParse.parse(queryStr);
		TopDocs docResult = searcher.search(query, 10);
		
		System.out.println("================");
		System.out.println("Total hits = " + docResult.totalHits);
		for(ScoreDoc sd : docResult.scoreDocs) {
			System.out.println(searcher.doc(sd.doc));
		}
		
	}
	
	@Test
	public void searchTest2() throws Exception{
		
		String q = "test radiation";
		
		Directory dir=FSDirectory.open(Paths.get("index"));
		IndexReader reader=DirectoryReader.open(dir);
		IndexSearcher is=new IndexSearcher(reader);
		
		// Analyzer analyzer=new StandardAnalyzer(); // 标准分词器
		CJKAnalyzer analyzer=new CJKAnalyzer();
		QueryParser parser=new QueryParser("content", analyzer);
		Query query=parser.parse(q);
		
		long start=System.currentTimeMillis();
		TopDocs hits=is.search(query, 10);
		long end=System.currentTimeMillis();
		
		System.out.println("匹配 "+q+" ，总共花费"+(end-start)+"毫秒"+"查询到"+hits.totalHits+"个记录");
		
		QueryScorer scorer=new QueryScorer(query);
		Fragmenter fragmenter=new SimpleSpanFragmenter(scorer);
		SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("","");
		Highlighter highlighter=new Highlighter(simpleHTMLFormatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		for(ScoreDoc scoreDoc:hits.scoreDocs){
			Document doc=is.doc(scoreDoc.doc);
			
			List<IndexableField> fields = doc.getFields();
			
			for(IndexableField f : fields)
				System.out.println(f);
			
			System.out.println(doc.get("feedid"));
			System.out.println(doc.get("content"));
			
			String desc=doc.get("content");
			if(desc!=null){
				TokenStream tokenStream=analyzer.tokenStream("content", new StringReader(desc));
				System.out.println(highlighter.getBestFragment(tokenStream, desc));
			}
		}
		reader.close();
	}
	

}
