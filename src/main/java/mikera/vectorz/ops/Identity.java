package mikera.vectorz.ops;

import mikera.transformz.ATransform;
import mikera.transformz.Transformz;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;

/**
 * Singleton identity operator
 * 
 * @author Mike
 */
public final class Identity extends ALinearOp {
	
	public static final Identity INSTANCE = new Identity();

	private Identity() {
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
	public Op getDerivativeOp() {
		return Constant.ONE;
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
	public Identity getInverse() {
		return this;
	}
	
	@Override
	public Op compose(Op op) {
		return op;
	}
	
	@Override
	public Op product(Op op) {
		if (op instanceof Identity) return Ops.SQUARE;
		if (op instanceof Linear) {
			Linear o=(Linear)op;
			return Quadratic.create(o.getFactor(), o.getConstant(), 0.0);
		}
		if (op instanceof Power) return Power.create(((Power)op).getExponent()+1.0);
		return super.product(op);
	}
}
