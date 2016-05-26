import java.util.ArrayList;
import java.util.PriorityQueue;


public class test {

	/**
	 * @param args
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception {
		MyComparator myComparator=new MyComparator();
		PriorityQueue<DistanceExample> priorityQueue=new PriorityQueue<DistanceExample>(3,myComparator);

		ArrayList<DistanceExample> ade=new ArrayList<DistanceExample>();
		for(int i=100;i>0;i--){
		//for(int i=0;i<10;i++){
			DistanceExample temp=new DistanceExample();
			temp.distance=i;
			ade.add(temp);
		}
		for(int i=0;i<100;i++){
			DistanceExample temp=new DistanceExample();
			temp.distance=i;
			ade.add(temp);
		}
		for(int i=0;i<200;i++){
			DistanceExample de=ade.get(i);
			priorityQueue.add(de);
		}
		PriorityQueue<DistanceExample> priorityQueue1=new PriorityQueue<DistanceExample>(3,myComparator);	
		for(int i=0;i<3;i++){
			priorityQueue1.add(priorityQueue.poll());
		}
		
		for(int i=0;i<3;i++){
			System.out.println(priorityQueue1.poll().distance);
		}
	}

}
