import java.util.ArrayList;


public class test1 {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Integer> arr=new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			arr.add(0);
		}
		for(int i=0;i<1000000;i++){
			double random = Math.random();
			int index=(((int)(random*10)));
			arr.set(index, arr.get(index)+1);
		}
		System.out.println(arr);
	}

}
