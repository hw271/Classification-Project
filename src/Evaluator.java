import java.util.ArrayList;


public class Evaluator {

	/**
	 * @param args
	 */
	public Evaluator(){
		
	}
	
	public Performance kFoldsCross(DataSet trainSet, Classifier c1) throws InterruptedException{
		return kFoldsCross(trainSet, c1 ,10);
	}
	
	public Performance kFoldsCross(DataSet trainSet, Classifier c1, int k) throws InterruptedException{
		ArrayList<Performance> arrPer=new ArrayList<Performance>();
		ArrayList<DataSet> datasets=partition1(trainSet,k);
		for(int i=0;i<k;i++){
			Performance p=new Performance();
			
			//get train and test
			DataSet test=datasets.get(i);
			DataSet train=new DataSet(trainSet.name, trainSet.attributes);
			for(int j=0;j<k;j++){
				if(i!=j){
					train.add(datasets.get(j));
				}
			}
			//System.out.println("# of train examples="+train.examples.size()+", # of test examples="+test.examples.size());
			//p=holdOut(train, test, c1);
			
			//get all performances
			p=holdOut(train,test,c1);
			//System.out.println(p);
			arrPer.add(p);
		}
		
		Performance averagePerformance=new Performance();
		averagePerformance.accuracy=0;
		double max=0;
		double min=1;
		double variance=0;
		//get meanAccuracy, maxAccuracy, minAccuracy
		for(int i=0;i<k;i++){
			averagePerformance.accuracy+=arrPer.get(i).accuracy;
			if(max<arrPer.get(i).accuracy){
				max=arrPer.get(i).accuracy;
			}
			if(min>arrPer.get(i).accuracy){
				min=arrPer.get(i).accuracy;
			}
		}
		averagePerformance.accuracy/=k;
		
		//get dispersion
		if(max-averagePerformance.accuracy>averagePerformance.accuracy-min){
			averagePerformance.dispersion=max-averagePerformance.accuracy;
		}
		else{
			averagePerformance.dispersion=averagePerformance.accuracy-min;
		}
		
		//get variance
		for(int i=0;i<k;i++){
			variance+=Math.pow((arrPer.get(i).accuracy-averagePerformance.accuracy), 2);
		}
		variance/=k;
		averagePerformance.variance=variance;
		
		//get confidenceInterval
		double standardDeviation=Math.sqrt(variance);
		double z= 1.96;
		averagePerformance.confidenceInterval=z*standardDeviation/Math.sqrt(k);
		
		return averagePerformance;
	}
	
	public Performance holdOut(DataSet train, DataSet test, Classifier c1) throws InterruptedException{
		if(c1 instanceof IBk){
			IBk i1=(IBk) c1;
			i1.train(train);
			return i1.classify(test);
		}
		else if(c1 instanceof NaiveBayes){
			NaiveBayes nb1=(NaiveBayes) c1;
			nb1.train(train);
			return nb1.classify(test);
		}
		else if(c1 instanceof DecisionTree){
			DecisionTree dt1=(DecisionTree) c1;
			dt1.train(train);
			return dt1.classify(test);
		}
		else if(c1 instanceof Perceptron){
			Perceptron p=(Perceptron) c1;
			p.train(train);
			return p.classify(test);
		}
		
		else if(c1 instanceof KernalPerceptron){
			KernalPerceptron kp=(KernalPerceptron) c1;
			kp.train(train);
			return kp.classify(test);
		}
		
		else{
			System.out.println("wrong classifier.");
			return null;
		}
		
	}
	
	public ArrayList<DataSet> partition1(DataSet trainSet, int k){
		ArrayList<DataSet> partitionDataSets=new ArrayList<DataSet>();
		//RandomExample is a class which has an random number and an example
		//MyArrayList is a class extends ArrayList<RandomExample> and add a method: sort() which will sort
		//all randomExamples by its attribute random in ascending order
		
		for(int i=0;i<k;i++){
			DataSet tempDataSet=new DataSet(trainSet.name, trainSet.attributes);
			partitionDataSets.add(tempDataSet);
		}
		Examples exs=trainSet.examples;
		for(Example ex: exs){
			int index=(int) (Math.random()*k);
			DataSet temp=partitionDataSets.get(index);
			temp.examples.add(ex);
			partitionDataSets.set(index, temp);
		}
		/*
		for(int i=0;i<partitionDataSets.size();i++){
			System.out.println(partitionDataSets.get(i).examples.size());
		}
		*/
		return partitionDataSets;
	}
	
	/*	partition the dataset into k subsets with equal size
	public ArrayList<DataSet> partition(DataSet trainSet, int k){
		ArrayList<DataSet> partitionDataSets=new ArrayList<DataSet>();
		//RandomExample is a class which has an random number and an example
		//MyArrayList is a class extends ArrayList<RandomExample> and add a method: sort() which will sort
		//all randomExamples by its attribute random in ascending order
		
		//transform examples to newExamples
		MyArrayList newExamples= new MyArrayList(); 
		Examples exs=trainSet.examples;
		for(Example ex: exs){
			RandomExample re=new RandomExample(ex);
			newExamples.add(re);
		}
		//sort newExamples
		newExamples.sort();
		//System.out.println(newExamples.toString());
		
		for(int i=0;i<k;i++){
			DataSet tempDataSet=new DataSet(trainSet.name, trainSet.attributes);
			int numOfExsInSubset=trainSet.examples.size()/k;
			for(int j=0;j<numOfExsInSubset;j++){
				Example e1=newExamples.get(i*numOfExsInSubset+j).example;
				tempDataSet.examples.add(e1);
			}
			partitionDataSets.add(tempDataSet);
			//System.out.println("# of examples="+tempDataSet.examples.size());
		}
		return partitionDataSets;
	}
	 */
	
	public static void main(String[] args) throws Exception {
		
		
		// TODO Auto-generated method stub
		TrainTestSets t1=new TrainTestSets(args);
		Evaluator e1=new Evaluator();
		//NaiveBayes c1=new NaiveBayes();
		//e1.kFoldsCross(t1.train, c1);
		e1.partition1(t1.train, 5);

	}


}
