
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/* in project 3, I add following methods:
 * 
 * [method1]: public boolean isAllInOneClass()
 * this method is used to judge whether the node should be a leaf:
 * classLable_1=examples.get(0).classLable
 * foreach example in exampels:
 * 		if example.classLable != classLable_1
 * 			return false
 * return true;
 * 
 * [method2]: public int voteClass()
 * this method is used return the classLabel. When doing the test(observation), when the observation meet 
 * 		this node, which classLable it should be 
 * 		for(int i=0;i<this.examples.size();i++){
 *			int exampleVoteIndex=(int)(double)this.examples.get(i).get(classLableIndex);
 *			vote.set(exampleVoteIndex, vote.get(exampleVoteIndex)+1);
 *		}
 * 
 * [method3]: ArrayList<ArrayList<Integer>> getClassDistribution(Attribute attribute)
 * foreach example ex:
			attrValue=ex.get(attrIndex);
			classValue=ex.get(classLableIndex);
			update:num[attrValue][classValue]+=1
	(in the method it use arrayList to achieve this)
 * 
 * [methdo4]: ArrayList<DataSet> partitionOnAttribute(Attribute attr)
 * 		foreach example ex
			//indexInDomain存的是example的这个attribute下面的值，也是这个attribute等于的这个value在domain中的index
			indexInDomain=ex.get(attributeIndex);
			update:partitionDataSets.get(indexInDomain).examples.add(ex);
		}
		return partitionDataSets;
 * 
 * [method5]: double getInfoGain(Attribute attribute)
 * 		informationGain=entropy(S)-sum(sv/S*entropy(sv))
 * 		entropy(s)=-sum(plogp)
 * 		      (# of examples with classLabel=y and attributeValue=x)
 *      p=    ---------------------------------------------------------
 *            (# of examples with attributeValue=x)
 * 
 * [method6]: double getInfoSplit(Attribute attribute)
 * 		informationSplit=-sum(sv/S*log(sv/S))
 * 		sv=# of examples with attributeValue=x
 * 		S= totol # of examples
 * 
 * [method7]: Attribute getBestAttr()
 * 		foreach attribute in attributes:
 * 			            informationGain
 * 			gainRatio=------------------
 *  					informationSplit
 *  	find the attribute x which has biggest gainRatio
 *  return x;
 * */


public class DataSet implements OptionHandler {

	protected Attributes attributes;
	// the Attributes object for attribute attributes, types, and domains
	protected Examples examples;
	// the examples
	protected String name;
	// the name of this data set
	protected java.util.Random random;
	// a random number generator
	protected long seed;
	// default random seed

	// constructor:
	public DataSet() {
		attributes = new Attributes();
		examples = new Examples(attributes);
	}
	
	public DataSet(Attributes attributes) {
		this.attributes = attributes;
		examples = new Examples(attributes);
	}
	
	public DataSet(String name, Attributes attributes){
		this.name=name;
		this.attributes=attributes;
		examples = new Examples(attributes);
	}

	// for this construction method, filename correspond to a file which should
	// only have @examples
	public DataSet(Attributes attributes, String filename) throws IOException {
		this.attributes = attributes;
		examples = new Examples(attributes);
		this.loadExample(filename);
	}

	public DataSet(String filename) throws IOException {
		// use the BufferedInputStream to read all the content in one time
		// use String lines=new String(filecontent,"UTF-8"); to revert byte to
		// string
		this.load(filename);
	}

	public boolean isAllInOneClass(){
		int classLableIndex=this.attributes.classIndex;
		if(this.examples.size()==0){
			return true;
		}
		double value=this.examples.get(0).get(classLableIndex);
		for(int i=0;i<this.examples.size();i++){
			if(value!=this.examples.get(i).get(classLableIndex)){
				return false;
			}
		}
		return true;
	}
	public int voteClass() {
		int classLableIndex=this.attributes.classIndex;
		ArrayList<Integer> vote=new ArrayList<Integer>();
		for(int i=0;i<attributes.get(classLableIndex).domain.size();i++){
			vote.add(0);
		}
		for(int i=0;i<this.examples.size();i++){
			int exampleVoteIndex=(int)(double)this.examples.get(i).get(classLableIndex);
			vote.set(exampleVoteIndex, vote.get(exampleVoteIndex)+1);
		}
		int winnerClassIndex=0;
		int winnerVote=vote.get(winnerClassIndex);
		for(int i=0;i<vote.size();i++){
			if(vote.get(i)>winnerVote){
				winnerClassIndex=i;
				winnerVote=vote.get(winnerClassIndex);
			}
		}
		return winnerClassIndex;
	}
	//num 的第一层是attribute domain; 第二层是classLabel
	public ArrayList<ArrayList<Integer>> getClassDistribution(Attribute attribute){
		if(attribute.is_numeric){
			//System.out.println("this is a numeric attribute. in DataSet::getEntropy(Attribute attribute) your attribute should be categorical ");
			return null;
		}
		//# of examples with attribute j and class i
		//initialization
		ArrayList<ArrayList<Integer>> num=new ArrayList<ArrayList<Integer>>();
		int attrIndex=this.attributes.getIndex(attribute.name);
		int classLableIndex=this.attributes.classIndex;
		Attribute classAttribute=this.attributes.get(classLableIndex);
		
		for(int i=0;i<attribute.domain.size();i++){
			ArrayList<Integer> temp=new ArrayList<Integer>();
			for(int j=0;j<classAttribute.domain.size();j++){
				temp.add(0);
			}
			num.add(temp);
		}
		//update num
		for(int i=0;i<this.examples.size();i++){
			Example ex=this.examples.get(i);
			int attrValue=(int)(double)ex.get(attrIndex);
			int classValue=(int)(double)ex.get(classLableIndex);
			ArrayList<Integer> tempNum=num.get(attrValue);
			tempNum.set(classValue, tempNum.get(classValue)+1);
			num.set(attrValue, tempNum);
		}
		return num;
	}

	public ArrayList<DataSet> partitionOnAttribute(Attribute attr){
		if(attr.is_numeric){
			//System.out.println("this is a numeric attribute. in DataSet::getEntropy(Attribute attribute) your attribute should be categorical ");
			return null;
		}
		
		//initialization part
		ArrayList<DataSet> partitionDataSets=new ArrayList<DataSet>();
		for(int i=0;i<attr.domain.size();i++){
			DataSet temp=new DataSet();
			temp.name=this.name;
			temp.attributes=this.attributes;
			temp.examples.attributes=this.attributes;
			partitionDataSets.add(temp);
		}
		//System.out.println(partitionDataSets.size());
		
		
		int attributeIndex=this.attributes.getIndex(attr.name);
		//System.out.println("attribute="+attr.name+" index="+attributeIndex);
		for(int i=0;i<this.examples.size();i++){
			//indexInDomain存的是example的这个attribute下面的值，也是这个attribute等于的这个value在domain中的index
			Example ex=examples.get(i);
			int indexInDomain=(int)(double)ex.get(attributeIndex);
			partitionDataSets.get(indexInDomain).examples.add(ex);
		}
		return partitionDataSets;
	}
	
	//return information gain
	public double getInfoGain(Attribute attribute){
		/*num is to store the number of elements whose values attribute_j's value is X and class_label is Y
		 * the outer loop is for attribute_j's value and the inner loop is for class lable
		 * */
		if(attribute.is_numeric){
			//System.out.println("this is a numeric attribute. in DataSet::getEntropy(Attribute attribute) your attribute should be categorical ");
			return 0;
		}
		int classLableIndex=this.attributes.classIndex;
		Attribute classAttribute=this.attributes.get(classLableIndex);
		ArrayList<ArrayList<Integer>> num=getClassDistribution(attribute);
		
		double sumSvEntropy=0;
		for(int i=0;i<attribute.domain.size();i++){
			ArrayList<Integer> attrNum=num.get(i);
			//System.out.println(attrNum);
			
			int totalExs=this.examples.size();
			int totalNumInAttr=0;
			for(int j=0;j<classAttribute.domain.size();j++){
				totalNumInAttr+=attrNum.get(j);
			}
			//System.out.println("totalNumInAttr="+totalNumInAttr);
			double entropySv=0;
			for(int j=0;j<classAttribute.domain.size();j++){
				if(totalNumInAttr!=0){
					double p=(double)attrNum.get(j)/totalNumInAttr;
					//System.out.println("p="+p);
					if(p!=0)
						entropySv+=-p*Math.log10(p)/Math.log10(2);
					//System.out.println("entropySv="+entropySv);
				}
			}
			sumSvEntropy+=entropySv*totalNumInAttr/totalExs;
			//System.out.println("sumSvEntropy="+sumSvEntropy);
		}

		double entropy=0;
		for(int i=0;i<classAttribute.domain.size();i++){
			double numOfClassi=0;
			for(int j=0;j<attribute.domain.size();j++){
				numOfClassi+=num.get(j).get(i);
			}
			double p=numOfClassi/this.examples.size();
			if(p!=0){
				entropy+=-p*Math.log10(p)/Math.log10(2);
			}
			else{
				entropy+=0;
			}
			//System.out.println("entropy="+entropy);
		}
		double infoGain=entropy-sumSvEntropy;
		//System.out.println("attribute="+attribute);
		//System.out.println("infoGain="+infoGain);
		//System.out.println("=============================");
		return infoGain;
	}

	public double getInfoSplit(Attribute attribute){
		if(attribute.is_numeric){
			//System.out.println("this is a numeric attribute. in DataSet::getEntropy(Attribute attribute) your attribute should be categorical ");
			return 0;
		}
		double infoSplit=0;
		int s=this.examples.size();
		ArrayList<ArrayList<Integer>> num=getClassDistribution(attribute);
		for(int i=0;i<attribute.domain.size();i++){
			int sv=0;
			ArrayList<Integer> numClassLabel=num.get(i);
			for(int j=0;j<numClassLabel.size();j++){
				sv+=numClassLabel.get(j);
			}
			double p=(double)sv/s;
			if(p!=0){
				infoSplit+=-p*Math.log(p)/Math.log(2);
			}
		}
		return infoSplit;
	}

	public Attribute getBestAttr(){
		Attribute tempAttr;
		double tempInfoGain;
		double tempInfoSplit;
		double tempGainRatio;
		Attribute bestAttr=null;
		double bestGainRatio=0;
		for(int i=0;i<attributes.getSize();i++){
			tempAttr=attributes.get(i);
			if(tempAttr.equals(attributes.get(attributes.classIndex))) 
				continue;
			tempInfoGain=getInfoGain(tempAttr);
			tempInfoSplit=getInfoSplit(tempAttr);
			if(tempInfoSplit!=0){
				tempGainRatio=tempInfoGain/tempInfoSplit;
			}
			else{
				tempGainRatio=0;
			}
			//System.out.println(tempGainRatio);
			if(bestGainRatio<tempGainRatio){
				bestGainRatio=tempInfoGain;
				bestAttr=tempAttr;
			}
		}
		return bestAttr;
	}

	public void add(DataSet dataset) {
		// Adds the examples of the specified data set to this data set.
		if (attributes.equals(dataset.attributes)) {
			examples.addAll(dataset.examples);
		} else {
			System.out
					.println("illegal add. this dataset does not have the same attributes.");
		}

	}

	public void add(Example example) {
		if (example.size() == attributes.getSize()) {
			// check whether the value is ok
			int i = 0;
			for (double x : example) {
				if (x >= attributes.get(i).domain.size()) {
					System.out.println("x is larger than the size of attribute"
							+ i + ".domain, which is "
							+ attributes.get(i).domain.size());
					break;
				}
				i++;
			}
			examples.add(example);
		} else {
			System.out
					.println("illegal add. this example does not have the same # of attributes.");
		}
	}

	public Attributes getAttributes() {
		// *Gets the attributes of this DataSet object.
		return attributes;
	}

	public Examples getExamples() {
		// Gets the examples of this data set.
		return examples;
	}

	public boolean getHasNominalAttributes() {
		// Returns true if this data set has nominal attributes; returns false
		// otherwise.
		return attributes.getHasNominalAttributes();
	}

	public boolean getHasNumericAttributes() {
		// Returns true if this data set has numeric attributes; returns false
		// otherwise.
		return attributes.getHasNumericAttributes();
	}

	public long getSeed() {
		// Gets the seed for this data set.
		return seed;
	}

	public void load(String filename) throws IOException {
		File file = new File(filename);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));
		fin.read(filecontent);
		String lines = new String(filecontent, "UTF-8");

		Scanner sc = new Scanner(lines);
		this.parse(sc);
	} 
	
	public void loadExample(String filename) throws IOException {
		// Loads examples from the specified file
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String line = br.readLine();
		boolean is_example = false;
		while (line != null) {
			if (is_example) {
				Scanner sc = new Scanner(line);
				Example newEx = new Example(attributes, sc);
				examples.add(newEx);
			}
			if (line.contains("@example")) {
				is_example = true;
			}
			line = br.readLine();
		}
		br.close();
	}

	private void parse(Scanner sc) {

		sc.useDelimiter("@examples");
		String dataset_attributes = sc.next();
		//System.out.println("DataSet.parse(): dataset_attributes=\n"+dataset_attributes);
		Scanner sc_set = new Scanner(dataset_attributes);
		while (sc_set.hasNext()) {
			String term = sc_set.next();
			if (term.equals("@dataset")) {
				break;
			}
		}
		this.name = sc_set.next();

		Scanner sc_attri = new Scanner(dataset_attributes);
		this.attributes = new Attributes();
		this.attributes.parse(sc_attri);

		String examples = "@examples\n" + sc.next();
		//System.out.println("DataSet.parse(): examples=\n"+examples);
		Scanner sc_ex = new Scanner(examples);

		this.examples = new Examples(this.attributes, sc_ex);
	}

	// ??????????????????????????????
	public void setOptions(String[] options) {
		// Sets the options for this data set.
	}

	public void setRandom(java.util.Random random) {
		// Sets the random number generator for this data set.
		this.random = random;
	}

	public void setSeed(long seed) {
		// Sets the random number seed for this data set.
		this.seed = seed;
	}

	public String toString() {
		// Returns a string representation of the data set in a format similar
		// to that of the file format.
		String st = "";
		st += "@dataset " + name + "\n";
		st += attributes.toString();
		if(examples.size()!=0){
			st += examples.toString();
		}
		else{
			st += "no examples";
		}
		return st;
	}
	
	public static void main(String[] args) throws IOException {
		String filename = "./iris.mff";
		DataSet dataset = new DataSet(filename);
		//for(int i=0;i<dataset.attributes.getSize()-1;i++){
		//}
//		Attribute a1=dataset.attributes.get(1);
	//	a1=dataset.getBestAttr();
	//	double infoSplit=dataset.getInfoSplit(a1);
	//	System.out.println(infoSplit);
		System.out.println(dataset);
		//System.out.println(dataset.voteClass());
		
	}
}
