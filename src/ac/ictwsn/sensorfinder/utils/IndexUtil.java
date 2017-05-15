package ac.ictwsn.sensorfinder.utils;

import java.util.List;

import ac.ictwsn.sensorfinder.dto.SensorDocument;

public class IndexUtil {
	
	public static SensorDocument[] normalization(List<SensorDocument> list){
		SensorDocument[] sparr = new SensorDocument[list.size()];
		Double sum = 0.0;
		for(SensorDocument sp : list)
			sum += sp.getScore();
		for(int i=0;i<sparr.length;i++)
			sparr[i]=new SensorDocument(
					list.get(i).getFeedSensorStr(), 
					list.get(i).getScore()/sum);
		return sparr;
	}
	
	public static Double[] normalization(Double[] input){
		int K = input.length;
		double sum = 0.0;
		for(int i=0;i<K;i++) {
			if(input[i] == null) input[i] = 0.0;
			sum += input[i];
		}
		for(int i=0;i<K;i++) input[i] /= sum;
		return input;
	}
	
	public static int getLinkWeight(Double[] l0, Double[] l1){
		int K = l0.length;
		double distance = 0.0;
		for(int k = 0; k < K; k++){
			if(l0[k] == null) l0[k] = 0.0;
			if(l1[k] == null) l1[k] = 0.0;
			distance += (l0[k] - l1[k]) * (l0[k] - l1[k]);
		}
			 
		return (int)(10 / (1 + distance));
	}

}
