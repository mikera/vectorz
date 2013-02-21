package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class LinearOp extends ALinearOp {
	private final double factor;
	private final double constant;
	
	private LinearOp(double factor, double constant) {
		this.factor=factor;
		this.constant=constant;
	}
	
	public static ALinearOp create(double factor, double constant) {
		if (factor==0.0) {
			return ConstantOp.create(constant);
		}
		if (factor==1.0) {
			if (constant==0.0) return IdentityOp.INSTANCE;
			return OffsetOp.create(constant);
		}
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
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public ALinearOp getInverse() {
		return LinearOp.create(1.0/factor, -constant/factor);
	}
	
	public Op compose(ALinearOp op) {
		return LinearOp.create(factor*op.getFactor(),factor*op.getConstant()+constant);
	}
	
	@Override
	public Op compose(Op op) {
		if (op instanceof ALinearOp) {
			return compose((ALinearOp) op);
		}
		return super.compose(op);
	}
}
