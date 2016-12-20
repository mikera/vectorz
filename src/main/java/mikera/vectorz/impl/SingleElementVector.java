package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector1;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * A sparse immutable vector that has a only one element that can be non-zero.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class SingleElementVector extends ASingleElementVector {
	final double value;
	
	public SingleElementVector(int componentIndex, int dimensions) {
		this(componentIndex,dimensions,0.0);
	}
	
	public SingleElementVector(int componentIndex, int dimensions, double value) {
		super(componentIndex, dimensions);
		
		if (dimensions<=0) throw new IllegalArgumentException("SingleElementVEctor must have >= 1 dimensions");
		if (componentIndex<0||componentIndex>=dimensions) throw new IllegalArgumentException("Invalid non-zero component index: "+componentIndex);
		
		this.value=value;
	}
	
	public static SingleElementVector create(double val, int i, int len) {
		return new SingleElementVector(i,len,val);
	}
	
	@Override
	public boolean isZero() {
		return value==0.0;
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		if (value==0.0) return true;
		return (start>index)||(start+length<=index);
	}

	@Override
	public double magnitude() {
		return value;
	}
	
	@Override
	public double elementSum() {
		return value;
	}
	
	@Override
	public double elementProduct() {
		return (length>1)?0.0:value;
	}
	
	@Override
	public double elementMax(){
		return (length>1)?Math.max(0.0, value):value;
	}
	
	@Override
	public double elementMin(){
		return (length>1)?Math.min(0.0, value):value;
	}
	
	@Override
	public double elementSquaredSum() {
		return value*value;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}

	@Override
	public double get(int i) {
		if(!((i>=0)&&(i<length))) throw new IndexOutOfBoundsException();
		return (i==index)?value:0.0;
	}

	@Override
	public double unsafeGet(int i) {
		return (i==index)?value:0.0;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		init=op.reduceZeros(init,index);
		init=op.apply(init,value);
		return op.reduceZeros(init, length-index-1);
	}
	
	@Override
	public double reduce(Op2 op) {
		if (index==0) {
			return op.reduceZeros(value, length-1);
		} else {
			double result=op.reduceZeros(index);
			result=op.apply(result,value);
			return op.reduceZeros(result, length-index-1);
		}
	}
	
	@Override
	public AVector applyOpCopy(Op op) {
		if (op.isStochastic()) return super.applyOpCopy(op);
		
		double v=op.apply(0.0);
		if (v==0.0) {
			return SingleElementVector.create(op.apply(value), index, length);
		} 
		return super.applyOpCopy(op);
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		if (index<offset) return;
		if (index>=offset+length) return;
		array[arrayOffset-offset+index]+=value;
	}
	
	@Override
	public void addToArray(double[] array, int offset, int stride) {
		array[offset+index*stride]+=value;
	}
	
	@Override
	public void addMultipleToArray(double factor, int offset, double[] array, int arrayOffset, int length) {
		if (index<offset) return;
		if (index>=offset+length) return;
		array[arrayOffset-offset+index]+=value*factor;
	}
	
	@Override
	public final AScalar slice(int i) {
		if (i==index) return VectorIndexScalar.wrap(this, i);
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return ImmutableScalar.ZERO;
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		int len=checkRange(offset,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		if ((offset>index)||((offset+length)<=index)) {
			return ZeroVector.create(length);
		}
		return SingleElementVector.create(value, index-offset, length);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector a) {
		if (a instanceof ZeroVector) {
			return SingleElementVector.create(value, index, length+a.length());
		}
		return null;
	}
	
	@Override
	public AVector multiplyCopy(double d) {
		return SingleElementVector.create(value*d,index,length);
	}
	
	@Override
	public AVector innerProduct(AMatrix a) {
		return a.getRow(index).multiplyCopy(value);
	}
	
	@Override
	public SingleElementVector exactClone() {
		return new SingleElementVector(index,length,value);
	}
	
	@Override
	public SparseIndexedVector sparseClone() {
		return SparseIndexedVector.create(length, Index.of(index), new double[] {value});
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		if (data[offset+index]!=value) return false;
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
		if (v.unsafeGet(index)!=value) return false;
		return v.isRangeZero(0, index-1)&&(v.isRangeZero(index+1, length-index-1));
	}

	@Override
	public int nonSparseElementCount() {
		return 1;
	}

	@Override
	public AVector nonSparseValues() {
		return Vector1.of(value);
	}

	@Override
	public Index nonSparseIndex() {
		return Index.of(index);
	}
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return (value==0.0)?0.0:elementVisitor.visit(index,value);
	}
	
	@Override
	public int[] nonZeroIndices() {
		if (value==0.0) {
			return IntArrays.EMPTY_INT_ARRAY;
		} else {
			return new int[]{index};
		}
	}

	@Override
	public boolean includesIndex(int i) {
		return (i==index);
	}

	@Override
	public void add(ASparseVector v) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public boolean hasUncountable() {
		return Double.isNaN(value) || Double.isInfinite(value);
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return Math.pow(value, p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return Math.pow(Math.abs(value), p);
    }

	@Override
	public double dotProduct(double[] data, int offset) {
		return value*data[offset+index];
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		return value*data[offset+index*stride];
	}

	@Override
	protected double value() {
		return value;
	}

	@Override
	public double dotProduct(AVector v) {
		return value*v.unsafeGet(index);
	}

}
