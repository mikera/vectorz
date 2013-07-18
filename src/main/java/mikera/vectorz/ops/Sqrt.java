package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;
import mikera.vectorz.util.DoubleArrays;

public class Sqrt extends AFunctionOp {
	
	public static final Sqrt INSTANCE=new Sqrt();

	private static final Op SQRT_DERIVATIVE_OP = Division.create(Constant.HALF,INSTANCE);

	@Override
	public double apply(double x) {
		return Math.sqrt(x);
	}
	
	@Override
	public void applyTo(INDArray a) {
		a.sqrt();
	}
	
	@Override
	public void applyTo(AVector a) {
		a.sqrt();
	}
	
	@Override
	public void applyTo(double[] data, int offset, int length) {
		DoubleArrays.sqrt(data,offset,length);
	}
	
	@Override
	public double averageValue() {
		return 1.0;
	}
	
	@Override
	public double derivative(double x) {
		return 0.5/Math.sqrt(x);
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 0.5/y;
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public Op getDerivativeOp() {
		return SQRT_DERIVATIVE_OP;
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public Op getInverse() {
		return Ops.SQUARE;
	}
	
	@Override
	public double applyInverse(double y) {
		return y*y;
	}
	
	@Override 
	public double minDomain() {
		return 0.0;
	}
	
	@Override 
	public double minValue() {
		return 0.0;
	}

}
