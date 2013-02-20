package mikera.vectorz.op;

import mikera.vectorz.AVector;
import mikera.vectorz.ArrayVector;

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
}
