package mikera.vectorz;

import mikera.vectorz.Tools;

/**
 * Class representing a fixed-size vector of mutable double values
 * 
 * @author Mike
 * 
 */
public final class Vector extends AbstractVector {
	private final double[] data;

	private final int offset;
	private final int length;

	public Vector(double[] values) {
		offset = 0;
		length = values.length;
		data = new double[length];
		System.arraycopy(values, 0, data, 0, length);
	}

	public Vector(int length) {
		this.length = length;
		offset = 0;
		data = new double[length];
	}

	public Vector(Vector source) {
		length = source.length;
		this.offset = 0;
		data = new double[length];
		System.arraycopy(source.data, source.offset, this.data, 0, length);
	}

	/**
	 * Constructs a vector directly referencing a sub-vector of an existing
	 * Vector
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 */
	private Vector(Vector source, int offset, int length) {
		if (offset < 0) {
			throw new IndexOutOfBoundsException("Negative offset for Vector: "
					+ offset);
		}
		if (offset + length > source.length) {
			throw new IndexOutOfBoundsException(
					"Beyond bounds of parent vector with offset: " + offset
							+ " and length: " + length);
		}
		this.length = length;
		this.offset = source.offset + offset;
		this.data = source.data;
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
	public Vector subVector(int offset, int length) {
		if ((offset + length) > this.length)
			throw new IndexOutOfBoundsException("Upper bound " + this.length
					+ " breached:" + (offset + length));
		if (offset < 0)
			throw new IndexOutOfBoundsException("Lower bound breached:"
					+ offset);
		return new Vector(this, offset, length);
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

	/**
	 * Test for equality on vectors. Returns true iff all values in the vector
	 * are identical
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Vector))
			return false;
		Vector v = (Vector) o;
		if (length != v.length)
			return false;
		if ((data == v.data) && (offset == v.offset))
			return true;
		for (int i = 0; i < length; i++) {
			if (v.data[v.offset + i] != data[offset + i])
				return false;
		}
		return true;
	}

	public void add(Vector v) {
		if (v.length != length) {
			throw new Error("Source vector has different size: " + v.length);
		}
		for (int i = 0; i < length; i++) {
			data[offset + i] += v.data[v.offset + i];
		}
	}
	
	public void addMultiple(Vector v, double factor) {
		if (v.length != length) {
			throw new Error("Source vector has different size: " + v.length);
		}
		for (int i = 0; i < length; i++) {
			data[offset + i] += v.data[v.offset + i]*factor;
		}
	}
	
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
}
