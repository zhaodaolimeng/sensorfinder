package ac.ictwsn.core.util;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.standard.StandardFilter;

public class ChainedAnalyser extends Analyzer{


	/**
	 * 这个解析器应至少支持英文的解析
	 * 另外，考虑支持中文和日文。
	 * 现有的Analyzer中，smartcn可正常解析中文和英文，但日文显示不对
	 * CJKAnalyzer对于东亚文字，强制将近邻的两位作为一个词进行切分，同时包括stopword，虽然有冗余，但现在看是最佳方案。
	 * 
	 * 这里想尝试写一个能进行拼接的Analyzer
	 */
	@Override
    protected TokenStreamComponents createComponents(String s) {
		
        final Tokenizer standardTokenizer = new MyStandardTokenizer();
        TokenStream tok = new StandardFilter(standardTokenizer);
        tok = new LowerCaseFilter(tok);
        
        return new TokenStreamComponents(standardTokenizer, tok);
    }

    private class MyStandardTokenizer extends Tokenizer {

        protected MyStandardTokenizer() {
            super( );
        }

        public boolean incrementToken() throws IOException {
            //add your logic
            return false;
        }
    }

}
