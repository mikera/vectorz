package mikera.vectorz.ops;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class ConstantOp extends ALinearOp {
	
	public final double value;
	
	public static final ConstantOp ZERO=new ConstantOp(0.0);
	
	private ConstantOp(double value) {
		this.value=value;
	}
	
	public static final ConstantOp create(double constant) {
		if (constant==0) return ZERO;
		return new ConstantOp(constant);
	}
	
	@Override
	public double apply(double x) {
		return value;
	}
	
	@Override
	public double applyInverse(double x) {
		return Double.NaN;
	}
	
	@Override
	public void applyTo(AVector v) {
		v.fill(value);
	}
	
	@Override
	public void applyTo(double[] data) {
		Arrays.fill(data, value);
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		Arrays.fill(data,start,start+length,value);
	}
	
	@Override
	public double getFactor() {
		return 0.0;
	}
	
	@Override
	public double getConstant() {
		return value;
	}

	@Override
	public double minValue() {
		return value;
	}

	@Override
	public double maxValue() {
		return value;
	}
	
	@Override
	public double averageValue() {
		return value;
	}
	
	@Override
	public double derivative(double x) {
		return 0.0;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return 0.0;
	}
	
	public Op compose(Op op) {
		return this;
	}
}
