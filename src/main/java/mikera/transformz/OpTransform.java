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
}
