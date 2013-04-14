package mikera.vectorz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.AScalar;

public class DoubleScalar extends AScalar {
	public double value;

	public DoubleScalar(double value) {
		this.value=value;
	}
	
	public static INDArray create(double value) {
		return new DoubleScalar(value);
	}	

	@Override
	public double get() {
		return value;
	}	
	
	@Override
	public void set(double value) {
		this.value=value;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public DoubleScalar clone() {
		return new DoubleScalar(value);
	}
	
	@Override
	public DoubleScalar exactClone() {
		return clone();
	}


}
