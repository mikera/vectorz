package mikera.vectorz.impl;

import java.io.ObjectStreamException;
import java.util.Iterator;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised immutable vector containing nothing but zeros.
 * 
 * Must have length 1 or more: use Vector0 instead for length 0 vectors.
 * 
 * @author Mike
 */
public final class ZeroVector extends ASparseVector {
	private static final long serialVersionUID = -7928191943246067239L;
	
	private static final int ZERO_VECTOR_CACHE_SIZE=30;
	private static final ZeroVector[] ZERO_VECTORS = new ZeroVector[ZERO_VECTOR_CACHE_SIZE];
	private static final Double ZERO_DOUBLE=0.0;
	
	private static ZeroVector last=new ZeroVector(ZERO_VECTOR_CACHE_SIZE);
	
	static {
		for (int i=1; i<ZERO_VECTOR_CACHE_SIZE; i++) {
			ZERO_VECTORS[i]=new ZeroVector(i);
		}
	}
	
	private ZeroVector(int dimensions) {
		super(dimensions);
	}
	
	public static ZeroVector create(int dimensions) {
		if (dimensions<=0) throw new IllegalArgumentException("Can't create length "+dimensions+" ZeroVector. Use Vector0 instead");
		return new ZeroVector(dimensions);
	}
	
	public static ZeroVector createNew(int dimensions) {
		if (dimensions<=0) throw new IllegalArgumentException("Can't create length "+dimensions+" ZeroVector. Use Vector0 instead");
		return new ZeroVector(dimensions);
	}
	
	public static ZeroVector createCached(int dimensions) {
		if (dimensions<=0) throw new IllegalArgumentException("Can't create length "+dimensions+" ZeroVector. Use Vector0 instead");
		ZeroVector zv=tryCreate(dimensions);
		if (zv!=null) return zv;
		zv= new ZeroVector(dimensions);
		last=zv;
		return zv;
	}
	
	private static ZeroVector tryCreate(int dimensions) {
		if (dimensions<ZERO_VECTOR_CACHE_SIZE) {
			return ZERO_VECTORS[dimensions];
		}
		if (dimensions==last.length) return last;
		return null;
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
	public void add(ASparseVector v) {
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
	public double elementProduct() {
		return 0.0;
	}
	
	@Override
	public double elementMax(){
		return 0.0;
	}
	
	@Override
	public double elementMin(){
		return 0.0;
	}
	
	@Override
	public int maxElementIndex(){
		return 0;
	}
	
	@Override
	public double maxAbsElement(){
		return 0.0;
	}
	
	@Override
	public int maxAbsElementIndex(){
		return 0;
	}
	
	@Override
	public int minElementIndex(){
		return 0;
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
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isUnitLengthVector() {
		return false;
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		// do nothing!
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		// do nothing!
	}
	
	@Override
	public final ImmutableScalar slice(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return ImmutableScalar.ZERO;
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new RepeatedElementIterator(length,ZERO_DOUBLE);
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
		return ZeroVector.create(length+a.length);
	}
	
	/**
	 * readResolve method to ensure we always use the singleton
	 */
	private Object readResolve() throws ObjectStreamException {
		ZeroVector zv=tryCreate(length);
		if (zv!=null) return zv;
		return this;
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
	
	@Override
	public int hashCode() {
		return Hash.zeroVectorHash(length);
	}
	
	@Override
	public ZeroVector exactClone() {
		return new ZeroVector(length);
	}
	
	@Override
	public AVector sparseClone() {
		return Vectorz.createSparseMutable(length);
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[length];
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (v.length()!=length) return false;
		return v.isZero();
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.isZero(data, offset, length);
	}
	
	@Override
	public boolean elementsEqual(double value) {
		return value==0.0;
	}

}
