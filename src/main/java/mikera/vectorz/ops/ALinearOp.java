package mikera.vectorz.ops;

import mikera.vectorz.Op;

public abstract class ALinearOp extends APolynomialOp {

	public abstract double getFactor();
	
	public abstract double getConstant();
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		return getFactor();
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return getFactor();
	}
	
	@Override
	public Op getDerivativeOp() {
		return ConstantOp.create(getFactor());
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public Op compose(Op op) {
		if ((getFactor()==1.0)&&(getConstant()==0.0)) {
			return op;
		}
		return super.compose(op);
	}
}
