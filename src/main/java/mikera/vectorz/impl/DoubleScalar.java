package mikera.vectorz.impl;

import mikera.vectorz.AScalar;

public class DoubleScalar extends AScalar {
	public double value;

	public DoubleScalar(double value) {
		this.value=value;
	}
	
	public static AScalar create(double value) {
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
	public void abs() {
		value=Math.abs(value);
	}
	
	@Override
	public void add(double d) {
		value+=d;
	}
	
	@Override
	public void sub(double d) {
		value-=d;
	}
	
	@Override
	public void add(AScalar s) {
		value+=s.get();
	}
	
	@Override 
	public void multiply(double factor) {
		value*=factor;
	}
	
	@Override
	public void negate() {
		value=-value;
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
	public void getElements(double[] dest, int offset) {
		dest[offset]=value;
	}
	
	@Override
	public DoubleScalar exactClone() {
		return clone();
	}


}
