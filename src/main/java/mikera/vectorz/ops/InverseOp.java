package mikera.vectorz.ops;

import mikera.vectorz.Op;

public class InverseOp extends Op {
	private final Op op;
	
	public InverseOp(Op op) {
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

}
