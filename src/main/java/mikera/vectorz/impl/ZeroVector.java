package mikera.vectorz.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised immuatble vector containing nothing but zeros.
 * 
 * @author Mike
 */
public final class ZeroVector extends AComputedVector implements ISparse {
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
		if (v.length()!=length) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));
		return 0.0;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return 0.0;
	}
	
	@Override
	public ZeroVector innerProduct(AMatrix m) {
		if (m.rowCount()!=length) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, m));
		return ZeroVector.create(m.columnCount());
	}

	@Override
	public double get(int i) {
		if (i<0||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return 0.0;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public double unsafeGet(int i) {
		return 0.0;
	}

	@Override
	public void unsafeSet(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
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
	public boolean isZero() {
		return true;
	}
	
	@Override
	public boolean isBoolean() {
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
	
	@Override 
	public AVector join(AVector a) {
		if (a instanceof ZeroVector) {
			return join((ZeroVector)a);
		}
		return super.join(a);
	}
	
	public ZeroVector join(ZeroVector a) {
		return ZeroVector.create(length+a.length);
	}

}
