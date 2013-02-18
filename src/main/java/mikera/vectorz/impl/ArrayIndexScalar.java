package mikera.vectorz.impl;

import mikera.vectorz.AScalar;

public class ArrayIndexScalar extends AScalar {
	final double[] array;
	final int index;
	
	public ArrayIndexScalar(double[] array, int index) {
		this.array=array;
		this.index=index;
	}
	
	@Override
	public double get() {
		return array[index];
	}
	
	@Override
	public void set(double value) {
		array[index]=value;
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	
	@Override
	public DoubleScalar clone() {
		return new DoubleScalar(array[index]);
	}

}
