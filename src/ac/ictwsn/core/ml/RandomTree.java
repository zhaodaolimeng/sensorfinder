package ac.ictwsn.core.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import ac.ictwsn.core.util.Pair;

class TreeNode {

	TreeNode left;
	TreeNode right;
	int attributeIndex; // 用于分裂的属性
	double splitValue; // 用于分裂的属性对应的值

	boolean isLeaf;
	String classLabel; // 分类标记
}

/**
 * 构建决策树
 * 
 * 1. 计算混杂程度，如果小于某阈值，则说明到达叶节点，选择当前元素中占比最高的类别作为该节点 
 * 2. 随机生成一个大小为candidateAttributeSize的候选属性集合，从候选集合中选择熵增益最高的一个特征作为判断特征 
 * 3. 根据该特征计算最佳切分位置
 * 
 * @author limeng
 *
 */
public class RandomTree {

	final double eps = 1e-6;

	private int randomSeed;
	private int maxLevel;
	private int candidateAttributeSize; //
	private Double[][] X;
	private String[] y;

	private TreeNode root;

	public RandomTree(List<Double[]> xSamples, List<String> ySamples, int maxLevel, int candidateAttributeSize) {
		this.randomSeed = 0;
		this.maxLevel = maxLevel;
		this.candidateAttributeSize = candidateAttributeSize;

		List<Integer> idList = new ArrayList<Integer>();
		this.X = new Double[xSamples.size()][xSamples.get(0).length];
		this.y = new String[ySamples.size()];

		for (int i = 0; i < xSamples.size(); i++) {
			idList.add(i);
			this.X[i] = xSamples.get(i);
			this.y[i] = ySamples.get(i);
		}
		this.root = buildTree(idList, 0);
	}

	public String predict(Double[] X) {
		TreeNode cur = root;
		while (!cur.isLeaf) {
			if (X[cur.attributeIndex] <= cur.splitValue)
				cur = cur.left;
			else
				cur = cur.right;
		}
		return cur.classLabel;
	}

	private TreeNode buildTree(List<Integer> idList, int currentLevel) {
		
		TreeNode node = new TreeNode();
		/**
		 * 判定当前树是否已经到达最大层，或当前节点的熵已经达到最大值
		 */
		Statistics stat = new Statistics(idList, X, y);
		stat.countMajorClass();
		int maxCnt = stat.maxCnt;
		int sumCnt = stat.sumCnt;
		String maxLabel = stat.maxLabel;

		if (currentLevel > this.maxLevel || Math.abs(1.0 * maxCnt / sumCnt) < eps) {
			node.isLeaf = true;
			node.classLabel = maxLabel;
			return node;
		}

		/**
		 * 找到最合适的属性
		 * 1. 随机采样k个特征 
		 * 2. 对于每个特征attr，计算其最大增益对应的划分 
		 * 2.1 根据attr的数值对数据进行排序 
		 * 2.2 对于attr对应的每个可划分的位置，计算划分之后的熵，熵最大的位置对应的切分位置 
		 * 3. 使用选定的切分属性和切分位置对数据集进行分割
		 */
		node.attributeIndex = 0;
		node.splitValue = 0.0;
		double minEntropy = Double.MAX_VALUE;

		List<Integer> attributeIndexList = new ArrayList<Integer>();
		Integer attributeSize = this.X[0].length;
		for (int i = 0; i < attributeSize; i++)
			attributeIndexList.add(i);
		Collections.shuffle(attributeIndexList, new Random(this.randomSeed));

		for (int i = 0; i < this.candidateAttributeSize; i++) {

			double tempMinEntropy = Double.MAX_VALUE;
			double tempSplit = 0.0;
			int attr = attributeIndexList.get(i);
			
			// 根据属性集对元素进行升序排序
			List<Integer> sortedIdList = stat.sortByAttribute(idList, attr);

			// 对每个排序位置计算熵增
			for (int j = 0; j < sortedIdList.size() - 1; j++) {
				double hd0 = stat.entropy(sortedIdList.subList(0, j));
				double hd1 = stat.entropy(sortedIdList.subList(j, sortedIdList.size()));
				double tempEntropy = hd0 * (j + 1) / sortedIdList.size() + 
						hd1 * (sortedIdList.size() - j) / sortedIdList.size();
				
				tempEntropy = -tempEntropy;
				
				if(tempEntropy < tempMinEntropy){
					tempMinEntropy = tempEntropy;
					tempSplit = (X[sortedIdList.get(j)][attr] + X[sortedIdList.get(j + 1)][attr]) / 2; 
				}
			}

			if (tempMinEntropy < minEntropy) {
				minEntropy = tempMinEntropy;
				node.splitValue = tempSplit;
				node.attributeIndex = attr;
			}
		}

		List<Integer> leftList = new ArrayList<Integer>();
		List<Integer> rightList = new ArrayList<Integer>();
		for (int i = 0; i < idList.size(); i++) {
			if (X[i][node.attributeIndex] <= node.splitValue)
				leftList.add(i);
			else
				rightList.add(i);
		}

		buildTree(rightList, currentLevel + 1);
		node.left = buildTree(leftList, currentLevel + 1);
		node.right = buildTree(rightList, currentLevel + 1);
		return node;
	}

}

class Statistics {
	private List<Integer> idList;
	private Double[][] X;
	private String[] y;

	int maxCnt = 0;
	int sumCnt = 0;
	String maxLabel = "";

	Statistics(List<Integer> idList, Double[][] X,  String[] y) {
		this.X = X;
		this.y = y;
		this.idList = idList;
	}

	public void countMajorClass() {
		
		this.sumCnt = 0;
		this.maxCnt = 0;

		HashMap<String, Integer> classCnt = new HashMap<String, Integer>();
		for (int i = 0; i < idList.size(); i++) {
			String L = y[idList.get(i)];
			if (!classCnt.containsKey(L))
				classCnt.put(L, 0);
			classCnt.put(L, classCnt.get(L) + 1);
			if (maxCnt < classCnt.get(L)) {
				maxCnt = classCnt.get(L);
				maxLabel = L;
			}
			sumCnt += classCnt.get(L);
		}
	}
	
	public List<Integer> sortByAttribute(List<Integer> idList, Integer attributeIndex){
		
		List<Pair<Double, Integer>> dataList = new ArrayList<Pair<Double, Integer>>();
		for (int j = 0; j < idList.size(); j++)
			dataList.add(new Pair<Double, Integer>(X[j][attributeIndex], idList.get(j)));
		
		Collections.sort(dataList, new Comparator<Pair<Double, Integer>>() {
			public int compare(Pair<Double, Integer> arg0, Pair<Double, Integer> arg1) {
				if(arg0.getFirst() > arg1.getFirst()) return 1;
				if(arg0.getFirst() < arg1.getFirst()) return -1;
				return 0;
			}
		});
		
		
		//FIXME
		for(int j=0; j<dataList.size(); j++)
			System.out.println(dataList.get(j).getFirst() + "\t" + y[dataList.get(j).getSecond()]);
		System.out.println("=============");
		
		idList = new ArrayList<Integer>();
		for(int i=0; i<dataList.size(); i++)
			idList.add(dataList.get(i).getSecond());
		return idList;
	}
	
	/**
	 * H(D) = sum{ p*log2(p) } H(D|A) = sum{p * H(D|A=x)}
	 * 
	 * @param idList
	 * @return
	 */
	public double entropy(List<Integer> idList) {
		double result = 0.0;
		HashMap<String, Integer> classCnt = getProbabilityOfClasses(idList);
		for(Entry<String, Integer> entry : classCnt.entrySet()){
			double p = 1.0 * entry.getValue() / idList.size(); 
			result += p * Math.log(p); 
		}
		return result;
	}
	
	
	private HashMap<String, Integer> getProbabilityOfClasses(List<Integer> idList){
		HashMap<String, Integer> classCnt = new HashMap<String, Integer>();
		for(int i=0; i<idList.size(); i++) {
			if(!classCnt.containsKey(y[idList.get(i)]))
				classCnt.put(y[idList.get(i)], 0);
			classCnt.put(y[idList.get(i)], classCnt.get(y[idList.get(i)]) + 1);
		}
		return classCnt;
	}

}
