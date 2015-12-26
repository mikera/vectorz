package mikera.transformz;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public class OpTransform extends ASizedTransform {
	private final Op op;

	public OpTransform(Op op, int size) {
		super(size);
		this.op=op;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		dest.set(source);
		op.applyTo(dest);
	}
	
	
	@Override 
	public boolean isInvertible() {
		return op.hasInverse();
	}
	
	@Override 
	public OpTransform inverse() {
		Op invOp=op.getInverse();
		if (op==null) throw new UnsupportedOperationException("Operator "+ op + " does not have an inverse");
		return new OpTransform(invOp,size);
	}
}
