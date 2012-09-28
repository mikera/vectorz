package mikera.vectorz;

import mikera.vectorz.Tools;

/**
 * Class representing a fixed-size vector of mutable double values
 * 
 * @author Mike
 * 
 */
public final class SubVector extends ArrayVector {
	private final double[] data;

	private final int offset;
	private final int length;

	public SubVector(double[] values) {
		offset = 0;
		length = values.length;
		data = new double[length];
		System.arraycopy(values, 0, data, 0, length);
	}

	public SubVector(int length) {
		this.length = length;
		offset = 0;
		data = new double[length];
	}

	public SubVector(SubVector source) {
		length = source.length;
		this.offset = 0;
		data = new double[length];
		System.arraycopy(source.data, source.offset, this.data, 0, length);
	}

	/**
	 * Constructs a vector directly referencing a sub-vector of an existing
	 * array-based Vector
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 */
	SubVector(ArrayVector source, int offset, int length) {
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

	/**
	 * Returns a vector referencing a sub-vector of the current vector
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	public SubVector subVector(int offset, int length) {
		if ((offset + length) > this.length)
			throw new IndexOutOfBoundsException("Upper bound " + this.length
					+ " breached:" + (offset + length));
		if (offset < 0)
			throw new IndexOutOfBoundsException("Lower bound breached:"
					+ offset);
		return new SubVector(this, offset, length);
	}

	@Override
	public double get(int i) {
		if ((i < 0) || (i > length))
			throw new IndexOutOfBoundsException("Index = " + i);
		return data[offset + i];
	}

	@Override
	public void set(int i, double value) {
		if ((i < 0) || (i > length))
			throw new IndexOutOfBoundsException("Index = " + i);
		data[offset + i] = value;
	}


	public void add(SubVector v) {
		if (v.length != length) {
			throw new Error("Source vector has different size: " + v.length);
		}
		for (int i = 0; i < length; i++) {
			data[offset + i] += v.data[v.offset + i];
		}
	}
	
	public void addMultiple(SubVector v, double factor) {
		if (v.length != length) {
			throw new Error("Source vector has different size: " + v.length);
		}
		for (int i = 0; i < length; i++) {
			data[offset + i] += v.data[v.offset + i]*factor;
		}
	}
	 
	@Override
	public void fill(double value) {
		for (int i = 0; i < length; i++) {
			data[offset + i] += value;
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
}
