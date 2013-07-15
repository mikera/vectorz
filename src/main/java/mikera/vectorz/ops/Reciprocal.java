package mikera.vectorz.ops;

import mikera.vectorz.Op;

public final class Reciprocal extends AFunctionOp {
	@Override
	public double apply(double x) {
		return 1.0/x;
	}

	@Override
	public double derivative(double x) {
		return -1.0/(x*x);
	}

	@Override
	public double derivativeForOutput(double y) {
		return -y*y;
	}

	@Override public double averageValue() {return 1.0;}

	@Override public boolean hasInverse() {return true;}

	@Override public Op getInverse() {return this;}

	@Override public boolean hasDerivative() {return true;}
}