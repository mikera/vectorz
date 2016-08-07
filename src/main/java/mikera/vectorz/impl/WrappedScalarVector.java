package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.Op;
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
		super(1);
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
	public void multiply(double factor) {
		scalar.multiply(factor);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
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
		checkIndex(i);
		scalar.set(value);
	}
	
	@Override 
	public void applyOp(Op op) {
		scalar.applyOp(op);
	}
	
	@Override
	public double elementSum() {
		return scalar.get();
	}
	
	@Override
	public double elementSquaredSum() {
		double x= scalar.get();
		return x*x;
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
	public void addAt(int i, double v) {
		scalar.add(v);
	}

	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		return data[offset]*scalar.get();
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return (scalar.get()==data[offset]);
	}
}
