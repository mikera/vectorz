package mikera.transformz.impl;

import mikera.transformz.ATransform;
import mikera.vectorz.AVector;
import mikera.vectorz.op.AUnaryOp;

public class AOpTransform extends ATransform  {
	private final AUnaryOp op;
	private final int dims;

	public AOpTransform(AUnaryOp op, int dims) {
		this.op=op;
		this.dims=dims;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		assert(source.length()==dims);
		assert(dest.length()==dims);
		for (int i=0; i<dims; i++) {
			dest.set(i, op.apply(source.get(i)));
		}
	}
	
	@Override
	public double calculateElement(int i, AVector inputVector) {
		return op.apply(inputVector.get(i));
	}

	@Override
	public int inputDimensions() {
		return dims;
	}

	@Override
	public int outputDimensions() {
		return dims;
	}

}
