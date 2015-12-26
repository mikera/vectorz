package mikera.vectorz.ops;

import java.util.Arrays;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class Constant extends ALinearOp {
	
	public final double value;
	
	public static final Constant ZERO=new Constant(0.0);
	public static final Constant HALF=new Constant(0.5);
	public static final Constant ONE=new Constant(1.0);
	public static final Constant TWO=new Constant(2.0);
	
	private Constant(double value) {
		this.value=value;
	}
	
	public static final Constant create(double constant) {
		if (constant==0) return ZERO;
		return new Constant(constant);
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
	public void applyTo(INDArray v) {
		v.fill(value);
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
	
	@Override
	public Op getDerivativeOp() {
		return Constant.ZERO;
	}
	
	@Override
	public Op compose(Op op) {
		return this;
	}
	
	@Override
	public Op product(Op op) {
		if (value==0) return Constant.ZERO;
		return Linear.create(value, 0.0).compose(op);
	}
}
