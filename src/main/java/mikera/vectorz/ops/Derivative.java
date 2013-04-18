package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class Derivative extends AFunctionOp {
	private Op op;
	
	public Derivative(Op base) {
		if (!base.hasDerivative()) {
			throw new IllegalArgumentException("Op has no derivative: "+base.getClass());
		}
		this.op=base;
	}

	@Override
	public double apply(double x) {
		return op.derivative(x);
	}
}
