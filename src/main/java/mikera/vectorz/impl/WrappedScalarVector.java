package mikera.vectorz.impl;

import mikera.vectorz.AScalar;

/**
 * A length 1 vector, as a view wrapping a single AScalar
 * 
 * Main purpose is to provide an efficient view for AScalar.asVector()
 * 
 * @author Mike
 */
public class WrappedScalarVector extends AWrappedVector<AScalar> {
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
		if (i!=0) throw new IndexOutOfBoundsException("Index: "+i);
		return scalar.get();
	}
	
	@Override
	public double unsafeGet(int i) {
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

	@Override
	public AScalar getWrappedObject() {
		return scalar;
	}
}
