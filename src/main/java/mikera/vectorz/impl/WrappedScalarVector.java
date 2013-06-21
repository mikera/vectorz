package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;

/**
 * A length 1 vector, as a view wrapping a single AScalar
 * @author Mike
 *
 */
public class WrappedScalarVector extends AVector {
	private static final long serialVersionUID = 1912695454407729415L;
	
	public final AScalar scalar;
	
	public WrappedScalarVector(AScalar s) {
		this.scalar=s;
	}

	@Override
	public int length() {
		return 1;
	}

	@Override
	public double get(int i) {
		assert(i==0);
		return scalar.get();
	}

	@Override
	public void set(int i, double value) {
		if (i!=0) throw new IndexOutOfBoundsException();
		scalar.set(value);
	}
	
	@Override
	public WrappedScalarVector exactClone() {
		return new WrappedScalarVector(scalar.exactClone());
	}
}
