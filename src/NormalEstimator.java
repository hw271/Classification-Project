import java.util.ArrayList;

//反思：nullPointerException的发生很有可能是该实例化的对象仅仅声明却没有实例化，如：ArrayList<Double> array1;
//构造函数内一定要将类声明的变量实例化！！！！！

public class NormalEstimator implements Estimator{

	//attribute存储的是该Estimator对应的属性
	Attribute attribute;
	//indexOfAttributes存储的是这个attribute在attributes中是第几个，方便从example中抓到数据的位置
	int indexOfAttributes;
	//labelClass存储的是最后要判断的属性
	Attribute labelClass;
	//eg    make 	type
	//		trek 	hybrid
	//		giant	hybrid
	//		giant	mountain
	//attrClassData存储的是train中的该属性的data，是一个二维的arrayList，一维索引是存储的labelClass的index，即是hybrid还是mountain
	//arrayList中的data是某个type中的make，类似而为数组 attrClassData[type][make]
	ArrayList<ArrayList<Double>> attrClassData; 
	//param存储的是model的信息，包括mean,var,numOfExamples
	//param也相当于一个而为数组，param[type][statisticsInformation]
	ArrayList<ArrayList<Double>> param;
	/*
	->method1: addValue(double newV)
			mean=(mean*n+newV)/(n+1);
			variance=sqrt((variance*variance*n+(newV-mean)*(newV-mean))/(n+1));
			n=n+1;
			//用来修改module中的mean和variance，每个module对应一个类
	->method2: getProbability(int classIndex)
			p=exp(-(x-mean)powerOf2/(2*variance))/(standardError*sqrt(2*3.14))
	*/
	
	public NormalEstimator(){
		this.attrClassData=new ArrayList<ArrayList<Double>>();
		this.param=new ArrayList<ArrayList<Double>>();
	}
	
	public NormalEstimator(DataSet train, Attribute attribute, Attribute labelClass){
		//==================initial part========================
		//index是Estimator所对应的attribute在attributes中的位置，这个变量是为了方便在examples中找到每个example中这个属性的值
		this.attribute=attribute;
		this.labelClass=labelClass;
		//build a 2-dimension arrayList, 数组的一维index指示的归属的类，数组的第二维存储的是属于该类的实例在这个attribute中的值
		this.attrClassData=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<labelClass.domain.size();i++){
			ArrayList<Double> attrData=new ArrayList<Double>();
			attrClassData.add(attrData);
		}
		//build a 2-dimension arrayList, 数组的一维index指示的归属的类，数组的第二维存储的是parameter, 包括mean,var,numOfExamples
		this.param=new ArrayList<ArrayList<Double>>();
		
		for(int i=0;i<labelClass.domain.size();i++){
			ArrayList<Double> statisticsData=new ArrayList<Double>();
			param.add(statisticsData);
		}

		//获得属性在example中值存储的位置index（该属性是example中第几个属性）
		int index=train.attributes.getIndex(this.attribute.name);
		this.indexOfAttributes=index;
		int classIndex=train.attributes.getIndex(labelClass.name);
		
		//===================put data in attrClassData===============
		Examples e1=train.getExamples();
		for(int i=0;i<e1.size();i++){
			double	value=e1.get(i).get(index);
			int classPos=(int)(double)e1.get(i).get(classIndex);
			//System.out.println("classPos="+classPos);
			attrClassData.get(classPos).add(value);
		}
		this.buildModel(this.attrClassData);
		
	
	}
	
	private void buildModel(ArrayList<ArrayList<Double>> attrClassData){
		//System.out.println("attrClassData.size()="+attrClassData.size());
		for(int i=0;i<attrClassData.size();i++){
			double mean=0;
			double var=0;
			//System.out.println("mean="+mean+" var="+var);
			ArrayList<Double> data=attrClassData.get(i);
			double numOfExamples=data.size();
			System.out.println("numOfExamples="+data.size());
			//建立model-normal distribution
			for(int j=0;j<numOfExamples;j++){
				double value=data.get(j);
				//System.out.println("value="+value);
				mean+=value;
			}
			mean/=numOfExamples;
			var=getVar(data, mean, numOfExamples);
			//System.out.println("mean="+mean+" var="+var);
			
			this.param.get(i).add(mean);
			this.param.get(i).add(var);
			this.param.get(i).add(numOfExamples);
			/*
			ArrayList<Double> tempParam=new ArrayList<Double>();
			tempParam.add(mean);
			tempParam.add(var);
			tempParam.add(numOfExamples);
			System.out.println("i="+i);
			this.param.set(i, tempParam);
			*/
		}
	}
	
	private double getVar(ArrayList<Double> data, double mean, double numOfExamples){
		double var=0;
		for(int j=0;j<numOfExamples;j++){
			double value=data.get(j);
			var+=Math.pow((value-mean),2);
		}
		var/=numOfExamples;
		return var;
	}
	
	public void addValue(double newValue){


	}
	
	//返回的是arrayList，也就是属于A1，A2，A3。。。类分别的概率
	public ArrayList<Double> getProbability(double x){
		//p=exp(-(x-mean)powerOf2/(2*variance))/(standardError*sqrt(2*3.14));
		double PI=3.14;
		ArrayList<Double> prob=new ArrayList<Double>();
		for(int i=0;i<labelClass.getSize();i++){			
			double mean=this.param.get(i).get(0);
			double var = this.param.get(i).get(1);
			double denominator=Math.sqrt(var*2*PI);
			double numerator=Math.exp( -Math.pow((x-mean),2) / (2*var));
			prob.add(numerator/denominator);
			/*
			if(numerator/denominator>1){
				System.out.println("	mean="+mean+" var="+var+" denominator="+denominator+" numerator="+numerator+" probability="+numerator/denominator);
				System.out.println("	x="+x);
			}
			*/
		}
		return prob;
	}
	
	public void printParam(){
		System.out.println("statistics information:");
		for(int i=0;i<labelClass.getSize();i++){
			System.out.println("	class="+labelClass.domain.get(i)+", mean="+this.param.get(i).get(0)+" var="+this.param.get(i).get(1));
		}
	}
	
	public static void main(String[] args) throws Exception {
		TrainTestSets t1=new TrainTestSets(args);
		//System.out.println(t1.toString());
		System.out.println(t1.train.attributes.get(4).toString());
		NormalEstimator n1=new NormalEstimator(t1.train, t1.train.attributes.get(4),t1.train.attributes.get(t1.train.attributes.getSize()-1));
		n1.printParam();
		System.out.println(n1.getProbability(212).toString());
	}
	
	
}
