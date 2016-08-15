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
 * Specialised immutable unit axis vector. 
 * 
 * Has a 1.0 in exactly one element, 0.0 everywhere else.
 * 
 * @author Mike
 */
public final class AxisVector extends ASingleElementVector {
	private static final long serialVersionUID = 6767495113060894804L;
	
	private AxisVector(int axisIndex, int length) {
		super(axisIndex,length); 
	}
	
	public static AxisVector create(int axisIndex, int dimensions) {
		if ((axisIndex<0)||(axisIndex>=dimensions)) throw new IllegalArgumentException("Axis out of range");
		return new AxisVector(axisIndex,dimensions);
	}
	
	public int axis() {
		return index;
	}
	
	@Override
	public double magnitude() {
		return 1.0;
	}
	
	@Override
	public double elementSquaredSum() {
		return 1.0;
	}
	
	@Override 
	public double normalise() {
		// nothing to do, already unit length
		return 1.0;
	}
	
	@Override
	public AVector normaliseCopy() {
		return this;
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
		return index;
	}
	
	@Override
	public double maxAbsElement(){
		return 1.0;
	}
	
	@Override
	public int maxAbsElementIndex(){
		return index;
	}
	
	@Override
	public int minElementIndex(){
		if (length==1) return 0;
		return (index==0)?1:0;
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
		return (start>index)||(start+length<=index);
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
		return v.unsafeGet(axis());
	}
	
	@Override 
	public double dotProduct(double[] data, int offset) {
		return data[offset+axis()];
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		return data[offset+index*stride];
	}
	
	
	public double dotProduct(Vector3 v) {
		switch (axis()) {
			case 0: return v.x;
			case 1: return v.y;
			default: return v.z;
		}
	}
	
	public double dotProduct(Vector2 v) {
		switch (axis()) {
			case 0: return v.x;
			default: return v.y;
		}
	}
	
	@Override
	public AVector multiplyCopy(double d) {
		return SingleElementVector.create(d,index,length);
	}
	
	@Override
	public Scalar innerProduct(ADenseArrayVector v) {
		checkSameLength(v);
		return Scalar.create(v.unsafeGet(index));
	}
	
	@Override
	public AVector innerProduct(AMatrix m) {
		checkLength(m.rowCount());
		return m.getRow(index).copy();
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return (i==axis())?1.0:0.0;
	}
	
	@Override
	public double unsafeGet(int i) {
		return (i==axis())?1.0:0.0;
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		if (index<offset) return;
		if (index>=offset+length) return;
		array[arrayOffset-offset+index]+=1.0;
	}
	
	@Override
	public void addToArray(double[] array, int offset, int stride) {
		array[offset+index*stride]+=1.0;
	}
	
	@Override
	public void addMultipleToArray(double factor, int offset, double[] array, int arrayOffset, int length) {
		if (index<offset) return;
		if (index>=offset+length) return;
		array[arrayOffset-offset+index]+=factor;
	}
	
	@Override
	public final ImmutableScalar slice(int i) {
		checkIndex(i);
		if (i==axis()) return ImmutableScalar.ONE;
		return ImmutableScalar.ZERO;
	}
	
	@Override
	public Vector toNormal() {
		return toVector();
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] data=new double[length];
		data[index]=1.0;
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
		if ((start<=axis())&&(end>axis())) {
			return AxisVector.create(axis()-start,length);
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
	public Index nonSparseIndex() {
		return Index.of(axis());
	}
	
	@Override
	public int[] nonZeroIndices() {
		return new int[] {index};
	}
	
	@Override
	public double[] nonZeroValues() {
		return new double[] {1.0};
	}
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return elementVisitor.visit(index,1.0);
	}
	
	@Override
	public void add(ASparseVector v) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public AVector addCopy(AVector v) {
		checkSameLength(v);
		AVector r=v.clone();
		r.addAt(index, 1.0);
		return r;
	}
	
	@Override
	public AVector subCopy(AVector v) {
		checkSameLength(v);
		AVector r=v.negateCopy().mutable();
		r.addAt(index, 1.0);
		return r;
	}

	@Override
	public boolean includesIndex(int i) {
		return (i==axis());
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		if (data[offset+index]!=1.0) return false;
		for (int i=0; i<index; i++) {
			if (data[offset+i]!=0.0) return false;
		}
		for (int i=index+1; i<length; i++) {
			if (data[offset+i]!=0.0) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v==this) return true;
		if (length!=v.length()) return false;
		if (v.unsafeGet(index)!=1.0) return false;
		return v.isRangeZero(0, index-1)&&(v.isRangeZero(index+1, length-index-1));
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
	public SparseIndexedVector sparseClone() {
		return SparseIndexedVector.create(length, Index.of(index), new double[] {1.0});
	}
	
	@Override
	public void validate() {
		if (length<=0) throw new VectorzException("Axis vector length is too small: "+length);
		if ((axis()<0)||(axis()>=length)) throw new VectorzException("Axis index out of bounds");
		super.validate();
	}

	@Override
	public boolean hasUncountable() {
		return false;
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return 1;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }

	@Override
	protected double value() {
		return 1.0;
	}
}
