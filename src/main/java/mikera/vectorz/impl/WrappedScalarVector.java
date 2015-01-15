package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.util.ErrorMessages;

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
	public boolean isMutable() {
		return scalar.isMutable();
	}
	
	@Override
	public boolean isFullyMutable() {
		return scalar.isFullyMutable();
	}
	
	@Override
	public int componentCount() {
		return 1;
	}
	
	@Override
	public AScalar getComponent(int k) {
		if (k!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidComponent(this,k));
		return scalar;
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
	public boolean isBoolean() {
		return scalar.isBoolean();
	}
	
	@Override
	public boolean isZero() {
		return scalar.isZero();
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
	public double elementSum() {
		return scalar.get();
	}
	
	@Override
	public double elementMax(){
		return scalar.get();
	}
	
	@Override
	public double elementMin(){
		return scalar.get();
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return data[offset]*scalar.get();
	}
	
	@Override
	public WrappedScalarVector exactClone() {
		return new WrappedScalarVector(scalar.exactClone());
	}

	@Override
	public AScalar getWrappedObject() {
		return scalar;
	}

	@Override
	public void addAt(int i, double v) {
		scalar.add(v);
	}
}
