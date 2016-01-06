package mikera.vectorz.ops;

import mikera.vectorz.Op;

/**
 * A class that represents the derivative of another op. Assumes the other op has a derivative
 * @author Mike
 *
 */
public class Derivative extends AFunctionOp {
	private Op op;
	
	public Derivative(Op base) {
		this.op=base;
	}

	@Override
	public double apply(double x) {
		return op.derivative(x);
	}
}
