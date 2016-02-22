package mikera.vectorz.impl;

import java.nio.DoubleBuffer;
import java.util.Iterator;

import mikera.arrayz.impl.IDense;
import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Immutable array-backed vector. Keeps defensive array copy to ensure immutability.
 * 
 * @author Mike
 *
 */
public class ImmutableVector extends AStridedVector implements IDense {
	private static final long serialVersionUID = -3679147880242779555L;

	public final int offset;
		
	private ImmutableVector(double[] data) {
		this(data,0,data.length);
	}
	
	public static ImmutableVector of(double... data) {
		return wrap(data.clone());
	}
	
	private ImmutableVector(double[] data, int offset, int length) {
		super(length,data);
		this.offset=offset;
	}
	
	public static ImmutableVector create(double[] data) {
		return wrap(DoubleArrays.copyOf(data));
	}
	
	/** 
	 * Create an ImmutableVector with a copy of the elements in the source vector
	 * @param v
	 * @return
	 */
	public static ImmutableVector create(AVector v) {
		double[] data=v.getElements();
		return wrap(data);
	}
	
	/**
	 * Wraps the given double array as an ImmutableVector. Caller should ensure that
	 * the data array is never subsequently modified
	 * @param data
	 * @return
	 */
	public static ImmutableVector wrap(double[] data) {
		return new ImmutableVector(data,0,data.length);
	}
	
	public static ImmutableVector wrap(double[] data, int offset, int length) {
		if ((offset<0)||(length<0)||((offset+length>data.length))) throw new IndexOutOfBoundsException();
		return new ImmutableVector(data,offset,length);
	}
	
	public static ImmutableVector wrap(Vector source) {
		double[] data=source.data;
		return new ImmutableVector(data,0,data.length);
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
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data,offset,length);
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		return DoubleArrays.isZero(data, offset+start, length);
	}
	
	@Override
	public final ImmutableScalar slice(int i) {
		return ImmutableScalar.create(get(i));
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);
		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		return new ImmutableVector(data,offset+start,length);
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data,offset,length());
	}
	
	@Override
	public double[] toDoubleArray() {
		return DoubleArrays.copyOf(data,offset,length);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		System.arraycopy(this.data, this.offset, data, offset, length());
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length) {
		System.arraycopy(data, this.offset+offset, dest, destOffset, length);
	}
	
	@Override
	public void multiplyTo(double[] data, int offset) {
		DoubleArrays.arraymultiply(this.data, this.offset, data,offset,length());
	}
	
	@Override
	public void addToArray(double[] array, int offset) {
		addToArray(0,array,offset,length());
	}

	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		DoubleArrays.add(data, offset+this.offset, array, arrayOffset, length);
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		int dataOffset=this.offset+offset;
		DoubleArrays.addMultiple(array, arrayOffset, data, dataOffset, length, factor);
	}
	
	@Override
	public void divideTo(double[] data, int offset) {
		DoubleArrays.arraydivide(this.data, this.offset, data,offset,length());
	}
	
	@Override public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(this.data, this.offset, data, offset, length());
	}
	
	@Override public double dotProduct(double[] data, int offset, int stride) {
		return DoubleArrays.dotProduct(this.data, this.offset, data, offset, stride, length());
	}
	
	@Override public double dotProduct(AVector v) {
		checkSameLength(v);
		return v.dotProduct(data, offset);
	}
	
	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data, offset, length);
	}
	
	@Override
	public double get(int i) {
		checkIndex(i);
		return data[offset+i];
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public double unsafeGet(int i) {
		return data[offset+i];
	}

	@Override
	public void unsafeSet(int i, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));		
	}
	
	@Override
	public void addAt(int i, double v) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));		
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new StridedElementIterator(data,offset,length,1);
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < length; i++) {
			hashCode = 31 * hashCode + (Hash.hashCode(data[offset+i]));
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v.length()!=length) return false;
		return v.equalsArray(data, offset);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(data,offset,this.data, this.offset,length());
	}
	
	@Override
	public Vector clone() {
		return Vector.wrap(toDoubleArray());
	}
	
	@Override
	public AVector sparse() {
		return SparseImmutableVector.create(this);
	}

	@Override
	public AVector exactClone() {
		return new ImmutableVector(data,offset,length);
	}
	
	@Override
	public AVector immutable() {
		return this;
	}
	
	@Override
	public void validate() {
		if ((offset<0)||(offset+length>data.length)||(length<0)) throw new VectorzException("ImmutableVector data out of bounds");
		super.validate();
	}

	@Override
	protected int index(int i) {
		return offset+i;
	}

	@Override
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int getStride() {
		return 1;
	}

	@Override
	public void set(AVector v) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public void setElements(double[] values, int offset) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));		
	}

	@Override
	public void setElements(int pos, double[] values, int offset, int length) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public void applyOp(Op op) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

}
