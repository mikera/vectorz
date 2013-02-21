package mikera.vectorz.ops;

import mikera.vectorz.Op;

public abstract class ABoundedOp extends Op {
	public boolean isBounded() {
		return true;
	}
	
	public abstract double minValue();
	
	public abstract double maxValue();
}
