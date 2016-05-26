import java.util.ArrayList;
import java.util.PriorityQueue;


public class test2 implements Classifier{
/*
 * [l3-class IBk classifier]
		
			DataSet trainSet;
			void train(DataSet){
				
			}
			int classify(Example){
				distribution[]=getDistribution(Example);
				choose the class with majority vote;
				return classLable;
			}
			
			performance classify(TestSet test){
				for each Example:
					classIndex = classify(Example);
					if(classIndex!=trueClassIndex){
						#misClass+=1;
					}
					performance.accuracy=1-#misClass/# of examples;
					return performance;
			}
			Double[] getDistribution(observation o){
				o=scaler.scale(o);
				neighbor heap=findNeighbors(o,k);
				distribution over classlabel: initial = 1/(# of examples)
				for each neighbor
					distribution[neighbor class index]+=1.0;
				return normalized(distribution);
			}
			
			priorQueue findNeighbor(o,k){
				//对于kNN来说，get到离这个点最近的k个点
				建立一个大小为k的优先队列priorQueue
				for each example in database{
					if distance(example_i, observation)<bigggestElem in priorQueue
						priorQueue.push(example_i);
						priorQueue.pop(biggestElem)
				}
				最后返回这个大小为k的队列
			}
 * */
	int k;
	DataSet train;
	int numClass;
	int classLabelIndex;
	public test2(){
		this.k=3;
	}
	public test2(int k){
		this.k=k;
	}
	
	public void train(DataSet train){
		this.train=editedKNN(train);
		this.numClass=train.attributes.get(train.attributes.getClassIndex()).domain.size();
		this.classLabelIndex=train.attributes.getClassIndex();
		
		
	}

	public DataSet editedKNN(DataSet train){
		System.out.println("editedKNN(DataSet train) train.name=");
		this.train.name=train.name;
		this.train.attributes=train.attributes;
		System.out.println(train.examples.size());
		for(int i=0;i<train.examples.size();i++){
			Example newExample=train.examples.get(i);
			System.out.println(newExample);
			
			if(i<k){
				this.train.examples.add(newExample);
			}
			else{
				int classifyLabel=classify(newExample);
				int trueLable=(int)(double)newExample.get(classLabelIndex);
				
				if(classifyLabel!=trueLable){
					this.train.examples.add(newExample);
				}
			}
		}
		return this.train;
	}
	
	public int classify(Example example1){
		ArrayList<Double> vote;
		vote=getDistribution(example1);
		double max=0;
		int label=0;
		for(int i=0;i<this.numClass;i++){
			if(max<vote.get(i)){
				max=vote.get(i);
				label=i;
			}
		}
		//System.out.println(vote);
		return label;
	}

	public Performance classify(DataSet test){
		Examples testExamples=test.examples;
		Performance p1=new Performance();
		int misClass=0;
		for(Example ex:testExamples){
			int trueLabel=(int)(double)ex.get(test.attributes.getClassIndex());
			int label=this.classify(ex);
			if(trueLabel!=label){
				misClass++;
			}
		}
		p1.accuracy=1-(double)misClass/test.examples.size();
		return p1;
	}

	ArrayList<Double> getDistribution(Example example1){
		PriorityQueue<DistanceExample> p1=findNeighbor(example1);
		
		ArrayList<Integer> vote=new ArrayList<Integer>();
		ArrayList<Double> distribution=new ArrayList<Double>();//Double[this.numClass];
		
		for(int i=0;i<this.numClass;i++){
			vote.add(0);
			distribution.add(0.0);
		}

		while(!p1.isEmpty()){
			
			DistanceExample ex=p1.poll();
			
			int classIndex=(int)(double)ex.example.get(classLabelIndex);
			vote.set(classIndex, vote.get(classIndex)+1);
		}

		for(int i=0;i<this.numClass;i++){
			distribution.set(i, (double)vote.get(i)/this.k);
		}
		
		return distribution;
	}
	
	PriorityQueue<DistanceExample> findNeighbor(Example e1){
		
		MyComparator myComparator=new MyComparator();
		PriorityQueue<DistanceExample> priorityQueue=new PriorityQueue<DistanceExample>(k,myComparator);
		System.out.println("PriorityQueue<DistanceExample> findNeighbor(Example e1)");
		Examples exs=this.train.examples;
		//System.out.println("==============size="+k+" example="+e1);
		for(Example ex: exs){
			DistanceExample de=new DistanceExample();
			de.distance=this.getDistance(e1, ex);
			de.example=ex;
			

			if(priorityQueue.size()<k){
				priorityQueue.add(de);
			}
			else{
				DistanceExample farEx=priorityQueue.peek();
				double farDistance=farEx.distance;
				double distance=getDistance(ex,e1);
				if(farDistance>distance){
					//System.out.println("poll distance="+temp.distance+"add distance="+de.distance);
					priorityQueue.add(de);
				}
			}

		}

		return priorityQueue;
	}
	
	double getDistance(Example e1, Example e){
		double distance=0.0;
		for(int attrIndex=0;attrIndex<e1.size();attrIndex++){
			if(attrIndex!=train.attributes.classIndex){
				if(!this.train.attributes.get(attrIndex).is_numeric){
					if(!e1.get(attrIndex).equals(e.get(attrIndex))){
						distance+=1;
					}
				}
				else{
					System.out.println("numeric");
					Scaler sc1=new Scaler(this.train.attributes.get(attrIndex),this.train);
					double dis1=sc1.scale(e1.get(attrIndex));
					//System.out.println("dis1="+dis1);
					double dis2=sc1.scale(e.get(attrIndex));
					//System.out.println("dis2="+dis2);
					distance+=Math.pow((dis1-dis2),2);
				}
			}
		}
		return distance;
	}
	
	public static void main(String[] args) throws Exception{
		TrainTestSets t1=new TrainTestSets(args);
		System.out.println(t1.train);
		
		System.out.println("==============");
		test2 t2=new test2();

		t2.train(t1.train);
		System.out.println(t2.train);
		
		/*
		 *Evaluator e1=new Evaluator();
		 *if(args.contains("-k")){
		 *	IBk c1=new IBk(kNN);
		 *}
		 *else{
		 *	IBk c1=new IBk();
		 *
		 *}
		 *if(datasetArgs.contains("-T")){
			 *String[] datasetArgs=getArgs(args);
			 *TestTrainSets t1=new TrainTestSets(datasetArgs);
		 *	performance=e1.holdOut(t1.train, t1.test)
		 *}
		 *else{
		 	 *String[] datasetArgs=getArgs(args);
			 *TestTrainSets t1=new TrainTestSets(datasetArgs);
		 *	if(args.contains("-x"))
		 *		performance=e1.kFoldsCross(kFolds);
		 *	else
		 *		performance=e1.kFoldsCross();
		 *}
		 *System.out.println(performance)
		 * 
		 * */
		
		/*
		Evaluator e1=new Evaluator();
		ArrayList<String> arrArgs=new ArrayList<String>();
		Performance performance;
		for(String arg:args) arrArgs.add(arg);
		//System.out.println(arrArgs.toString());
		IBk c1;
		if(arrArgs.contains("-k")){
			int kNN = Integer.valueOf(arrArgs.get(arrArgs.indexOf("-k")+1));
			c1=new IBk();
		}
		else{
			c1=new IBk();
		}
		
		
		if(arrArgs.contains("-T")){
									String[] datasetArgs=new String[4];
									datasetArgs[0]="-t";
									datasetArgs[1]=arrArgs.get(arrArgs.indexOf("-t")+1);
									datasetArgs[2]="-T";
									datasetArgs[3]=arrArgs.get(arrArgs.indexOf("-T")+1);
			TrainTestSets t1=new TrainTestSets(datasetArgs);
			t1.train=c1.editedKNN(t1.train);
			
			performance=e1.holdOut(t1.train, t1.test, c1);
		}
		else{
									String[] datasetArgs=new String[2];
									datasetArgs[0]="-t";
									datasetArgs[1]=arrArgs.get(arrArgs.indexOf("-t")+1);
			TrainTestSets t1=new TrainTestSets(datasetArgs);
			t1.train=c1.editedKNN(t1.train);
			
			if(arrArgs.contains("-x")){
									int k=Integer.valueOf(arrArgs.get(arrArgs.indexOf("-x")+1));
				performance=e1.kFoldsCross(t1.train, c1, k);
			}
			else{
				performance=e1.kFoldsCross(t1.train, c1);
			}
		}
		if(!arrArgs.contains("-T")){
			System.out.println(performance);
		}
		else{
			double accuracy=((double)(int)(performance.accuracy*10000))/100;
			System.out.println("accuracy="+accuracy+"%");
		}
		*/
		
	}
	
}
