package ac.ictwsn.core.ml;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


/**
 * 
 * 随机森林（RF, Random forest）分类器
 * 
 * RF基于bagging方法，bagging方法每次从分类器中采样出一个子样本集，训练得到一个子分类器.
 * 通过一定方式组合这些分类器，得到一个性能更优的分类器
 * 
 * RF方法在bagging的基础上，在每次选择特征时，从特征集合中抽取一个大小为k的子集作为最优（而不是使用所有的d个特征）
 * k大小一般设定为k=log2(d)
 * 
 * @author limeng
 *
 */
public class RandomForest {
	
	private int numOfTrees; // 总的树数目
	private int maxDepth; // 最大树深度
	private int randomSeed;
	
	private RandomTree[] forest; // 决策树
	
	public RandomForest(int numOfTrees, int maxDepth){
		this.numOfTrees = numOfTrees;
		this.maxDepth = maxDepth;
		this.randomSeed = 0;
	}
	
	/**
	 * 
	 * 训练方法，使用bootstrapping生成不同的训练集；使用每个训练集获得一个决策树
	 * 
	 * @param X 每一行为一维向量，作为特征
	 * @param y 类型编号
	 */
	public void train(List<Double[]> X, List<String> y){
		
		System.out.println("Size of training set = " + X.size());
		this.forest = new RandomTree[this.numOfTrees];
		Random random = new Random(this.randomSeed);
		
		for(int i=0; i<this.numOfTrees; i++){
			// bootstrap
			List<Double[]> XSamples = new ArrayList<Double[]>();
			List<String> ySamples = new ArrayList<String>();
			for(int j=0; j<X.size(); j++){
				int t = random.nextInt(X.size());
				XSamples.add(X.get(t));
				ySamples.add(y.get(t));
			}
			
			// build decision tree
			int candidateAttributeSize = (int)(Math.log(X.get(0).length) / Math.log(2));
			forest[i] = new RandomTree(XSamples, ySamples, this.maxDepth, candidateAttributeSize);
		}
	}

	/**
	 * 
	 * 预测方法，使用每个决策树进行预测，之后对预测结果进行打分
	 * 
	 * @param x 一维向量
	 * @return 输出类型编号
	 */
	public List<String> predict(List<Double[]> X){
		
		System.out.println("Size of testing set = " + X.size());
		
		List<String> resultList = new ArrayList<String>();
		HashMap<String, Integer> resultCnt = new HashMap<String, Integer>();
		int maxCnt = 0;
		String maxLabel = "";
		
		for(Double[] x : X){
			for(int i=0; i<this.numOfTrees; i++){
				String label = forest[i].predict(x);
				if(!resultCnt.containsKey(label))
					resultCnt.put(label, 0);
				resultCnt.put(label, resultCnt.get(label) + 1);
				if(resultCnt.get(label) > maxCnt) {
					maxCnt = resultCnt.get(label);
					maxLabel = label;
				}
			}
			resultList.add(maxLabel);
		}
		return resultList;
	}
	
	public static void main(String[] args) throws IOException {
		
		/**
		 * Load iris dataset, Sample data:
		 * 5.1,3.5,1.4,0.2,Iris-setosa
		 */
		List<Double[]> featureList = new ArrayList<Double[]>();
		List<String> labelList = new ArrayList<String>();
		
		System.out.println("Loading iris dataset for test ...");
		URL url = new URL("https://archive.ics.uci.edu/ml/machine-learning-databases/iris/bezdekIris.data");
		Scanner scanner = new Scanner(url.openStream());
		String item = "";
		while(scanner.hasNext()){
			item = scanner.nextLine();
			String [] mixed = item.split(",");
			Double [] feature = new Double[mixed.length-1];
			for(int i=0;i<mixed.length - 1; i++)
				feature[i] = Double.parseDouble(mixed[i]);
			featureList.add(feature);
			labelList.add(mixed[mixed.length-1]);
		}
		scanner.close();
		System.out.println("Load done!");
		
		// cross validation
		List<Double[]> train_X = new ArrayList<>();
		List<Double[]> test_X = new ArrayList<>();
		List<String> train_y = new ArrayList<>();
		List<String> test_y = new ArrayList<>();
				
		for(int i=0; i<featureList.size(); i++){
			if(i % 10 == 0){
				//sample
				test_X.add(featureList.get(i));
				test_y.add(labelList.get(i));
			}else{
				train_X.add(featureList.get(i));
				train_y.add(labelList.get(i));
			}
		}

		RandomForest rf = new RandomForest(10, 5);
		rf.train(train_X, train_y);
		System.out.println("Training done!");
		List<String> pred = rf.predict(test_X);
		
		//check predict result
		int hit = 0;
		for(int i=0; i< pred.size(); i++){
			System.out.println(pred.get(i) + "\t" + test_y.get(i));
			if(pred.get(i).equals(test_y.get(i))) {
				hit++;
			}
		}
		System.out.println("Accuracy = " + 1.0 * hit / pred.size());
	}

}
