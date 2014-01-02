package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised immuatble vector containing nothing but zeros.
 * 
 * @author Mike
 */
public final class ZeroVector extends ASparseVector {
	private static final long serialVersionUID = -7928191943246067239L;
	
	private final int length;
	
	private static final int ZERO_VECTOR_CACHE=30;
	private static final ZeroVector[] ZERO_VECTORS = new ZeroVector[ZERO_VECTOR_CACHE];
	
	static {
		for (int i=0; i<ZERO_VECTOR_CACHE; i++) {
			ZERO_VECTORS[i]=new ZeroVector(i);
		}
	}
	
	private ZeroVector(int dimensions) {
		length=dimensions;
	}
	
	public static ZeroVector create(int dimensions) {
		if (dimensions<0) throw new IllegalArgumentException(ErrorMessages.illegalSize(dimensions));
		if (dimensions<ZERO_VECTOR_CACHE) {
			return ZERO_VECTORS[dimensions];
		}
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
	public AVector innerProduct(AMatrix m) {
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
	public final ImmutableScalar slice(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return ImmutableScalar.ZERO;
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
	public AVector subVector(int offset, int length) {
		if ((offset<0)||(offset+length>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		if (length==this.length) return this;
		return ZeroVector.create(length);
	}
	
	@Override 
	public AVector join(AVector a) {
		if (a instanceof ZeroVector) {
			return join((ZeroVector)a);
		} else if (a instanceof AxisVector) {
			AxisVector av=(AxisVector)a;
			return AxisVector.create(av.getAxis()+length, av.length()+length);
		}
		return super.join(a);
	}
	
	public ZeroVector join(ZeroVector a) {
		if (length==0) return a;
		
		int alen=a.length;
		if (alen==0) return this;
		return ZeroVector.create(length+a.length);
	}

	@Override
	public int nonSparseElementCount() {
		return 0;
	}

	@Override
	public AVector nonSparseValues() {
		return Vector0.INSTANCE;
	}

	@Override
	public Index nonSparseIndexes() {
		return Index.EMPTY;
	}

	@Override
	public boolean includesIndex(int i) {
		return false;
	}

}
