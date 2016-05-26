import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

class DTNode{
	Attribute attribute;
	ArrayList<ArrayList<Integer>> numOfEx;
	ArrayList<DTNode> children;
	DTNode parent;
	int classIndex;
	
	public DTNode(){
		attribute=null;
		numOfEx=new ArrayList<ArrayList<Integer>>();
		children=new ArrayList<DTNode>();
		classIndex=-1;
	}
	
	public DTNode(Attribute attr){
		attribute=attr;
		numOfEx=new ArrayList<ArrayList<Integer>>();
		children=new ArrayList<DTNode>();
		classIndex=-1;
	}
	
	public String toString(){
		String st="";
		//st+=attribute.name;
		//st+="\n	children size"+children.size();
		st+="\n	examples_distribution"+numOfEx;
		return st;
	}
}

/*DecisionTree class
 * root
 * attributes
 * leafNodes: this arrayList is to help pruning. it is a FIFO queue which can ensure pruning all unnecessary children
 * 
 * method-void buildTree(DataSet dataset, DTNode root):
 * 		dataset.findBestAttribute();
 * 		use the attribute to partition the DataSet;
 * 		for each subDataSet:
 * 			if(# of subDataset.examples==0) build an empty leaf node;
 * 			else if(examples of subDataSet are all in one class OR cannot find split attribute) build an real leaf node;
 * 			else recursively build subTree
 * end method 
 * 
 ****************************************************************************************
 * [Note] the reason we need an empty leaf node: deal with following situation
 * situation: at node n-1, we use make as the decision-attribute, whose domain is [trek, giant, fenghuang]
 * and when doing the train(): we just has two examples: trek ... mtn; giant ... hybrid
 * when doing the test(): we meet an example fenghuang ...;
 * if we do not have an empty node corresponding to the attribute_value=fenghuang
 * the decision tree will meet error
 * So we need the empty leaf node
 * 
 * how to decide the previous test observation's class
 * I use its parent's ClassLabel as its  classLabel
 *****************************************************************************************
 *
 *****************************************************************************************
 *the reason sometimes we cannot find split attribute anymore (which means that in leaf node, examples still have different classLabel)is that:
 *considering the following situations:
 *		one node just contains the following two examples:
 *		example1: trek knobby straight y 250.3 mountain
 *		example2: trek knobby straight y 185.4 hybrid
 *
 *		except numeric attribute, all other attributes' value in these two examples are the same.
 *		when meeting such situations, stop split the tree. make this node as a leaf
 ***************************************************************************************** 
 *
 *method int classify(Example e1)
 * start from root
 * while(node is not a leaf){
 * 		based on example e1
 * 		node=one of node.children
 * }
 * return node.classIndex
 * end method
 * 
 * other methods about pruning:
 * 		I write it near the prune method
 * 
 * main:
 * it is almost same as NaiveBayes, except the classifier is instance of DecisionTree instead of NaiveBayes
 * */


public class DecisionTree implements Classifier{
	DTNode root;
	Attributes attributes;
	ArrayBlockingQueue<DTNode> leafNodes;
	//pruning parameter:
	double z;
	
	public DecisionTree(){
		root=new DTNode();
		leafNodes=new  ArrayBlockingQueue<DTNode>(1000);
		attributes=new Attributes();
		z=0.6925;
	}
	public DecisionTree(DataSet trainSet) throws InterruptedException{
		train(trainSet);
	}

	public void train(DataSet trainSet) throws InterruptedException{
		root=new DTNode();
		leafNodes=new  ArrayBlockingQueue<DTNode>(trainSet.examples.size());
		attributes=trainSet.attributes;
		z=0.6925;
		buildTree(trainSet,root);
		prune(root);
	}
	
	public void buildTree(DataSet dataset, DTNode root) throws InterruptedException{
		root.attribute=dataset.getBestAttr();
		ArrayList<ArrayList<Integer>> num=dataset.getClassDistribution(root.attribute);
		root.numOfEx=num;
		root.parent=null;
		root.classIndex=dataset.voteClass();
		
		ArrayList<DataSet> datasets=dataset.partitionOnAttribute(root.attribute);
		
		//System.out.println(root);
		//System.out.println(dataset);
		for(int i=0;i<datasets.size();i++){
			
			DataSet childDataSet=datasets.get(i);
			//empty leaf node
			/*the reason we need an empty leaf node:
			 * situation: at node n-1, we use make as the decision-attribute, whose domain is [trek, giant, fenghuang]
			 * and when doing the train(): we just has two examples: trek ... mtn; giant ... hybrid
			 * when doing the test(): we meet an example fenghuang ...;
			 * if we do not have an empty node corresponding to the attribute_value=fenghuang
			 * the decision tree will meet error
			 * So we need the empty leaf node
			 * 
			 * how to decide the previous test observation's class
			 * I use its parent's ClassLabel as its  classLabel
			 * */
			if(childDataSet.examples.size()==0){
				DTNode child=new DTNode();
				child.parent=root;
				child.attribute=null;
				child.children=null;
				child.numOfEx=null;
				child.classIndex=child.parent.classIndex;
				root.children.add(child);
				continue;
			}
			//real leaf node
			else if(childDataSet.isAllInOneClass() || childDataSet.getBestAttr()==null){
				//System.out.print(childDataSet.examples.size());
				//System.out.println(" is leaf");
				DTNode child=new DTNode();
				child.parent=root;
				child.attribute=null;
				child.children=null;
				child.numOfEx=childDataSet.getClassDistribution(root.attribute);
				child.classIndex=childDataSet.voteClass();
				//System.out.println("vote="+child.classIndex);
				root.children.add(child);
				
				//System.out.println("leaf node:"+child.numOfEx);
				this.leafNodes.add(child);
				//System.out.println("after add child, queue size="+this.leafNodes.size());
				continue;
			}
			//not leaf
			else{
				//System.out.println(childDataSet.examples.size());
				DTNode child=new DTNode();
				child.parent=root;
				//child's attribute, children and numOfEx are set in buildTree;
				//System.out.println("before build the subtree, queue size="+this.leafNodes.size());
				buildTree(childDataSet, child);
				//System.out.println("after build the subtree, queue size="+this.leafNodes.size());
				//System.out.println("intermedia node:"+child.numOfEx);
				child.classIndex=childDataSet.voteClass();
				root.children.add(child);
				//System.out.println("add child="+child);
			}
		}
		//System.out.println("root.attribute.name="+root.attribute.name+" root.children.size()="+root.children.size());
	}
	
	/* methods about pruning:
	 * 
	 * 1.void prune()
	 * 2.boolean notAllChildrenAreLeaves(DTNode parent) 
	 * 3.boolean shouldPrune(DTNode parent)
	 * 4.double getNP(DTNode node)
	 * 5.double u25(int n1, int x1)
	 *  
	 *getNP(parent):
	 *		find n and x;
	 *		return n*u25%(n,x) 
	 * 
	 *u25(n,x)
	 *		calculate the value according to the formula
	 * */
	//calculate u25%(n,x)
	public double u25(int n1, int x1){
		double p;
		double n=(double) n1;
		double x=(double) x1;
		p=(x+0.5+z*z/2+Math.sqrt(z*z*((x+0.5)*(1-(x+0.5)/n)+z*z/4)))/(n+z*z);
		//System.out.println("u25(): p="+p);
		return p;
	}
	
	//return n*u25%(n,x)
	public double getNP(DTNode node){
		double npOfNode;
		int n=0;
		int rightClassed=0;
		for(int i=0;i<node.numOfEx.size();i++){
			ArrayList<Integer> numClass=node.numOfEx.get(i);
			for(int j=0;j<numClass.size();j++){
				n+=numClass.get(j);
			}
			//System.out.println("classIndex="+node.classIndex);
			//System.out.println(numClass.size());
			rightClassed+=numClass.get(node.classIndex);
		}
		//System.out.println("n="+n);
		npOfNode=n*u25(n,n-rightClassed);
		//System.out.println("node n*p="+npOfNode);
		return npOfNode;
	}
	
	/* prune method1:
	 * it is a top-down method:
	 * recursively, compare parent node's error and children's error
	 * */
	///*
	private double prune(DTNode parent){
		double parentError=0;
		double childrenError=0;
		//if it is an empty leaf
		if(parent.numOfEx==null){
			return 0.0;
		}
		else if(parent.children==null){
			return getNP(parent);
		}
		else{
			for(int i=0;i<parent.children.size();i++){
				double tempError=prune(parent.children.get(i));
				if(tempError==-1){
					return -1;
				}
				childrenError+=tempError;
			}
		}
		parentError=getNP(parent);
		if(parentError<childrenError){
			System.out.println("prune");
			parent.children=null;
			return parentError;
		}
		else{
			return -1;
		}
	}
	//*/
	/*
	private double prune(DTNode parent){
		double parentError=0;
		double childrenError=0;
		//if it is an empty leaf
		if(parent.numOfEx==null){
			return 0.0;
		}
		else if(parent.children==null){
			return getNP(parent);
		}
		else{
			for(int i=0;i<parent.children.size();i++){
				double tempError=prune(parent.children.get(i));
				childrenError+=tempError;
			}
		}
		parentError=getNP(parent);
		if(parentError<childrenError){
			System.out.println("prune");
			parent.children=null;
			return parentError;
		}
		else{
			return childrenError;
		}
	}
	*/
	/* prune method2: just ignore this part. I try another way, "bottom up", to implement the prune. 
	 * it also works.
	 * it is a bottom up method:
	 * it is not a recursive method. I use a queue to store all leaf nodes.
	 * I also compare the difference of standard prune and not-standard prune here.
	 * 
	 * void prune()	
	 	while(!leafNodes.isEmpty()){
			DTNode headChild = leafNodes.poll();
			DTNode parent = headChild.parent;
			if(parent==null: which means it is root, should not do prune anymore){
				break;
			}
			if(parent.children==null || notAllChildrenAreLeaves(parent)){
				******************************************************************************************
				*[Note]these two conditions are used to test the difference between standard pruning method 
				*in not-standard method, I should use parent.children==null 
				*	this is because in previous pruning, all children of this node may have already been pruned
				*in standard method, I use notAllChildrenAreLeaves(parent):
				*	which means, if NOT ALL children of this node are leaves, I cannot prune this node
				*******************************************************************************************
				continue;
			}
			else{
				if(shouldPrune(parent)){
					parent.children=null;
					leafNodes.add(parent);
				}
			}
		}
	 *
	 *shouldPrune(DTNode parent):
	 *		if parent's n*u25% > sum(all children's n(i)*u25%(i)) return false
	 *		else return true;
	 */
	/*
	private boolean shouldPrune(DTNode parent){
		double pOfParent;
		double pOfChildren=0;
		pOfParent=getNP(parent);
		for(int k=0;k<parent.children.size();k++){
			double pOfChild;
			DTNode child = parent.children.get(k);
			if(child.numOfEx!=null){
				pOfChildren+=getNP(child);
			}
		}
		//System.out.println("		shouldPrune:pOfParent="+pOfParent);
		//System.out.println("		shouldPrune:pOfChildren="+pOfChildren);
		if(pOfParent>=pOfChildren){
			//System.out.println("should not prune");
			return false;
		}else{
			//System.out.println("should prune");
			return true;
		}
	}
	
	private boolean notAllChildrenAreLeaves(DTNode parent){
		boolean result=false;
		//System.out.println("parent.children.size()="+parent.children.size());
		//System.out.println("parent.attribute:"+parent.attribute);
		for(int i=0;i<parent.children.size();i++){
			//System.out.println(i);
			//System.out.println("parent child i="+parent.children.get(i));

				DTNode child=parent.children.get(i);
				if(child.children!=null){
					return true;
				}

		}
		return result;
	}
	
	
	private void prune2(){
		//System.out.println(leafNodes.size());
		while(!leafNodes.isEmpty()){
			DTNode headChild = leafNodes.poll();
			DTNode parent = headChild.parent;
			//System.out.println("in prune: parent.classIndex"+parent.classIndex);
			//System.out.println(headChild);
			if(parent==null){
				//System.out.println(headChild.numOfEx);
				continue;
			}
			else if(parent.children==null){
				continue;
			}
			
			//if having this part, it is a standard prune, else it is a not-standard prune
			else if(notAllChildrenAreLeaves(parent)){
				continue;
			}
			else{
				if(shouldPrune(parent)){
					parent.children=null;
					leafNodes.add(parent);
					//System.out.println("prune");
				}
			}
		}
	}
	*/
	public int classify(Example e1){
		DTNode parentNode=root;
		int attrIndex=attributes.getIndex(parentNode.attribute.name);
		//System.out.println(parentNode.attribute.name);
		int domainIndex=(int)(double)e1.get(attrIndex);
		//System.out.println("domainIndex="+domainIndex);
		//System.out.println(root.children);
		DTNode childNode=root.children.get(domainIndex);
		
		while(childNode.children!=null){
			parentNode=childNode;
			//System.out.println(e1);
			//System.out.println(parentNode.attribute.name);
			attrIndex=attributes.getIndex(parentNode.attribute.name);
			//System.out.println("attrIndex="+attrIndex);
			domainIndex=(int)(double)e1.get(attrIndex);
			//System.out.println("domainIndex="+domainIndex);
			childNode=parentNode.children.get(domainIndex);
		}
		return childNode.classIndex;
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
	
	public static void main(String[] args) throws Exception{
		/*	
		String filename = "./bike.mff";
		DataSet dataset = new DataSet(filename);
		DecisionTree dt=new DecisionTree(dataset);
		//System.out.println(dt.root);

		dt.train(dataset);
		System.out.println(dt.getNP(dt.root));

		*/
		///*
		
		DecisionTree c1=new DecisionTree();
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
		
	
		//*/	
	}	
}
