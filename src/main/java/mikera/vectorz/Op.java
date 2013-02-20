package mikera.vectorz;

import mikera.transformz.impl.AOpTransform;

public abstract class Op implements IOp {

	@Override
	public void applyTo(AVector v) {
		if (v instanceof ArrayVector) {
			applyTo((ArrayVector)v);
		}
		v.applyOp(this);
	}
	
	public void applyTo(ArrayVector v) {
		applyTo(v.getArray(), v.getArrayOffset(),v.length());
	}

	@Override
	public void applyTo(double[] data, int start, int length) {
		for (int i=0; i<length; i++) {
			data[start+i]=apply(data[start+i]);
		}
	}
	
	public void applyTo(double[] data) {
		applyTo(data,0,data.length);
	}
	
	public AOpTransform getTransform(int dims) {
		return new AOpTransform(this,dims);
	}
	
	public double minValue() {
		return -Double.MAX_VALUE;
	}
	
	public double maxValue() {
		return Double.MAX_VALUE;
	}
	
	public boolean isBounded() {
		return (minValue()>-Double.MAX_VALUE)||(maxValue()<Double.MAX_VALUE);
	}
}
