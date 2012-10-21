package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.ArrayVector;
import mikera.vectorz.Tools;
import mikera.vectorz.Vectorz;

/**
 * Vector referring to an offset into a double[] array
 * 
 * @author Mike
 * 
 */
public final class ArraySubVector extends ArrayVector {
	private static final long serialVersionUID = 1262951505515197105L;

	private final double[] data;

	private final int offset;
	private final int length;

	public ArraySubVector(double[] values) {
		this(values.clone(),0,values.length);
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
	public ArraySubVector(ArrayVector source, int offset, int length) {
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

	public void add(ArrayVector v) {
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
	
	public void addMultiple(ArrayVector v, double factor) {
		int vlength=v.length();
		if (vlength != length) {
			throw new Error("Source vector has different size: " + vlength);
		}
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[offset + i] += vdata[voffset + i]*factor;
		}
	}
	 
	@Override
	public void fill(double value) {
		for (int i = 0; i < length; i++) {
			data[offset + i] += value;
		}
	}
	
	@Override
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i+offset]*=factor;
		}	
	}

	/**
	 * Vector hashcode, designed to match hashcode of Java double array
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < length; i++) {
			hashCode = 31 * hashCode + (Tools.hashCode(data[offset+i]));
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
	public boolean isReference() {
		return true;
	}
	
	@Override
	public AVector clone() {
		return Vectorz.create(this);
	}


}
