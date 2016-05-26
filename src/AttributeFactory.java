

import java.util.Scanner;

public class AttributeFactory {
	public AttributeFactory(){
		
	}
	/**
	 * input Scanner sc=new Scanner("@attribute make m1 m2 m3 ...");
	 * output an Attribute
	 * */
	public Attribute make(Scanner scanner){
		Attribute newAttribute=new Attribute();
		String temp;
		temp=scanner.next();
		if(temp.equals("@attribute")){
			newAttribute.name=scanner.next();
			temp=scanner.next();
			if(temp.equals("numeric")){
				newAttribute.domain=null;
				newAttribute.is_numeric=true;
			}
			else{
				newAttribute.domain.add(temp);
				while(scanner.hasNext()){
					newAttribute.domain.add(scanner.next());
				}
				//System.out.println(newAttribute.domain.toString());
			}
		}
		else{
			System.out.println("it is not an attribute!");
		}
		return newAttribute; 
	}
}
