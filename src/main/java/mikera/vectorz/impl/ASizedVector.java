package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for vectors using a fixed final int size
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ASizedVector extends AVector {
	protected final int length;
	
	protected ASizedVector(int length) {
		this.length=length;
	}
	
	@Override
	public final int length() {
		return length;
	}
	
	@Override
	public final long elementCount() {
		return length();
	}
	
	@Override
	public final int sliceCount() {
		return length;
	}
	
	@Override
	public final double get(long i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return unsafeGet((int)i);
	}
	
	@Override
	public final int[] getShape() {
		return new int[] {length};
	}
	
	@Override
	public final boolean isSameShape(AVector a) {
		return length==a.length();
	}
	
	@Override
	protected final int checkSameLength(AVector v) {
		int len=length;
		if (len!=v.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));		
		return len;
	}
	
	@Override
	public final int checkRange(int offset, int length) {
		int len=this.length;
		int end=offset+length;
		if ((offset<0)||(end>len)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		return len;
	}
	
	@Override
	public final int checkIndex(int i) {
		int len=length;
		if ((i<0)||(i>=len)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return len;
	}
	
	@Override
	public final int checkLength(int length) {
		if (this.length!=length) throw new IllegalArgumentException("Vector length mismatch, expected length = "+length+", but got length = "+this.length);
		return length;
	}
	
	@Override
	protected final int checkSameLength(ASizedVector v) {
		int len=length;
		if (len!=v.length) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));		
		return len;
	}
	
	@Override
	public final int[] getShapeClone() {
		return new int[] {length};
	}
	
	@Override
	public final boolean equalsArray(double[] data) {
		if (length!=data.length) throw new IllegalArgumentException("Wrong size of data array: "+data.length);
		return equalsArray(data,0);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		for (int i=0; i<length; i++) {
			if (unsafeGet(i)!=data[offset++]) return false;
		}
		return true;
	}
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		for (int i=0; i<length; i++) {
			double v=unsafeGet(i);
			if (v!=0.0) v=elementVisitor.visit(i,v);
			if (v!=0.0) return v;
		}
		return 0.0;
	}
}
