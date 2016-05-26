import java.util.ArrayList;


public class NaiveBayes implements Classifier {
/*
 * 		[l3-class NaiveBayes classifier]
			double prior[];
			void train(DataSet){
				get prior and conditional probability				
			}
			int classify(Example){
				foreach attr_i in example:
					foreach class_j:
						p(example|class_j)*=Estimator.getProbability();	//???log-sum-exp trick
				prior[]=getDistribution(observation);
				posterior=prior(class_j)*p(examples|class_j);
				choose the class with bigger posterior;	
				return classIndex
				
			}
			performance classify(DataSet){
				for each example in DataSet(){
					classGet=classify(example);
					if(classGet!=classTrue){
						#misClass+=1;	
					}
				}
				performance.accuracy=1-#misClass/# of examples;
			}
			ArrayList<Double>(Double[]) getDistribution(observation o){
				return posterior;
			}
 * 
 */
	
	ArrayList<Double> prior;
	int labelClassIndex;
	Attribute labelClass;
	ArrayList<Estimator> attributeEstimator;
	
	public NaiveBayes(){
	}
	
	public void train(DataSet train){
		//get prior 获得先验概率
		this.prior=new ArrayList<Double>();
		this.labelClassIndex=train.attributes.classIndex;
		this.labelClass=train.attributes.get(labelClassIndex);
		
		//System.out.println(this.labelClass.domain);
		
		for(int i=0;i<this.labelClass.domain.size();i++){
			//initialize: give each prior(class) an initial value of 1/(# of examples)
			prior.add(0.0);
		}
		Examples es1=train.examples;
		for(Example e1:es1){
			int theClass=(int)(double)e1.get(labelClassIndex);
			prior.set(theClass, prior.get(theClass)+1.0);
		}
		
		int numOfExamples=train.examples.size();
		int numOfLabelClassesDomain=train.attributes.get(train.attributes.getClassIndex()).domain.size();
		for(int i=0;i<prior.size();i++){
			prior.set(i, (prior.get(i)+1.0)/(numOfExamples+numOfLabelClassesDomain));
		}
		//System.out.println(prior);
		/*another method:
		 * initial every prior with 1; 
		 * calculate the prior=(prior)/(sum all(prior));
		 * it will be esiear to think;
		 * */
		//get conditional probability 获得条件概率
		this.attributeEstimator=new ArrayList<Estimator>();
		
		for(int i=0;i<train.attributes.getSize();i++){
			if(i!=train.attributes.classIndex){
				if(!train.attributes.get(i).is_numeric){
					CategoricalEstimator e1=new CategoricalEstimator(train, train.attributes.get(i),train.attributes.get(train.attributes.getClassIndex()));
					attributeEstimator.add(e1);
				}
				else{
					NormalEstimator e1=new NormalEstimator(train, train.attributes.get(i), train.attributes.get(train.attributes.getClassIndex()));
					attributeEstimator.add(e1);
				}
			}
		}
		
	}
	
	public int classify(Example example1){
		//posterior，size=labelClass.size();
		ArrayList<Double> post=new ArrayList<Double>();
		post=getDistribution(example1);
		
		
		//use log-sum-exp to avoid underflow
		double max=0;
		int label=0;
		for(int i=0;i<post.size();i++){
			if(post.get(i)>max){
				max=post.get(i);
				label=i;
			}
		}
		return label;
	}

	Performance classify(DataSet test){
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
			
	public ArrayList<Double> getDistribution(Example example1){
		ArrayList<Double> post=new ArrayList<Double>();		
		for(int i=0;i<this.prior.size();i++){
			post.add(this.prior.get(i));
		}
		//System.out.println(prior);
		for(int i=0;i<attributeEstimator.size();i++){
			//i是example中每个attribute的index;j对应分类到classj，条件概率p(class j|attribute i)由estimator 给出
			//value就相当于决定了是attribute.domain中的哪一个
			if(i!=this.labelClassIndex){
				double value=example1.get(i);
				if(this.attributeEstimator.get(i) instanceof CategoricalEstimator){
					CategoricalEstimator ce1=(CategoricalEstimator) this.attributeEstimator.get(i);

					for(int j=0;j<post.size();j++){
						double prob=ce1.getProbability((int)value, j);
						//if(prob>1) System.out.println(prob+">1 catergoric");
						post.set(j, post.get(j)*prob);
						
					}
	
				}
				else{
					NormalEstimator ne1=(NormalEstimator) this.attributeEstimator.get(i);
					ArrayList<Double> prob=ne1.getProbability(value);
					for(int j=0;j<post.size();j++){
						//if(prob.get(j)>1) System.out.println(prob.get(j)+">1 numeric");
						post.set(j, post.get(j)*prob.get(j));
					}
				}
			}
		}
		/*
		for(int i=0;i<post.size();i++){
			System.out.print(String.format("%.6f",post.get(i))+" ");
		}
		System.out.println();
		*/
		return post;
	}
	
	
	public static void main(String[] args) throws Exception{
		/*NaiveBayse c1=new NaiveBayes();
		 *Evaluator e1=new Evaluator();
		 *
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
		
		
		NaiveBayes c1=new NaiveBayes();
		Evaluator e1=new Evaluator();
		ArrayList<String> arrArgs=new ArrayList<String>();
		Performance performance;
		for(String arg:args) arrArgs.add(arg);
		//System.out.println(arrArgs.toString());
		
		if(arrArgs.contains("-T")){
									String[] datasetArgs=new String[4];
									datasetArgs[0]="-t";
									datasetArgs[1]=arrArgs.get(arrArgs.indexOf("-t")+1);
									datasetArgs[2]="-T";
									datasetArgs[3]=arrArgs.get(arrArgs.indexOf("-T")+1);
			TrainTestSets t1=new TrainTestSets(datasetArgs);
			performance=e1.holdOut(t1.train, t1.test, c1);
		}
		else{
									String[] datasetArgs=new String[2];
									datasetArgs[0]="-t";
									datasetArgs[1]=arrArgs.get(arrArgs.indexOf("-t")+1);
			TrainTestSets t1=new TrainTestSets(datasetArgs);
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

	}
}
