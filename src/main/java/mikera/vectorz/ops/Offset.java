package mikera.vectorz.ops;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class Offset extends ALinearOp {
	private final double constant;
	
	private Offset(double constant) {
		this.constant=constant;
	}
	
	public static Offset create(double offset) {
		return new Offset(offset);
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
	public void applyTo(INDArray v) {
		v.add(constant);
	}
	
	@Override
	public void applyTo(AMatrix v) {
		v.add(constant);
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
	public Op getDerivativeOp() {
		return Constant.ONE;
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public Offset getInverse() {
		return Offset.create(-constant);
	}

	public Op compose(ALinearOp op) {
		return Linear.create(op.getFactor(), constant+op.getConstant());
	}
	
	@Override
	public Op compose(Op op) {
		if (op instanceof ALinearOp) {
			return compose((ALinearOp) op);
		}
		return super.compose(op);
	}
}
