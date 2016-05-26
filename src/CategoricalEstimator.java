import java.util.ArrayList;

/*
 * 		[l3-class categoricalEstimator]
			
			ArrayList<Double> conditionalProbability[];
			->method1:addValue(int class)
					//调整p(a = attribute j|c=class i)	= (# of examples with att j in class)/(# of examples in class i)
			->method2:getProbability(int classIndex)
					//获取条件概率 +1smooth后的 p(a = attribute j|c=class i)
*/
public class CategoricalEstimator extends Estimator {
	Attribute attribute;
	int attributeIndex;
	
	Attribute labelClass;
	int labelClassIndex;
	
	int sizeOfDomain;
	int numOfClasses;
	int numOfExamples;
	//注意：condProb[]中存储的不是条件概率(#of attr_j  in class_i)
	ArrayList<ArrayList<Integer>> numInAttjClassi;
	ArrayList<Integer> numInClassi;

	
	public CategoricalEstimator(DataSet train, Attribute attribute, Attribute labelClass){
		this.attribute=attribute;
		this.attributeIndex=train.attributes.getIndex(this.attribute.name);
		
		this.labelClass=labelClass;
		this.labelClassIndex=train.attributes.getClassIndex();
		
		this.sizeOfDomain=attribute.domain.size();
		this.numOfClasses=labelClass.domain.size();
		
		//生成二维arrayList,存储# of elems with attributes j of class i
		this.numInAttjClassi=new ArrayList<ArrayList<Integer>>(this.sizeOfDomain);
		for(int i=0;i<this.sizeOfDomain;i++){
				ArrayList<Integer> temp=new ArrayList<Integer>();
				this.numInAttjClassi.add(temp);
				for(int classIndex=0;classIndex<this.numOfClasses;classIndex++){
					this.numInAttjClassi.get(i).add(0);
				}	
		}

		//生成一维arrayList，存储# of elems of class i
		this.numInClassi=new ArrayList<Integer>();
		for(int cl=0;cl<this.numOfClasses;cl++){
			this.numInClassi.add(0);
		}
		
		Examples es1=train.examples;
		for(Example e1:es1){
			int attrIndex=(int)(double)e1.get(this.attributeIndex);
			int classIndex=(int)(double)e1.get(this.labelClassIndex);
			this.addValue(attrIndex, classIndex);
		}
		
	}
	
	public void addValue(int attrIndex, int classIndex){
		this.numInAttjClassi.get(attrIndex).set(classIndex,this.numInAttjClassi.get(attrIndex).get(classIndex)+1);
		this.numInClassi.set(classIndex, this.numInClassi.get(classIndex)+1);
		this.numOfExamples+=1;
	}
	
	public double getProbability(int attrIndex, int classIndex){
		double smoothProb=(this.numInAttjClassi.get(attrIndex).get(classIndex)+1)/
				((double)this.numInClassi.get(classIndex)+(double)this.attribute.getSize());
		return smoothProb;
	}
	
	
	public static void main(String[] args) throws Exception{
		TrainTestSets t1=new TrainTestSets(args);
		System.out.println(t1.toString());
		for(int i=0;i<t1.train.attributes.getSize();i++){
			System.out.println("i="+i+", attribute="+t1.train.attributes.get(i).name+" labelClass="+t1.train.attributes.get(t1.train.attributes.getClassIndex()).name);
			if(!t1.train.attributes.get(i).is_numeric){	
				CategoricalEstimator e1=new CategoricalEstimator(t1.train, t1.train.attributes.get(i),t1.train.attributes.get(t1.train.attributes.getClassIndex()));
				for(int k=0;k<t1.train.attributes.get(i).getSize();k++){
					for(int j=0;j<t1.train.attributes.get(t1.train.attributes.getClassIndex()).getSize();j++){
							System.out.print(e1.getProbability(k, j)+" ");
						}
					System.out.println();
				}
				System.out.println();
			}
		}
			
	}
}
