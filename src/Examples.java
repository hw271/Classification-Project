

import java.util.ArrayList;
import java.util.Scanner;

public class Examples extends ArrayList<Example>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Attributes attributes;
	
	public Examples(Attributes attributes){
		this.attributes=attributes;
	}
	public Examples(Attributes attributes, Scanner sc){
		this.attributes=attributes;
		sc.useDelimiter("\n");
		boolean is_ex=false;
		while(sc.hasNext()){
			String line=sc.next().toLowerCase();
			
			if(line==null || line.matches("\\s*") || line==" "){
				continue;
			}
			if(is_ex){
				Scanner sc1=new Scanner(line);
				Example ex=new Example(attributes, sc1);
				this.add(ex);	
			}	
			if(line.matches("@examples")){
				is_ex=true;
			}
		}
	}
	
	public String toString(){
		String st="@examples\n";

		if(this.size()!=0){
			//System.out.println("Example #="+this.size());
			for(Example x:this){
				st+=x.toString(attributes);
			}
		}
		else{
			st+="no examples";
		}
		return st;
	}
	
	
	public static void main(String[] args){
		Attributes as=new Attributes();
		
		Scanner sc_at=new Scanner("@attribute make trek bridgestone cannondale nishiki garyfisher\n@attribute tires knobby treads\n@attribute bars straight curved\n@attribute bottles y n\n@attribute weight numeric\n@attribute type mountain hybrid");
		as.parse(sc_at);
		Scanner sc_ex=new Scanner("@examples\nTrek	Knobby	Straight	y	250.3	Mountain\nBridgestone	Treads	Straight	y	200	Hybrid\nCannondale	Knobby	Curved	n	222.9	Mountain\nNishiki	Treads	Curved	y	190.3	Hybrid\nTrek	Treads	Straight	y	196.8	Hybrid");
		Examples exs=new Examples(as,sc_ex);
		System.out.println(exs.toString());
	}
}
