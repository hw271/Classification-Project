
public class Performance {
	double accuracy;
	double dispersion;
	double variance;
	double confidenceInterval;
	public Performance(){
		this(0.0,0.0,0.0,0.0);
	}
	public Performance(double accuracy,double dispersion){
		this(accuracy, dispersion, 0.0, 0.0);
	}
	
	public Performance(double accuracy,double dispersion,double variance){
		this(accuracy,dispersion,variance,0.0);
	}
	
	public Performance(double accuracy,double dispersion,double variance, double confidenceInterval){
		this.accuracy=accuracy;
		this.dispersion=dispersion;
		this.variance=variance;
		this.confidenceInterval=confidenceInterval;
	}
	

	
	public String toString(){
		double left=accuracy-confidenceInterval;
		double right=accuracy+confidenceInterval;
		return "accuracy="+((double)(int)(accuracy*10000))/100+"%-"+((double)(int)(dispersion*10000))/100+"%\nvariance="+variance+"\n95%confidenceInterval={"
				+((double)(int)(left*10000))/100+"%, "+((double)(int)(right*10000))/100+"%}";
	}
}
