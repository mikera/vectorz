package mikera.vectorz.ops;

import mikera.transformz.ATransform;
import mikera.transformz.Transformz;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;

/**
 * Singleton identity operator
 * 
 * @author Mike
 */
public final class IdentityOp extends ALinearOp {
	
	public static final IdentityOp INSTANCE = new IdentityOp();

	private IdentityOp() {
		// no content
	}
	
	@Override
	public double getFactor() {
		return 1.0;
	}
	
	@Override
	public double getConstant() {
		return 0.0;
	}
	
	@Override
	public double apply(double x) {
		return x;
	}
	
	@Override
	public double applyInverse(double y) {
		return y;
	}
	
	@Override
	public void applyTo(AVector v) {
		// done!
	}
	
	@Override
	public void applyTo(double[] data) {
		// done!
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		// done!
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double averageValue() {
		return 0.0;
	}
	
	@Override
	public double derivative(double x) {
		return 1.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 1.0;
	}
	
	@Override
	public ATransform getTransform(int dimensions) {
		return Transformz.identityTransform(dimensions);
	}
	
	@Override
	public IdentityOp getInverse() {
		return this;
	}
	
	@Override
	public Op compose(Op op) {
		return op;
	}
}
