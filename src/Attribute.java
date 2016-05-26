
import java.util.ArrayList;

public class Attribute {
	protected String name;
	protected ArrayList<String> domain=new ArrayList<String>();
	
	//in Attribute.toString(), if the domain is numeric, it needs to print numeric. if not, print the domain.
	//in Example.toString(), it need to know whether the attribute is a numeric attribute.
	public boolean is_numeric=false;
	public Attribute(){
		
	}
	
	public Attribute(String newName){
		name=newName;
	}
	
	public String getName(){
		return name;
	}
	
	public int getSize(){
		return domain.size();
	}
	
	public void setName(String newName){
		name=newName;
	}
	
	
	public String toString(){
		String attribute=name;
		if(this.is_numeric){
			attribute+=" numeric";
		}
		else{
			for(String x:domain){
				attribute+=" "+x;
			}
		}
		return attribute;
	}
	
	
}
