package ac.ictwsn.core.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class GzipHelper {
	
	public static void decompressGzip(String inputFile, String outputFile) 
			throws FileNotFoundException, IOException{
		byte[] buf = new byte[1024];
		GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(inputFile));
	    FileOutputStream out = new FileOutputStream(outputFile);
	    int len = 0;
	    while((len = gzis.read(buf)) > 0)
	    	out.write(buf, 0, len);
	    gzis.close();
	    out.close();
	}

}
