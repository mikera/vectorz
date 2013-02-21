package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class OffsetOp extends ALinearOp {
	private final double constant;
	
	private OffsetOp(double constant) {
		this.constant=constant;
	}
	
	public static OffsetOp create(double offset) {
		return new OffsetOp(offset);
	}
	
	@Override
	public double apply(double x) {
		return x+constant;
	}
	
	@Override
	public double applyInverse(double y) {
		return y-constant;
	}
	
	@Override
	public void applyTo(AVector v) {
		v.add(constant);
	}
	
	@Override
	public void applyTo(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i]+=constant;
		}
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			data[i+start]+=constant;
		}	
		
	}
	
	@Override
	public double getFactor() {
		return 1.0;
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
		return 1.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 1.0;
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public OffsetOp getInverse() {
		return OffsetOp.create(-constant);
	}

	public Op compose(ALinearOp op) {
		return LinearOp.create(op.getFactor(), constant+op.getConstant());
	}
	
	@Override
	public Op compose(Op op) {
		if (op instanceof ALinearOp) {
			return compose((ALinearOp) op);
		}
		return super.compose(op);
	}
}
