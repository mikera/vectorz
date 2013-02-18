package mikera.vectorz.impl;

import mikera.vectorz.AScalar;

public class DoubleScalar extends AScalar {
	public double value;

	public DoubleScalar(double value) {
		this.value=value;
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
	
	
}
