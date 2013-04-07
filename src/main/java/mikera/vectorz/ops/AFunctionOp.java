package mikera.vectorz.ops;

import mikera.vectorz.Op;

public abstract class AFunctionOp extends Op {

	@Override
	public double averageValue() {
		return apply(0);
	}
}
