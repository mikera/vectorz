package mikera.vectorz.impl;

import java.nio.DoubleBuffer;
import java.util.Iterator;

import mikera.randomz.Hash;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Immutable array-backed vector. Keeps defensive array copy to ensure immutability.
 * 
 * @author Mike
 *
 */
public class ImmutableVector extends AVector {
	private static final AVector EMPTY_IMMUTABLE_VECTOR = new ImmutableVector(DoubleArrays.EMPTY);
	
	private double[] data;
	public int offset;
	public int length;
	
	public boolean isMutable() {
		return false;
	}
	
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	private ImmutableVector(double[] data) {
		this(data,0,data.length);
	}
	
	private ImmutableVector(double[] data, int offset, int length) {
		this.data=data;
		this.length=length;
		this.offset=offset;
	}
	
	public static ImmutableVector create(AVector v) {
		int length=v.length();
		double[] data=new double[length];
		v.getElements(data, 0);
		return new ImmutableVector(data, 0,length);
	}
	
	@Override
	public AVector subVector(int start, int length) {
		if ((start<0)||(start+length>this.length)||(length<0)) throw new IllegalArgumentException("Illegal subvector with arguments start= "+start+", length= "+length);
		if (length==0) return EMPTY_IMMUTABLE_VECTOR;
		if (length==this.length) return this;
		return new ImmutableVector(data,offset+start,length);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(this.data, this.offset, dest, offset, length());
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data,offset,length());
	}
	
	@Override
	public void copyTo(double[] data, int offset) {
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
		if((offset<0)||(offset+length>length())) throw new IndexOutOfBoundsException();
		DoubleArrays.add(data, offset+this.offset, array, arrayOffset, length);
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		int dataOffset=this.offset+offset;
		
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*data[i+dataOffset];
		}
	}
	
	@Override
	public void divideTo(double[] data, int offset) {
		DoubleArrays.arraydivide(this.data, this.offset, data,offset,length());
	}
	
	@Override public double dotProduct(double[] data, int offset) {
		return DoubleArrays.dotProduct(this.data, this.offset, data, offset, length());
	}
	
	@Override public double dotProduct(AVector v) {
		return v.dotProduct(data, offset);
	}
	
	@Override
	public double magnitudeSquared() {
		return DoubleArrays.elementSquaredSum(data, offset, length);
	}
	
	@Override
	public double get(int i) {
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
	public int length() {
		return length;
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
	public Vector clone() {
		Vector v=Vector.createLength(length);
		v.set(this);
		return v;
	}

	@Override
	public AVector exactClone() {
		return new ImmutableVector(data,offset,length);
	}
	
	@Override
	public AVector immutable() {
		return this;
	}

}
