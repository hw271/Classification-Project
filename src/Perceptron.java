import java.util.ArrayList;


public class Perceptron implements Classifier{
	ArrayList<Double> w;
	double eta;
	
	public Perceptron(int attrNum){
		w=new ArrayList<Double>();
		for(int i=0;i<attrNum-1;i++){
			w.add(0.0);
		}
		//
		w.add(0.0);
		//
		eta=0.5;
	}
	
	public double dotProduct(ArrayList<Double> x, ArrayList<Double> w){
		if(x.size()!=w.size()){
			System.out.println("two vectors' size are not the same!");
			System.out.println("x="+x);
			System.out.println("w="+w);
			return 0.0;
		}
		double result=0;
		for(int i=0;i<x.size();i++){
			result+=x.get(i)*w.get(i);
		}
		return result;
	}
	
	public void train(DataSet trainSet){
		boolean converge=false;
		while(!converge){
			converge=true;
			for(int i=0;i<trainSet.examples.size();i++){
				Example ex=trainSet.examples.get(i);
				double yi=ex.get(ex.size()-1);
				//System.out.println(yi);
				ArrayList<Double> vectorX=new ArrayList<Double>(ex.subList(0, ex.size()-1));
				//new
				vectorX.add(1.0);
				//
				double y=dotProduct(vectorX, w);
				if(y*yi<=0){
					converge=false;
					for(int j=0;j<w.size();j++){
						w.set(j, w.get(j)+eta*yi*vectorX.get(j));
					}
					System.out.println(w);
					
				}
			}
		}
	}
	
	
	public int classify(Example ex){
		double result=0;
		for(int i=0;i<ex.size()-1;i++){
			result+=ex.get(i)*w.get(i);
		}
		result+=w.get(w.size()-1);
		if(result>0){
			return 1;
		}
		return -1;
	}
	
	public Performance classify(DataSet test){
		Examples testExamples=test.examples;
		Performance p1=new Performance();
		int misClass=0;
		for(Example ex:testExamples){
			int trueLabel=(int)(double)ex.get(test.attributes.getClassIndex());
			int label=this.classify(ex);
			//System.out.println(label+"  "+trueLabel);
			if(trueLabel!=label){
				//System.out.println("miss");
				misClass++;
				//System.out.println(ex);
				//System.out.println(label);
			}
		}
		p1.accuracy=1-(double)misClass/test.examples.size();
		return p1;
	}
	
	public static void main(String[] args) throws Exception{
			
			
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
				Perceptron c1=new Perceptron(t1.train.attributes.getSize());
				performance=e1.holdOut(t1.train, t1.test, c1);
			}
			else{
										String[] datasetArgs=new String[2];
										datasetArgs[0]="-t";
										datasetArgs[1]=arrArgs.get(arrArgs.indexOf("-t")+1);
				TrainTestSets t1=new TrainTestSets(datasetArgs);
				Perceptron c1=new Perceptron(t1.train.attributes.getSize());
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
