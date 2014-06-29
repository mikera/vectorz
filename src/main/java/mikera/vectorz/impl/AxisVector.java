package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Specialised immutable unit axis vector. Has a 1.0 in one element, 0.0 everywhere else.
 * 
 * @author Mike
 */
public final class AxisVector extends ASparseVector {
	private static final long serialVersionUID = 6767495113060894804L;
	
	private final int axis;
	
	private AxisVector(int axisIndex, int length) {
		super(length);
		this.axis=axisIndex;
	}
	
	public static AxisVector create(int axisIndex, int dimensions) {
		if ((axisIndex<0)||(axisIndex>=dimensions)) throw new IllegalArgumentException("Axis out of range");
		return new AxisVector(axisIndex,dimensions);
	}
	
	public int getAxis() {
		return axis;
	}
	
	@Override
	public double magnitude() {
		return 1.0;
	}
	
	@Override
	public double magnitudeSquared() {
		return 1.0;
	}
	
	@Override 
	public double normalise() {
		// nothing to do, already unit length
		return 1.0;
	}
	
	@Override 
	public void square() {
		// no effect
	}
	
	@Override
	public AVector squareCopy() {
		return this;
	}
	
	@Override
	public void abs() {
		// no effect
	}
	
	@Override
	public AxisVector absCopy() {
		return this;
	}	
	
	@Override
	public void sqrt() {
		// no effect
	}
	
	@Override
	public AxisVector sqrtCopy() {
		return this;
	}
	
	@Override
	public void signum() {
		// no effect
	}
	
	@Override
	public AxisVector signumCopy() {
		return this;
	}

	
	@Override
	public double elementSum() {
		return 1.0;
	}
	
	@Override
	public double elementProduct() {
		return (length>1)?0.0:1.0;
	}
	
	@Override
	public double elementMax(){
		return 1.0;
	}
	
	@Override
	public double elementMin(){
		return (length>1)?0.0:1.0;
	}
	
	@Override
	public int maxElementIndex(){
		return axis;
	}
	
	@Override
	public double maxAbsElement(){
		return 1.0;
	}
	
	@Override
	public int maxAbsElementIndex(){
		return axis;
	}
	
	@Override
	public int minElementIndex(){
		if (length==1) return 0;
		return (axis==0)?1:0;
	}

	@Override
	public long nonZeroCount() {
		return 1;
	}

	@Override
	public boolean isZero() {
		return false;
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		return (start>axis)||(start+length<=axis);
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isBoolean() {
		return true;
	}
	
	@Override
	public boolean isUnitLengthVector() {
		return true;
	}
	
	@Override 
	public double dotProduct(AVector v) {
		return v.unsafeGet(getAxis());
	}
	
	@Override 
	public double dotProduct(double[] data, int offset) {
		return data[offset+getAxis()];
	}
	
	@Override 
	public double dotProduct(Vector v) {
		assert(length==v.length());
		return v.data[getAxis()];
	}
	
	public double dotProduct(Vector3 v) {
		switch (getAxis()) {
			case 0: return v.x;
			case 1: return v.y;
			default: return v.z;
		}
	}
	
	public double dotProduct(Vector2 v) {
		switch (getAxis()) {
			case 0: return v.x;
			default: return v.y;
		}
	}
	
	@Override
	public AVector innerProduct(double d) {
		return SingleElementVector.create(d,axis,length);
	}
	
	@Override
	public Scalar innerProduct(Vector v) {
		checkSameLength(v);
		return Scalar.create(v.unsafeGet(axis));
	}
	
	@Override
	public Scalar innerProduct(AVector v) {
		checkSameLength(v);
		return Scalar.create(v.unsafeGet(axis));
	}
	
	@Override
	public AVector innerProduct(AMatrix m) {
		checkLength(m.rowCount());
		return m.getRow(axis).copy();
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return (i==getAxis())?1.0:0.0;
	}
	
	@Override
	public double unsafeGet(int i) {
		return (i==getAxis())?1.0:0.0;
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		if (axis<offset) return;
		if (axis>=offset+length) return;
		array[arrayOffset-offset+axis]+=1.0;
	}
	
	@Override
	public void addToArray(double[] array, int offset, int stride) {
		array[offset+axis*stride]+=1.0;
	}
	
	@Override
	public void addMultipleToArray(double factor, int offset, double[] array, int arrayOffset, int length) {
		if (axis<offset) return;
		if (axis>=offset+length) return;
		array[arrayOffset-offset+axis]+=factor;
	}
	
	@Override
	public final ImmutableScalar slice(int i) {
		checkIndex(i);
		if (i==getAxis()) return ImmutableScalar.ONE;
		return ImmutableScalar.ZERO;
	}
	
	@Override
	public Vector toNormal() {
		return toVector();
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] data=new double[length];
		data[axis]=1.0;
		return data;
	}
	
	@Override
	public Vector toVector() {
		return Vector.wrap(toDoubleArray());
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);
		if (length==len) return this;
		if (length==0) return Vector0.INSTANCE;
				
		int end=start+length;
		if ((start<=getAxis())&&(end>getAxis())) {
			return AxisVector.create(getAxis()-start,length);
		} else {
			return ZeroVector.create(length);
		}
	}

	@Override
	public double density() {
		return 1.0/length;
	}
	
	@Override
	public int nonSparseElementCount() {
		return 1;
	}

	private static final ImmutableVector NON_SPARSE=ImmutableVector.of(1.0);
	
	@Override
	public AVector nonSparseValues() {
		return NON_SPARSE;
	}

	@Override
	public Index nonSparseIndexes() {
		return Index.of(getAxis());
	}
	
	@Override
	public int[] nonZeroIndices() {
		return new int[] {axis};
	}
	
	@Override
	public void add(ASparseVector v) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public AVector addCopy(AVector v) {
		checkSameLength(v);
		AVector r=v.clone();
		r.addAt(axis, 1.0);
		return r;
	}
	
	@Override
	public AVector subCopy(AVector v) {
		checkSameLength(v);
		AVector r=v.negateCopy().mutable();
		r.addAt(axis, 1.0);
		return r;
	}

	@Override
	public boolean includesIndex(int i) {
		return (i==getAxis());
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		if (data[offset+axis]!=1.0) return false;
		for (int i=0; i<axis; i++) {
			if (data[offset+i]!=0.0) return false;
		}
		for (int i=axis+1; i<length; i++) {
			if (data[offset+i]!=0.0) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(AVector v) {
		int len=v.length();
		if (len!=length) return false;
		if (v.unsafeGet(axis)!=1.0) return false;
		if (!v.isRangeZero(0,axis)) return false;
		if (!v.isRangeZero(axis+1,length-axis-1)) return false;
		return true;
	}
	
	@Override
	public boolean elementsEqual(double value) {
		return (value==1.0)&&(length==1);
	}

	@Override
	public AxisVector exactClone() {
		// immutable, so return self
		return this;
	}
	
	@Override
	public void validate() {
		if (length<=0) throw new VectorzException("Axis vector length is too small: "+length);
		if ((getAxis()<0)||(getAxis()>=length)) throw new VectorzException("Axis index out of bounds");
		super.validate();
	}

	@Override
	public boolean hasUncountable() {
		return false;
	}
	
}
