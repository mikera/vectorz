package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.DoubleArrays;

public final class Linear extends ALinearOp {
	private final double factor;
	private final double constant;
	
	private Linear(double factor, double constant) {
		this.factor=factor;
		this.constant=constant;
	}
	
	public static ALinearOp create(double factor, double constant) {
		if (factor==0.0) {
			return Constant.create(constant);
		}
		if (factor==1.0) {
			if (constant==0.0) return Identity.INSTANCE;
			return Offset.create(constant);
		}
		if ((factor==-1.0)&& (constant==0.0)) {
			// TODO: special negate class?
		}
		return new Linear(factor,constant);
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
	public void applyTo(INDArray v) {
		v.scaleAdd(factor,constant);
	}
	
	@Override
	public void applyTo(AVector v) {
		v.scaleAdd(factor,constant);
	}
	
	@Override
	public void applyTo(double[] data) {
		DoubleArrays.scaleAdd(data, factor, constant);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		DoubleArrays.scaleAdd(data, start, length, factor,constant);
	}
	
	@Override
	public double getFactor() {
		return factor;
	}
	
	@Override
	public double getConstant() {
		return constant;
	}
	
	@Override
	public double averageValue() {
		return constant;
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
	public Op getDerivativeOp() {
		return Constant.create(getFactor());
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public ALinearOp getInverse() {
		return Linear.create(1.0/factor, -constant/factor);
	}
	
	public Op compose(ALinearOp op) {
		return Linear.create(factor*op.getFactor(),factor*op.getConstant()+constant);
	}
	
	@Override
	public Op compose(Op op) {
		if (op instanceof ALinearOp) {
			return compose((ALinearOp) op);
		}
		return super.compose(op);
	}
}
