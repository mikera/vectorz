package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.randomz.Hash;

/**
 * Vector referring to an offset into a double[] array
 * 
 * @author Mike
 * 
 */
public final class ArraySubVector extends AArrayVector {
	private static final long serialVersionUID = 1262951505515197105L;

	private final double[] data;

	private final int offset;
	private final int length;

	public static ArraySubVector wrap(double[] values) {
		return new ArraySubVector(values);
	}
	
	private ArraySubVector(double[] values) {
		this(values,0,values.length);
	}
	
	private ArraySubVector(double[] data, int offset, int length) {
		this.data=data;
		this.offset=offset;
		this.length=length;
	}


	public ArraySubVector(int length) {
		this.length = length;
		offset = 0;
		data = new double[length];
	}

	public ArraySubVector(ArraySubVector source) {
		length = source.length;
		this.offset = 0;
		data = new double[length];
		System.arraycopy(source.data, source.offset, this.data, 0, length);
	}
	
	public static ArraySubVector wrap(double[] data, int offset, int length) {
		return new ArraySubVector(data,offset,length);
	}

	/**
	 * Constructs a vector directly referencing a sub-vector of an existing
	 * array-based Vector
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 */
	public ArraySubVector(AArrayVector source, int offset, int length) {
		int len=source.length();
		if (offset < 0) {
			throw new IndexOutOfBoundsException("Negative offset for Vector: "
					+ offset);
		}
		if (offset + length > len) {
			throw new IndexOutOfBoundsException(
					"Beyond bounds of parent vector with offset: " + offset
							+ " and length: " + length);
		}
		this.length = length;
		this.offset = source.getArrayOffset() + offset;
		this.data = source.getArray();
	}


	@Override
	public int length() {
		return length;
	}

	

	@Override
	public double get(int i) {
		if ((i < 0) || (i >= length))
			throw new IndexOutOfBoundsException("Index: " + i);
		return data[offset + i];
	}

	@Override
	public void set(int i, double value) {
		if ((i < 0) || (i >= length))
			throw new IndexOutOfBoundsException("Index: " + i);
		data[offset + i] = value;
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[offset + i];
	}

	@Override
	public void unsafeSet(int i, double value) {
		data[offset + i] = value;
	}


	@Override
	public void add(AArrayVector v) {
		int vlength=v.length();
		if (vlength != length) {
			throw new Error("Source vector has different size: " + vlength);
		}
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[offset + i] += vdata[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(AArrayVector v, double factor) {
		assert (v.length() == length);
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[offset + i] += vdata[voffset + i]*factor;
		}
	}
	
	@Override
	public void addAt(int i, double v) {
		assert((i>=0)&&(i<length));
		data[i+offset]+=v;
	}

	/**
	 * Vector hashcode, designed to match hashcode of Java double array
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < length; i++) {
			hashCode = 31 * hashCode + (Hash.hashCode(data[offset+i]));
		}
		return hashCode;
	}

	@Override
	public double[] getArray() {
		return data;
	}

	@Override
	public int getArrayOffset() {
		return offset;
	}
	
	@Override
	public boolean isView() {
		return true;
	}

	@Override 
	public ArraySubVector exactClone() {
		return new ArraySubVector(Arrays.copyOfRange(data, offset, offset+length),0,length);
	}
}
