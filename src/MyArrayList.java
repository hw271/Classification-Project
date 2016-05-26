import java.util.ArrayList;


public class MyArrayList extends ArrayList<RandomExample>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void sort(){
		for(int i=0;i<this.size();i++){
			RandomExample min=new RandomExample(null);
			min.random=1;
			int minPos=i;
			for(int j=i;j<this.size();j++){
				if(min.random>this.get(j).random){
					min=this.get(j);
					minPos=j;
				}
			}
			RandomExample temp=this.get(i);
			this.set(i, min);
			this.set(minPos, temp);
		}
	}
}
