import java.util.ArrayList;

class KernalPerceptron implements Classifier{
	ArrayList<Double> alpha;
	int c;
	int d;
	int k;
	int x;
	int delta;
	int classIndex;
	DataSet train;
	
	public KernalPerceptron(){
		alpha=new ArrayList<Double>();
		c=1;
		d=1;
		delta=7;
	}
	
	public double dotProduct(Example x, Example y){
		double result=0;
		for(int i=0;i<x.size();i++){
			if(i!=classIndex){
				result+=x.get(i)*y.get(i);
			}else{
				//homogeneous coordinate
				result+=1;
			}
		}
		return result;
	}
	
	public double polinomialKernal(Example exi,Example exj, double c, double d){
		
		double xy=dotProduct(exi, exj);
		return Math.pow((double)xy+c, d);
	}
	

	
	public double gaussianKernal(Example exi,Example exj){
		double result=0;
		for(int index=0;index<exi.size();index++){
			if(index!=classIndex){
				result+=Math.pow(exi.get(index)-exj.get(index),2);
			}
		}
		return Math.exp(-result/(2*Math.pow(delta, 2)));
	}
	
	
	public void train(DataSet trainSet){
		this.train=trainSet;
		this.classIndex=trainSet.attributes.classIndex;
		Examples examples=trainSet.examples;
		
		boolean converge=false;
		
		for(int i=0;i<examples.size();i++){
			alpha.add(0.0);
		}
		
		while(!converge){
			converge=true;
			for(int i=0;i<examples.size();i++){
				double y=0;
				for(int j=0;j<examples.size();j++){
					if(k==0){
						double yj=examples.get(j).get(classIndex);
						y+=alpha.get(j)*yj*polinomialKernal(examples.get(i), examples.get(j),c,d);
					}else{
						double yj=examples.get(j).get(classIndex);
						y+=alpha.get(j)*yj*gaussianKernal(examples.get(i), examples.get(j));
					}
				}
				
				double yi=examples.get(i).get(classIndex);
				if(y*yi<=0){
					alpha.set(i, alpha.get(i)+1);
					converge=false;
				}
				//System.out.println(alpha);
			}
		}
	}
	
	public int classify(Example ex){
		double y=0.0;
		Examples examples=train.examples;
		for(int j=0;j<examples.size();j++){
			if(k==0){
				double yj=examples.get(j).get(classIndex);
				y+=alpha.get(j)*yj*polinomialKernal(ex, examples.get(j),c,d);
			}else if(k==1){
				double yj=examples.get(j).get(classIndex);
				y+=alpha.get(j)*yj*gaussianKernal(ex, examples.get(j));
				
			}
		}
		if(y>0){
			return 1;
		}
		else{
			return -1;
		}
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
	
	public void setOptions(ArrayList<String> arrArgs){
		if(arrArgs.contains("-k")){
			String kernal = arrArgs.get(arrArgs.indexOf("-k")+1);
			if(kernal=="p"){
				k=0;
				if(arrArgs.contains("-c")){
					int constant = Integer.valueOf(arrArgs.get(arrArgs.indexOf("-c")+1));
					c=constant;
				}
				if(arrArgs.contains("-d")){
					int power = Integer.valueOf(arrArgs.get(arrArgs.indexOf("-d")+1));
					d=power;
				}
			}
			else if(kernal=="g"){
				k=1;
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		Evaluator e1=new Evaluator();
		ArrayList<String> arrArgs=new ArrayList<String>();
		Performance performance;
		for(String arg:args) arrArgs.add(arg);
		//System.out.println(arrArgs.toString());

		KernalPerceptron c1=new KernalPerceptron();
		c1.setOptions(arrArgs);
		
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