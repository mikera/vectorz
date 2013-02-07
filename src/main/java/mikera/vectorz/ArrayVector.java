package mikera.vectorz;

import java.util.Arrays;

import mikera.vectorz.impl.ArraySubVector;

/**
 * Base class for vectors backed by a double[] array.
 * 
 * The double array can be directly accessed for performance purposes
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class ArrayVector extends AVector {

	public abstract double[] getArray();
	
	public abstract int getArrayOffset();

	/**
	 * Returns a vector referencing a sub-vector of the current vector
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public ArraySubVector subVector(int offset, int length) {
		int len=this.length();
		if ((offset + length) > len)
			throw new IndexOutOfBoundsException("Upper bound " + len
					+ " breached:" + (offset + length));
		if (offset < 0)
			throw new IndexOutOfBoundsException("Lower bound breached:"
					+ offset);
		return new ArraySubVector(this, offset, length);
	}
	
	@Override
	public void copyTo(double[] data, int offset) {
		System.arraycopy(getArray(), getArrayOffset(), data, offset, length());
	}
	
	@Override
	public void fillRange(int offset, int length, double value) {
		if ((offset<0)||((offset+length)>length())) {
			throw new IndexOutOfBoundsException("Filling range offset="+offset+" length="+length);
		}
		double[] arr=getArray();
		int off=getArrayOffset();
		Arrays.fill(arr, off+offset, off+offset+length, value);
	}
	
	@Override
	public void set(AVector a) {
		assert(a.length()==length());
		a.copyTo(getArray(),getArrayOffset());
	}
	
	@Override
	public void set(AVector a, int offset) {
		assert(offset>=0);
		assert(offset+length()<=a.length());
		a.copy(offset, length(), this, 0);
	}
	
	@Override 
	public void add(AVector src) {
		if (src instanceof ArrayVector) {
			add ((ArrayVector)src,0);
			return;
		}
		int length=length();
		src.addToArray(0,getArray(),getArrayOffset(),length);		
	}
	
	public void add(ArrayVector v) {
		assert(length()==v.length());
		add(v,0);
	}
	
	@Override
	public void add(AVector src, int srcOffset) {
		if (src instanceof ArrayVector) {
			add ((ArrayVector)src,srcOffset);
			return;
		}
		int length=length();
		src.addToArray(srcOffset,getArray(),getArrayOffset(),length);
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		if (v instanceof ArrayVector) {
			addMultiple ((ArrayVector)v,factor);
			return;
		}
		int length=length();
		v.addMultipleToArray(factor,0,getArray(),getArrayOffset(),length);
	}
	
	@Override
	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		int length=length();
		double[] array=getArray();
		int offset=getArrayOffset();
		for (int i=0; i<length; i++) {
			array[i+offset]+=(a.get(i+aOffset)* b.get(i+bOffset)*factor);
		}
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		double[] data=getArray();
		int dataOffset=getArrayOffset()+offset;
		
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=data[i+dataOffset];
		}
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		double[] data=getArray();
		int dataOffset=getArrayOffset()+offset;
		
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*data[i+dataOffset];
		}
	}
	
	public void add(ArrayVector src, int srcOffset) {
		int length=length();
		double[] vdata=src.getArray();
		double[] data=getArray();
		int offset=getArrayOffset();
		int voffset=src.getArrayOffset()+srcOffset;
		for (int i = 0; i < length; i++) {
			data[offset+i] += vdata[voffset + i];
		}
	}
	
	@Override
	public void multiply(AVector v) {
		v.multiplyTo(getArray(), getArrayOffset());
	}
	
	@Override
	public void multiply(double[] data, int offset) {
		int len=length();
		double[] cdata=getArray();
		int coffset=getArrayOffset();
		for (int i = 0; i < len; i++) {
			set(i,cdata[i+coffset]*data[i+offset]);
		}	
	}
	
	@Override
	public void multiplyTo(double[] data, int offset) {
		int len=length();
		double[] cdata=getArray();
		int coffset=getArrayOffset();
		for (int i = 0; i < len; i++) {
			data[i+offset]*=cdata[i+coffset];
		}	
	}
	
	@Override
	public void divide(AVector v) {
		v.divideTo(getArray(), getArrayOffset());
	}
	
	@Override
	public void divide(double[] data, int offset) {
		int len=length();
		double[] cdata=getArray();
		int coffset=getArrayOffset();
		for (int i = 0; i < len; i++) {
			set(i,cdata[i+coffset]/data[i+offset]);
		}	
	}
	
	@Override
	public void divideTo(double[] data, int offset) {
		int len=length();
		double[] cdata=getArray();
		int coffset=getArrayOffset();
		for (int i = 0; i < len; i++) {
			data[i+offset]/=cdata[i+coffset];
		}	
	}
	
	
	@Override
	public void copy(int start, int length, AVector dest, int destOffset) {
		if (dest instanceof ArrayVector) {
			copy(start,length,(ArrayVector)dest,destOffset);
			return;
		}
		double[] src=getArray();
		int off=getArrayOffset();
		for (int i = 0; i < length; i++) {
			dest.set(destOffset+i,src[off+start+i]);
		}
	}
	
	public void copy(int start, int length, ArrayVector dest, int destOffset) {
		double[] src=getArray();
		int off=getArrayOffset();
		double[] dst=dest.getArray();
		System.arraycopy(src, off+start, dst, destOffset, length);
	}
	
	public void addMultiple(ArrayVector v, double factor) {
		int vlength=v.length();
		int length=length();
		if (vlength != length) {
			throw new Error("Source vector has different size: " + vlength);
		}
		double[] data=getArray();
		int offset=getArrayOffset();
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[offset+i] += vdata[voffset + i]*factor;
		}
	}
	
	
	@Override 
	public double magnitudeSquared() {
		int length=length();
		double[] data=getArray();
		int offset=getArrayOffset();
		double result=0.0;
		for (int i=0; i<length; i++) {
			double v=data[offset+i];
			result+=v*v;
		}
		return result;
	}
	
	@Override 
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}
}
