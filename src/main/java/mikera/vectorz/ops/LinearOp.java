package mikera.vectorz.ops;

import mikera.transformz.ATransform;
import mikera.transformz.Transformz;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class LinearOp extends Op {
	private final double factor;
	private final double constant;
	
	public LinearOp(double factor, double constant) {
		this.factor=factor;
		this.constant=constant;
	}
	
	public static LinearOp create(double factor, double constant) {
		return new LinearOp(factor,constant);
	}
	
	@Override
	public double apply(double x) {
		return (factor*x)+constant;
	}
	
	@Override
	public double applyInverse(double y) {
		return (y-constant)/factor;
	}
	
	@Override
	public void applyTo(AVector v) {
		v.scaleAdd(factor,constant);
	}
	
	@Override
	public void applyTo(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i]=(data[i]*factor)+constant;
		}
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]=(data[i+start]*factor)+constant;
		}	
		
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		return factor;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return factor;
	}
	
	@Override
	public ATransform getTransform(int dimensions) {
		return Transformz.identityTransform(dimensions);
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public LinearOp getInverse() {
		return LinearOp.create(1.0/factor, -constant/factor);
	}
}
