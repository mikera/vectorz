package mikera.vectorz.ops;

import mikera.vectorz.Op;

/**
 * Operator representing the inverse of another operator
 * 
 * @author Mike
 */
public class Inverse extends Op {
	private final Op op;
	
	public Inverse(Op op) {
		this.op=op;
	}

	@Override
	public double apply(double x) {
		return op.applyInverse(x);
	}
	
	@Override
	public double applyInverse(double y) {
		return op.apply(y);
	}
	
	@Override public double minValue() {
		return op.minDomain();
	}
	
	@Override public double maxValue() {
		return op.maxDomain();
	}
	
	@Override public double minDomain() {
		return op.minValue();
	}
	
	@Override public double maxDomain() {
		return op.maxValue();
	}
	
	@Override
	public double derivative(double y) {
		return 1.0/op.derivative(op.applyInverse(y));
	}
	
	@Override 
	public Op getInverse() {
		return op;
	}
	
	@Override
	public double averageValue() {
		return op.applyInverse(op.averageValue());
	}
	
	@Override
	public boolean hasInverse() {
		return true;
	}
	
	@Override
	public boolean hasDerivative() {
		return op.hasDerivative();
	}

}
