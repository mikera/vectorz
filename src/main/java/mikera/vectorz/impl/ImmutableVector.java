package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;

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
	public double get(int i) {
		return data[offset+i];
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException("not mutable");
	}

	@Override
	public double unsafeGet(int i) {
		return data[offset+i];
	}

	@Override
	public void unsafeSet(int i, double value) {
		throw new UnsupportedOperationException("not mutable");
		
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public AVector exactClone() {
		return new ImmutableVector(data,offset,length);
	}

}
