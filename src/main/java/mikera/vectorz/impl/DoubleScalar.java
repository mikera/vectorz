package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;

public class DoubleScalar extends AScalar {
	public double value;

	@Override
	public AVector asVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double get() {
		return value;
	}	
	
	@Override
	public void set(double value) {
		this.value=value;
	}
	
	
}
