package mikera.vectorz.op;

import mikera.vectorz.AVector;

public interface IUnaryOp {

	public void applyTo(AVector v);
	
	public double apply(double x);
	
	public void applyTo(double[] data, int start, int length);

}
