package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.DoubleArrays;

public final class Square extends APolynomialOp {
	public static final Square INSTANCE=new Square();
	
	private Square() {
	}
	
	public static Op create() {
		return INSTANCE;
	}
	
	@Override
	public final double apply(double x) {
		return x*x;
	}
	
	@Override
	public double applyInverse(double y) {
		return Math.sqrt(y);
	}
	
	@Override
	public void applyTo(AVector v) {
		v.square();
	}
	
	@Override
	public void applyTo(double[] data) {
		for (int i=0; i<data.length; i++) {
			double x=data[i];
			data[i]=x*x;
		}
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		DoubleArrays.square(data, start, length);
	}
	
	@Override
	public double averageValue() {
		return 1.0;
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public boolean hasDerivativeForOutput() {
		return false;
	}
	
	@Override
	public double derivative(double x) {
		return 2.0*x;
	}
	
	@Override
	public Op getDerivativeOp() {
		return Linear.create(2.0,0.0);
	}
	
	@Override
	public boolean hasInverse() {
		return false;
	}
	
	public Op compose(ALinearOp op) {
		double a=op.getFactor();
		double b=op.getConstant();
		
		return Quadratic.create(a*a,(2*a*b),b*b);
	}
	
	@Override
	public Op compose(Op op) {
		if (op instanceof ALinearOp) {
			return compose((ALinearOp) op);
		}
		return super.compose(op);
	}
}
