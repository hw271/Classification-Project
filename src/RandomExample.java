
public class RandomExample {
	double random;
	Example example;
	public RandomExample(Example e1){
		this.random=Math.random();
		this.example=e1;
	}
	
	public String toString(){
		String s="{"+random+"->"+example.toString()+"}";
		return s;
	}
}
