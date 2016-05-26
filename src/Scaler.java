
public class Scaler {
	double min;
	double max;
	Attribute attribute;
	static DataSet train;
	Scaler(Attribute attribute, DataSet train){
		this.attribute=attribute;
		Scaler.train=train;
		if(this.attribute.is_numeric){
			int indexOfAttribute=train.attributes.getIndex(this.attribute.name);
			this.max=Scaler.train.examples.get(0).get(indexOfAttribute);
			this.min=this.max;
			for(Example ex: Scaler.train.examples){
				if(this.max<ex.get(indexOfAttribute)){
					this.max=ex.get(indexOfAttribute);
				}
				if(this.min>ex.get(indexOfAttribute)){
					this.min=ex.get(indexOfAttribute);
				}
			}
		}
		else{
			System.out.println("this is not a numeric attribute");
		}
	}
	
	public double scale(double x){
		//overflow or underflow
		if(x>this.max) return 1;
		if(x<this.min) return 0;
		
		return (x-this.min)/(this.max-this.min);
	}
}
