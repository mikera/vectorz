package mikera.vectorz.ops;

import mikera.vectorz.Op;

public abstract class ABoundedOp extends Op {
	@Override
	public boolean isBounded() {
		return true;
	}
	
	@Override
	public abstract double minValue();
	
	@Override
	public abstract double maxValue();
	
	@Override
	public double averageValue() {
		return (minValue()+maxValue())*0.5;
	}
}
