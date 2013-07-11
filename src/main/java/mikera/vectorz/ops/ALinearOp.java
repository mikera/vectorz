package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public abstract class ALinearOp extends APolynomialOp {

	public abstract double getFactor();
	
	public abstract double getConstant();
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public void applyTo(INDArray a) {
		a.scaleAdd(getFactor(), getConstant());
	}
	
	@Override
	public void applyTo(AVector v) {
		v.scaleAdd(getFactor(), getConstant());
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
		return Constant.create(getFactor());
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
	
	public Op sum(ALinearOp op) {
		return Linear.create(getFactor()+op.getFactor(), getConstant()+op.getConstant());
	}
	
	@Override
	public Op sum(Op op) {
		if ((getFactor()==0.0)&&(getConstant()==0.0)) {
			return op;
		}
		return super.sum(op);
	}
}
