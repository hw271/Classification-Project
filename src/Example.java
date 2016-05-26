

import java.util.ArrayList;
import java.util.Scanner;

public class Example extends ArrayList<Double>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Example(int n){}
	

	public Example(Attributes attr, Scanner sc) {
		// TODO Auto-generated method stub
		String temp;
		int i=0;
		//System.out.println("construct example:");
		while(sc.hasNext()){
			temp=sc.next().toLowerCase();
			//System.out.print(temp+" ");
			if(attr.get(i).is_numeric){
				this.add(Double.valueOf(temp));
			}
			else if(attr.get(i).domain.contains(temp)){
				this.add((double)attr.get(i).domain.indexOf(temp));
			}
			i++;
		}
	}
	
	public String toString(Attributes attrs){
		String str="";
		int i=0;
			for(Double x:this){
				
				int index=x.intValue();
				String value="";
				//System.out.println(attrs);
				//System.out.println("i="+i);
				if(!attrs.get(i).is_numeric){
						value=attrs.get(i).domain.get(index);
				}
				else if(attrs.get(i).is_numeric){
						value=x.toString();
				}
				str+=value+" ";
				i++;
			}
			str.substring(0, str.length()-1);
			str+="\n";
		return str;
	}
	
}
