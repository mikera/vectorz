package mikera.vectorz;

import java.util.Arrays;


/**
 * General purpose vector or arbitrary length, backed by an internal double[] array
 * 
 * @author Mike
 *
 */
public final class Vector extends ArrayVector {
	private static final long serialVersionUID = 6283741614665875877L;

	public final double[] data;

	Vector(double... values) {
		data = values;
	}

	Vector(int length) {
		data = new double[length];
	}

	/**
	 * Copy constructor from an arbitrary vector
	 * 
	 * @param source
	 */
	public Vector(AVector source) {
		int length = source.length();
		data = new double[length];
		source.copyTo(this.data, 0);
	}
	
	/**
	 * Wraps a double array into a Vector, does *no defensive copy* so use with caution
	 * @param source
	 * @return
	 */
	public static Vector wrap(double[] source) {
		return new Vector(source);
	}
	
	public static Vector of(double... values) {
		int length = values.length;
		double[] data = new double[length];
		System.arraycopy(values, 0, data, 0, length);
		return new Vector(data);
	}
	
	public static Vector createLength(int length) {
		return new Vector(length);
	}
	
	@Override
	public int length() {
		return data.length;
	}

	@Override
	public double get(int i) {
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		data[i]=value;
	}

	@Override
	public double[] getArray() {
		return data;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}
	
	@Override
	public void fill(double value) {
		Arrays.fill(data, value);
	}
	
	public void add(ArrayVector v) {
		int vlength=v.length();
		int length=length();
		if (vlength != length) {
			throw new Error("Source vector has different size: " + vlength);
		}
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[i] += vdata[voffset + i];
		}
	}
	
	public void addMultiple(ArrayVector v, double factor) {
		int length=length();
		assert(length==v.length());
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[i] += vdata[voffset + i]*factor;
		}
	}
	
	@Override
	public void add(AVector v) {
		if (v instanceof ArrayVector) {
			add(((ArrayVector)v)); return;
		}
		int vlength=v.length();
		int length=length();
		if (vlength != length) {
			throw new Error("Source vector has different size: " + vlength);
		}
		for (int i = 0; i < length; i++) {
			data[i] += v.get(i);
		}
	}
	
	@Override
	public void sub(AVector v) {
		if (v instanceof ArrayVector) {subtract(((ArrayVector)v)); return;}
		int length=length();
		assert(length==v.length());
		for (int i = 0; i < length; i++) {
			data[i] -= v.get(i);
		}
	}
	
	public void subtract(ArrayVector v) {
		int length=length();
		assert(length==v.length());
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[i] -= vdata[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		if (v instanceof ArrayVector) {addMultiple(((ArrayVector)v),factor); return;}
		int length=length();
		assert(length==v.length());
		for (int i = 0; i < length; i++) {
			data[i] += v.get(i)*factor;
		}
	}
	
	@Override
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i]*=factor;
		}	
	}
	
	public boolean isReference() {
		return false;
	}
	
	@Override
	public AVector clone() {
		return new Vector(this);
	}

}
