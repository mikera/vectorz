package mikera.vectorz.ops;

import mikera.vectorz.Op;

public abstract class ALinearOp extends Op {

	public abstract double getFactor();
	
	public abstract double getConstant();
}
