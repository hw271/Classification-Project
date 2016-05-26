

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Attributes {
	ArrayList<Attribute> attributes;
	int	classIndex=0;
	boolean	hasNominalAttributes=false;
	boolean	hasNumericAttributes=false;
	
	public Attributes(){
		initialize();
	}


	public Attributes(Scanner scanner){
		this.parse(scanner);
	}
	
	public void	add(Attribute newAttribute){
		//Adds a new attribute to this set of attributes.
		if(attributes.contains(newAttribute)){
			System.out.println("already has this attribute!");
			return ;
		}
		attributes.add(newAttribute);
		classIndex=attributes.size()-1;
	}
	
	public Attribute get(int index){
		//Returns the ith attribute in this set of attributes.
		if(index<0 || index>=attributes.size()){
			System.out.println("index="+index);
			System.out.println("size="+attributes.size());
			System.out.println("out of array's boundary");
			return null;
		}
		return attributes.get(index);
	}
	public int	getClassIndex(){
		//Returns the index of the class label.
		return classIndex;
	}
	
	public boolean	getHasNominalAttributes(){
		//Returns true if this set of attributes has one or more nominal attributes; returns false otherwise.
		return hasNominalAttributes;
	}
	public boolean	getHasNumericAttributes(){
		//Returns true if this set of attributes has one or more numeric attributes; returns false otherwise.
		return hasNumericAttributes;
	}
	public int	getIndex(String name){
		//注意不能用Object，因为indexOf比较的是object而不是字面值，类似于String a.equals(object b)和String a==b的区别
		for(int index=0;index<attributes.size();index++){
			if(attributes.get(index).name.equals(name)){
				return index;
			}
		}
		return -1;
	}
	
	public int	getSize(){
		//Returns the number of attributes.
		return attributes.size();
	}
	
	private void	initialize(){
		attributes=new ArrayList<Attribute>();
		classIndex=0;
		hasNominalAttributes=false;
		hasNumericAttributes=false;
	//Initializes the attributes list.
	}

	//string = <attribute><attribute><attribute-list>
	//input = new Scanner(string)
	//in process, this.attributes 添加了所有string中的attribute
	//output:void
	public void	parse(Scanner scanner){
		//Parses the attribute declarations in the specified scanner.
		//parse the scanner with "\n" and construct a new scanner with each line
		//use AttributeFactory.make(Scanner sc) to get one attribute, and add it to attributes
		scanner.useDelimiter("\n");
		while(scanner.hasNext()){
			String newLine=scanner.next().toLowerCase();
			if(newLine.contains("@example")){
				break;
			}
			if(newLine.contains("@attribute")){
				Scanner sc_attr=new Scanner(newLine);
				Attribute newAttribute=new Attribute();
				AttributeFactory at=new AttributeFactory();
				newAttribute=at.make(sc_attr);
				this.add(newAttribute);
				if(newAttribute.is_numeric){
					this.hasNumericAttributes=true;
				}
				else{
					this.hasNominalAttributes=true;
				}
				
			}
			else{
				continue;
			}
		}		
	}
	/**
	 * ???????????????/what does setClassIndex mean??????????????
	 * */
	public void	setClassIndex(int classIndex){
	//Sets the class index for this set of attributes.
		this.classIndex = classIndex;
	}
	public String	toString(){
		String varStr="";
		for(Attribute x:attributes){
			varStr+="@attribute "+x.name;
			if(!x.is_numeric){
				for(String domainValue:x.domain){
					varStr+=" "+domainValue;
					//System.out.println("======\n"+varStr);
				}
			}
			else{
				varStr+=" numeric";
			}
			varStr+="\n";
		}
		return varStr;
	}

	public static void	main(String[] args) throws IOException{
		Attributes as=new Attributes();

		File file = new File("bike-te.train");  
	    Long filelength = file.length();  
	    byte[] filecontent = new byte[filelength.intValue()];
	    BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));
	    fin.read(filecontent);
		String lines=new String(filecontent,"UTF-8");
		
		Scanner sc1 = new Scanner(lines);
		as.parse(sc1);
		System.out.println(as.toString());
		System.out.println(as.attributes.get(4).name);
		System.out.println(as.getIndex(as.attributes.get(4).name));
	} 
}
