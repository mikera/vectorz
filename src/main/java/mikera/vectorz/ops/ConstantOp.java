package mikera.vectorz.ops;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public class ConstantOp extends Op {
	
	private final double value;
	
	public ConstantOp(double value) {
		this.value=value;
	}
	
	@Override
	public double apply(double x) {
		return value;
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

}
