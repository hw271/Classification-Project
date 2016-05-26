import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class KernalSVM {
	DataSet dataset;
	RealVector weight;
	
	public KernalSVM(String filename) throws IOException{
		dataset=new DataSet(filename);
		System.out.println(dataset);
		train();
	}
	
	public double[][] getX(Examples exs){
		int row=exs.size();
		int column=exs.get(0).size();
		double[][] arr=new double[row][column-1]; 
		for(int i=0;i<row;i++){
			for(int j=0;j<column-1;j++){
				arr[i][j]=exs.get(i).get(j);
			}
		}
		return arr;
	}
	
	public double[] getY(Examples exs){
		int row=exs.size();
		int column=exs.get(0).size();
		double[] arr=new double[row]; 
		for(int i=0;i<row;i++){
			arr[i]=exs.get(i).get(column-1);
		}
		return arr;
	}
	
	public RealMatrix getXy(RealMatrix x, RealVector y){
		RealMatrix xy=x.copy();
		for(int i=0;i<xy.getRowDimension();i++){
			for(int j=0;j<xy.getColumnDimension();j++){
				double temp=xy.getEntry(i, j)*y.getEntry(i);
				xy.setEntry(i, j, temp);
			}
		}
		return xy;
	}
	
	public RealMatrix getKernal(RealMatrix x){
		RealMatrix xT=x.transpose();
		RealMatrix xxT=x.multiply(xT);
		return xxT;
	}
	
	public RealMatrix getPolynomialKernal(RealMatrix x, double c, double r){
		RealMatrix xT=x.transpose();
		RealMatrix xxT=x.multiply(xT);
		int row=xxT.getRowDimension();
		int column=xxT.getColumnDimension();
		RealMatrix kernalxxT=MatrixUtils.createRealMatrix(row, column);
		for(int i=0;i<row;i++){
			for(int j=0;j<column;j++){
				double temp=Math.pow((xxT.getEntry(i, j)+c), r);
				kernalxxT.setEntry(i, j, temp);
			}
		}
		return kernalxxT;
	}
	
	public RealVector eliminateLast(RealVector y){
		RealVector b=y.getSubVector(0, y.getDimension()-1);
		if(y.getEntry(y.getDimension()-1)==1){
			b=b.mapMultiply(-1);
		}
		return b;
	}
	
	public RealVector getAlpha(RealMatrix xxT, RealVector b){
		int n=xxT.getRowDimension()-1;
		RealMatrix eX=MatrixUtils.createRealMatrix(n,n);
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				double temp=xxT.getEntry(i, j)+b.getEntry(i)*xxT.getEntry(n, j)+b.getEntry(j)*xxT.getEntry(i, n)+b.getEntry(i)*b.getEntry(j)*xxT.getEntry(n, n);
				eX.setEntry(i, j, temp);
			}
		}
		
		DecompositionSolver solver = new LUDecomposition(eX).getSolver();
		
		RealVector eY=b.mapAdd(1);
		RealVector alpha = solver.solve(eY);
		double lastAlpha=alpha.dotProduct(b);
		alpha=alpha.append(lastAlpha);
		
		return alpha;
	}
	
	public RealVector getWeight(RealMatrix x, RealVector y, RealVector alpha){
		System.out.println(x.toString());
		System.out.println(y.toString());
		System.out.println(alpha.toString());
		
		int column=x.getColumnDimension();
		double[] w=new double[column];
		RealVector weight=MatrixUtils.createRealVector(w);
		
		for(int j=0;j<column;j++){
			double temp=0;
			for(int i=0;i<x.getRowDimension();i++){
				temp+=alpha.getEntry(i)*y.getEntry(i)*x.getEntry(i, j);
			}
			weight.setEntry(j, temp);
		}
		return weight;
	}
	
	public void train(){
		double[][] arrayX=getX(dataset.examples);
		double[] arrayY=getY(dataset.examples);

		RealMatrix x=MatrixUtils.createRealMatrix(arrayX);
		RealVector y=MatrixUtils.createRealVector(arrayY);
		
		RealMatrix xy=getXy(x, y);
		RealMatrix xxT=getPolynomialKernal(xy,1,2);
		RealVector b=eliminateLast(y);

		RealVector alpha=getAlpha(xxT, b);
		RealVector weight=getWeight(x,y,alpha);
		
		System.out.println(alpha.toString());
		System.out.println(weight.toString());
		
	}
	
	public void test(){
		double[][] testx={{1,2},{-1,2},{-1,-2}};
		double[] testy={-1,-1,1};
		
		RealMatrix x=MatrixUtils.createRealMatrix(testx);
		RealVector y=MatrixUtils.createRealVector(testy);
		
		RealMatrix xy=getXy(x, y);
		RealMatrix xxT=getKernal(xy);
		RealVector b=eliminateLast(y);

		RealVector alpha=getAlpha(xxT, b);
		System.out.println(alpha.toString());
		RealVector weight=getWeight(xxT,y,alpha);
		System.out.println(weight.toString());
	}
	

	public int classify(Example ex){
		double result=0;
		for(int i=0;i<ex.size()-1;i++){
			result+=ex.get(i)*weight.getEntry(i);
		}
		if(result>0){
			return 1;
		}
		return -1;
	}
	
	public Performance classify(DataSet test){
		Examples testExamples=test.examples;
		Performance p1=new Performance();
		int misClass=0;
		for(Example ex:testExamples){
			int trueLabel=(int)(double)ex.get(test.attributes.getClassIndex());
			int label=this.classify(ex);
			if(trueLabel!=label){
				misClass++;
			}
		}
		p1.accuracy=1-(double)misClass/test.examples.size();
		return p1;
	}
	
	
	public static void main(String[] args) throws IOException{
		KernalSVM kp1=new KernalSVM("iris.mff");
		
	}
}
