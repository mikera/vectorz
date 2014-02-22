package mikera.transformz.impl;

import mikera.transformz.ATransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.ErrorMessages;

/**
 * A transform the represents the application of an operator to all elements of a vector
 * 
 * @author Mike
 *
 */
public class AOpTransform extends ATransform  {
	private final Op op;
	private final int dims;

	public AOpTransform(Op op, int dims) {
		this.op=op;
		this.dims=dims;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		if ((source.length()!=dims)) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if ((dest.length()!=dims)) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(source));
		for (int i=0; i<dims; i++) {
			dest.unsafeSet(i, op.apply(source.unsafeGet(i)));
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
