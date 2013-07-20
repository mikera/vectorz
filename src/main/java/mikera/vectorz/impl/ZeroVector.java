package mikera.vectorz.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;

/**
 * Specialised immuatble vector containing nothing but zeros.
 * 
 * @author Mike
 */
public final class ZeroVector extends ComputedVector implements ISparse {
	private static final long serialVersionUID = -7928191943246067239L;
	
	private int length;
	
	public ZeroVector(int dimensions) {
		length=dimensions;
	}
	
	public static ZeroVector create(int dimensions) {
		return new ZeroVector(dimensions);
	}

	@Override
	public int length() {
		return length;
	}
	
	@Override
	public double dotProduct(AVector v) {
		if (v.length()!=length) throw new IllegalArgumentException("Different vector lengths");
		return 0.0;
	}
	
	@Override
	public ZeroVector innerProduct(AMatrix m) {
		if (m.rowCount()!=length) throw new IllegalArgumentException("Incompatible vector*matrix sizes");
		return ZeroVector.create(m.columnCount());
	}

	@Override
	public double get(int i) {
		return 0.0;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException("Cannot set on immutable ZeroVector");
	}
	
	@Override
	public double magnitudeSquared() {
		return 0.0;
	}
	
	@Override
	public double magnitude() {
		return 0.0;
	}
	
	@Override
	public double elementSum() {
		return 0.0;
	}
	
	@Override
	public long nonZeroCount() {
		return 0;
	}

	@Override
	public boolean isZeroVector() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isUnitLengthVector() {
		return false;
	}
	
	@Override
	public int hashCode() {
		return Hash.zeroVectorHash(length);
	}
	
	@Override
	public ZeroVector exactClone() {
		return new ZeroVector(length);
	}
	
	@Override
	public double density() {
		return 0.0;
	}

}
