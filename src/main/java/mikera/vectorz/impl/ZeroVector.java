package mikera.vectorz.impl;

import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Iterator;

import mikera.arrayz.INDArray;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.Constants;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Specialised immutable vector containing nothing but zeros.
 * 
 * This enables significant optimisations on operations involving zeros and
 * composite arrays that have zero areas.
 * 
 * Must have length 1 or more: use Vector0 instead for immutable length 0
 * vectors.
 * 
 * @author Mike
 */
public final class ZeroVector extends ASparseVector {
	private static final long serialVersionUID = -7928191943246067239L;

	private static final int ZERO_VECTOR_CACHE_SIZE = 30;
	private static final ZeroVector[] ZERO_VECTORS = new ZeroVector[ZERO_VECTOR_CACHE_SIZE];

	private static ZeroVector last = new ZeroVector(ZERO_VECTOR_CACHE_SIZE);

	static {
		for (int i = 1; i < ZERO_VECTOR_CACHE_SIZE; i++) {
			ZERO_VECTORS[i] = new ZeroVector(i);
		}
	}

	private ZeroVector(int dimensions) {
		super(dimensions);
	}

	/**
	 * Create a ZeroVector with the specified number of dimensions
	 * 
	 * @param dimensions
	 * @return
	 */
	public static ZeroVector create(int dimensions) {
		return createCached(dimensions);
	}

	public static ZeroVector createNew(int dimensions) {
		if (dimensions <= 0)
			throw new IllegalArgumentException("Can't create length "
					+ dimensions + " ZeroVector. Use Vector0 instead");
		return new ZeroVector(dimensions);
	}

	public static ZeroVector createCached(int dimensions) {
		if (dimensions <= 0)
			throw new IllegalArgumentException("Can't create length "
					+ dimensions + " ZeroVector. Use Vector0 instead");
		ZeroVector zv = tryCreate(dimensions);
		if (zv != null) return zv;
		zv = new ZeroVector(dimensions);
		last = zv;
		return zv;
	}

	/**
	 * Creates a ZeroVector with the same number of elements as the given array.
	 * 
	 * @param arraySize
	 * @return
	 */
	public static ZeroVector create(INDArray array) {
		int n = Vectorz.safeLongToInt(array.elementCount());
		return ZeroVector.create(n);
	}

	private static ZeroVector tryCreate(int dimensions) {
		if (dimensions < ZERO_VECTOR_CACHE_SIZE) { return ZERO_VECTORS[dimensions]; }
		if (dimensions == last.length) return last;
		return null;
	}

	@Override
	public double dotProduct(AVector v) {
		checkSameLength(v);
		return 0.0;
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		return 0.0;
	}

	@Override
	public AVector innerProduct(AMatrix m) {
		checkLength(m.rowCount());
		return ZeroVector.create(m.columnCount());
	}

	@Override
	public Scalar innerProduct(ADenseArrayVector v) {
		checkSameLength(v);
		return Scalar.create(0.0);
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return 0.0;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public void setSparse(double value) {
		// OK
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
	public void addSparse(AVector v) {
		// nothing to do
	}

	@Override
	public AVector addCopy(AVector a) {
		checkSameLength(a);
		return a.copy();
	}
	
	@Override
	public AVector addCopy(double a) {
		return Vectorz.createRepeatedElement(length, a);
	}

	@Override
	public AVector subCopy(AVector a) {
		checkSameLength(a);
		return a.negateCopy();
	}

	@Override
	public RepeatedElementVector reciprocalCopy() {
		return RepeatedElementVector.create(length, 1.0 / 0.0);
	}

	@Override
	public ZeroVector absCopy() {
		return this;
	}

    @Override
    public void multiply(AVector a) {
        throw new UnsupportedOperationException(ErrorMessages.immutable(this));
    }

    @Override
    public void multiply(double factor) {
        throw new UnsupportedOperationException(ErrorMessages.immutable(this));
    }
    
	@Override
	public ZeroVector multiplyCopy(AVector a) {
		checkSameLength(a);
		return this;
	}

	@Override
	public ZeroVector multiplyCopy(double factor) {
		// We currently avoid handling special case of Double.NaN
		// if (factor==Double.NaN) 
		// return Vectorz.createRepeatedElement(length, factor*0.0);
		return this;
	}
	
	@Override
	public AVector normaliseCopy() {
		return this;
	}

	@Override
	public ZeroVector divideCopy(AVector a) {
		checkSameLength(a);
		return this;
	}

	@Override
	public double elementSquaredSum() {
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
	public double elementMax() {
		return 0.0;
	}

	@Override
	public double elementMin() {
		return 0.0;
	}

	@Override
	public int maxElementIndex() {
		return 0;
	}

	@Override
	public double maxAbsElement() {
		return 0.0;
	}

	@Override
	public int maxAbsElementIndex() {
		return 0;
	}

	@Override
	public int minElementIndex() {
		return 0;
	}

	@Override
	public long nonZeroCount() {
		return 0;
	}
	
	@Override
	public double[] nonZeroValues() {
		return DoubleArrays.EMPTY;
	}

	@Override
	public boolean isZero() {
		return true;
	}

	@Override
	public boolean isRangeZero(int start, int length) {
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
	public double reduce(Op2 op, double init) {
		return op.reduceZeros(init,length);
	}
	
	@Override
	public double reduce(Op2 op) {
		return op.reduceZeros(length);
	}
	
	@Override
	public AVector applyOpCopy(Op op) {
		if (op.isStochastic()) return super.applyOpCopy(op);
		
		double v=op.apply(0.0);
		if (v==0.0) {
			return this;
		} else {
			return RepeatedElementVector.create(length, v);
		}
	}

	@Override
	public void addToArray(int offset, double[] array, int arrayOffset,
			int length) {
		// do nothing!
	}

	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length,
			int stride) {
		for (int i = 0; i < length; i++) {
			dest[destOffset + i * stride] = 0.0;
		}
	}

	@Override
	public void addToArray(double[] dest, int offset, int stride) {
		// do nothing!
	}

	@Override
	public void addMultipleToArray(double factor, int offset, double[] array,
			int arrayOffset, int length) {
		// do nothing!
	}

	@Override
	public final ImmutableScalar slice(int i) {
		checkIndex(i);
		return ImmutableScalar.ZERO;
	}

	@Override
	public Iterator<Double> iterator() {
		return new RepeatedElementIterator(length, Constants.ZERO_DOUBLE);
	}

	@Override
	public double density() {
		return 0.0;
	}

	@Override
	public AVector subVector(int offset, int length) {
		int len = checkRange(offset, length);
		if (length == 0) return Vector0.INSTANCE;
		if (length == len) return this;
		return ZeroVector.create(length);
	}

	public ZeroVector join(ZeroVector a) {
		return ZeroVector.create(length + a.length);
	}

	@Override
	public AVector tryEfficientJoin(AVector a) {
		if (a instanceof ZeroVector) {
			return join((ZeroVector) a);
		} else if (a instanceof AxisVector) {
			AxisVector av = (AxisVector) a;
			return AxisVector.create(av.axis() + length, av.length()
					+ length);
		} else if (a instanceof SingleElementVector) {
			SingleElementVector sev = (SingleElementVector) a;
			return SingleElementVector.create(sev.value, length + sev.index,
					sev.length + length);
		}
		return null;
	}

	@Override
	public AVector select(int... order) {
		for (int i: order) {
			checkIndex(i);
		}
		int n = order.length;
		if (n == length) return this;
		return createNew(n);
	}

	/**
	 * readResolve method to ensure we always use the singleton
	 */
	private Object readResolve() throws ObjectStreamException {
		ZeroVector zv = tryCreate(length);
		if (zv != null) return zv;
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
	public Index nonSparseIndex() {
		return Index.EMPTY;
	}

	@Override
	public int[] nonZeroIndices() {
		return IntArrays.EMPTY_INT_ARRAY;
	}

	@Override
	public AVector squareCopy() {
		return this;
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
	public void getElements(double[] dest, int offset) {
		Arrays.fill(dest, offset, offset+length(), 0.0);
	}
	
	@Override
	public void getElements(Object[] dest, int offset) {
		int n=length();
		for (int i=0; i<n; i++) {
			dest[offset+i]=Constants.ZERO_DOUBLE;
		}
	}

	@Override
	public AVector selectClone(int... inds) {
		return Vectorz.newVector(inds.length);
	}

	@Override
	public AVector selectView(int... inds) {
		return Vectorz.createZeroVector(inds.length);
	}

	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (!isSameShape(v)) return false;
		return v.isZero();
	}

	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.isZero(data, offset, length);
	}

	@Override
	public boolean elementsEqual(double value) {
		return value == 0.0;
	}

	@Override
	public boolean hasUncountable() {
		return false;
	}

	@Override
	public double elementPowSum(double p) {
		return 0.0;
	}

	@Override
	public double elementAbsPowSum(double p) {
		return 0.0;
	}

	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		return 0.0;
	}

	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		// never visits any values
		return 0.0;
	}
}
