import java.io.IOException;
import java.util.ArrayList;

class HTNode{
	int classIndex;
	ArrayList<Integer> majorityVote;
	
	public HTNode(){
		majorityVote=new ArrayList<Integer>();
	}
	
	public void initializeVote(int classAmount){
		majorityVote=new ArrayList<Integer>(classAmount);
		for(int i=0;i<classAmount;i++){
			majorityVote.add(0);
		}
	}
}

class InternalHTNode extends HTNode{
	int attributeIndex;
	ArrayList<HTNode> children;
	public InternalHTNode(){
		majorityVote=new ArrayList<Integer>();
		children=new ArrayList<HTNode>();
	}
}

class LeafHTNode extends HTNode{
	//i is class value's index, j is attribute index and k is attribute value's index
	ArrayList<ArrayList<ArrayList<Integer>>> distribution;
	
	public LeafHTNode(){
		majorityVote=new ArrayList<Integer>();
		distribution=new ArrayList<ArrayList<ArrayList<Integer>>>();
	}
	
	public void initializeDistribution(Attributes attributes){
		int classIndex=attributes.getClassIndex();
		Attribute classAttr=attributes.get(classIndex);
		/* i is class value's index, j is attribute index and k is attribute value's index
		 * 				make							weight			waterbottles
		 * 				trek	giant	fenghuang		-				y		n
		 * class
		 * mountain
		 * hybrid
		 */
		
		int i1=classAttr.domain.size();
		int j1=attributes.getSize();
		
		for(int i=0;i<i1;i++){
			ArrayList<ArrayList<Integer>> attr_value=new ArrayList<ArrayList<Integer>>();
			for(int j=0;j<j1;j++){
				if(j!=classIndex){
					ArrayList<Integer> value=new ArrayList<Integer>();
					//System.out.println("j="+j+attributes.get(j).domain);
					if(!attributes.get(j).is_numeric){
						int k1=attributes.get(j).domain.size();
						for(int k=0;k<k1;k++){
							value.add(0);
						}
					}
					attr_value.add(value);
				}
				else{
					ArrayList<Integer> value=new ArrayList<Integer>();
					attr_value.add(value);
				}
			}
			distribution.add(attr_value);
		}
	}
	
	public void update(Example example){
	
	}
}

/* INPUT:
 * S: sequence of examples
 * X: a set of discrete attributes
 * G: information gain: a split evaluation function
 * theta: 1-theta is the probability that choosing the correct attribute at any given node
 * 
 * OUTPUT:
 * Hoeffding Decision Tree
 * */

public class HoeffdingTree implements Classifier{
	double epsilon;
	private double theta=0.25;
	HTNode root;
	
	public HoeffdingTree(){
		root=new HTNode();
	}
	
	public void train(DataSet trainSet){
		double R=getR(trainSet);
		int n=trainSet.examples.size();
		this.epsilon=Math.sqrt(R*R*Math.log(1/this.theta)/(2*n));
		
		buildTree(trainSet, root);
	}
	
	private void buildTree(DataSet trainSet, HTNode root){
		int classIndex=trainSet.attributes.classIndex;
		int classAmount = trainSet.attributes.get(classIndex).domain.size();
		for(int i=0;i<trainSet.examples.size();i++){
			//System.out.println("=====================================================");
			Example example=trainSet.examples.get(i);
			HTNode node;
			//根节点的建立 root
			if(i==0){
				LeafHTNode leaf=new LeafHTNode();
				leaf.initializeVote(classAmount);
				leaf.initializeDistribution(trainSet.attributes);
				this.root=leaf;
				node=this.root;
			}
			else{
				//start from the root and follow the right branch to find the leaf
				node=this.root;
				while(node instanceof InternalHTNode){
					InternalHTNode temp=(InternalHTNode)node;
					int attributeIndex=temp.attributeIndex;
					int index=(int)(double)example.get(attributeIndex);
					node=temp.children.get(index);
				}
			}
			
			//find the leaf node
			LeafHTNode leaf=(LeafHTNode)node;
			int classLable=(int)(double)example.get(classIndex);
			//update the vote table
			leaf.majorityVote.set(classLable, leaf.majorityVote.get(classLable)+1);
			//System.out.println(i+"th example="+example);
			//System.out.println("majorityVote:\n"+leaf.majorityVote);
			for(int j=0;j<trainSet.attributes.getSize();j++){
				if(j!=classIndex && !trainSet.attributes.get(j).is_numeric){
					int k=(int)(double)example.get(j);
					leaf.distribution.get(classLable).get(j).set(k, leaf.distribution.get(classLable).get(j).get(k)+1);
				}
			}				
			ArrayList<Double> informationGain=getInfoGain(leaf, trainSet.attributes);
			System.out.println("informationGain="+informationGain);
			
		}
	}
	
	private ArrayList<Double> getInfoGain(LeafHTNode leaf, Attributes attributes){
		int classIndex=attributes.classIndex;
		ArrayList<Double> informationGain=new ArrayList<Double>();
		int classNum=leaf.distribution.size();
		int attributesNum=leaf.distribution.get(0).size();
		//get the 
		int valueNum=leaf.distribution.get(0).get(0).size();
		ArrayList<Integer> SClassDistribution=new ArrayList<Integer>();
		for(int i=0;i<classNum;i++){
			int classiNum=0;
			for(int k=0;k<valueNum;k++){
				classiNum+=leaf.distribution.get(i).get(0).get(k);
			}
			//how many elems does each class has:
			SClassDistribution.add(classiNum);
		}
		//System.out.println("SClassDistribution="+SClassDistribution);
		double SEntropy=getEntropy(SClassDistribution);
		//System.out.println("SEntropy="+SEntropy);
		
		//get S
		int S=0;
		for(int i=0;i<SClassDistribution.size();i++){
			S+=SClassDistribution.get(i);
		}
		
		for(int j=0;j<attributesNum;j++){
			//initialize the information gain, which is equal to the total entropy
			if(j!=classIndex && !attributes.get(j).is_numeric){
				//get the Sv/S*entropy(Sv)
				int attrjvalueNum=leaf.distribution.get(0).get(j).size();
				//loop for value
				double sumSvEntropy=0;
				for(int k=0;k<attrjvalueNum;k++){
					ArrayList<Integer> SvClassDistribution=new ArrayList<Integer>();
					int classiNum=0;
					int Sv=0;
					//get class distribution and Sv
					//System.out.println("distribution="+leaf.distribution);
					for(int i=0;i<classNum;i++){
						classiNum=leaf.distribution.get(i).get(j).get(k);
						SvClassDistribution.add(classiNum);
						Sv+=classNum;
					}
					double SvEntropy=getEntropy(SvClassDistribution);
					//System.out.print("SvClassDistribution="+SvClassDistribution);
					//System.out.println(" j="+j+" k="+k+" SvEntropy="+SvEntropy);
					SvEntropy=(double)Sv/S*SvEntropy;
					sumSvEntropy+=-SvEntropy;
				}
				informationGain.add(SEntropy-sumSvEntropy);
			}
			else{
				informationGain.add(0.0);
			}
		}
		return informationGain;
	}
	
	private double getEntropy(ArrayList<Integer> classDistribution){
		int total=0;
		double entropy=0.0;
		for(int i=0;i<classDistribution.size();i++){
			total+=classDistribution.get(i);
		}
		if(total!=0){
			for(int i=0;i<classDistribution.size();i++){
				int temp=classDistribution.get(i);
				if(temp!=0){
					entropy+=-(double)temp/total*Math.log((double)temp/total);
				}
			}
		}
		return entropy;
	}
	
	private double getR(DataSet trainSet){
		int classIndex=trainSet.attributes.getClassIndex();
		int classNum=trainSet.attributes.get(classIndex).domain.size();
		return Math.log(classNum);
	}
	
	
	public static void main(String[] args) throws IOException{
		String filename = "./bike.mff";
		DataSet dataset = new DataSet(filename);
		HoeffdingTree hd=new HoeffdingTree();
		//System.out.println(hd.getR(dataset));
		LeafHTNode rootleaf=new LeafHTNode();
		rootleaf.initializeDistribution(dataset.attributes);
		//System.out.println("i="+rootleaf.distribution.size()+"distribution="+rootleaf.distribution);
		hd.root=rootleaf;
		if(hd.root instanceof LeafHTNode){
			LeafHTNode rootleaf1=(LeafHTNode)hd.root;
			System.out.println("distribution="+rootleaf1.distribution);
		}
		hd.train(dataset);
	}
}
