package ac.ictwsn.sensorfinder.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import ac.ictwsn.sensorfinder.web.model.xively.XivelyRequest;
import ac.ictwsn.sensorfinder.web.model.xively.XivelyResponse;

public class XivelyUtils {
	
	public static Date parseTime(String rawTime){
		if(rawTime == null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date ret = null; 
		try {
			ret = formatter.parse(rawTime);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return ret;
	}
	
	public static String concatStringWithComma(String[]strArr){
		if(strArr == null || strArr.length == 0)return "";
		StringBuffer buffer = new StringBuffer(strArr[0]);
		for(int i=1;i< strArr.length; i++)
			buffer.append(","+strArr[i]);
		return buffer.toString();
	}

	/**
	 * Send Xively server an GET request immediately.
	 * All of the request is in GET format. 
	 * 
	 * @param request
	 * 		Request body, contains the connection build method. 
	 * @param responseType
	 * 		Used to define the type of returned class, which extends XivelyResponse.
	 * @return
	 * 		A XivelyResponse type
	 */
	public static XivelyResponse sendRequestToXively(
			XivelyRequest request,
			Class<?> responseType) throws IOException{
		HttpURLConnection con;
		StringBuffer responseBuffer;
		String inputLine;
		XivelyResponse response = null;
		con = request.buildConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		responseBuffer = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
			responseBuffer.append(inputLine);
		in.close();
		ObjectMapper mapper = new ObjectMapper();
		response = (XivelyResponse) mapper.readValue(
				responseBuffer.toString(), responseType);
		con.disconnect();
		return response;
	}

}
