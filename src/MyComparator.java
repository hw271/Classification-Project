import java.util.Comparator;


public class MyComparator implements Comparator<DistanceExample>{

	@Override
	public int compare(DistanceExample e1, DistanceExample e2) {
		return (int)(-e1.distance+e2.distance);
	}
	

} 