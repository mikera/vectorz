package mikera.vectorz;

import java.util.Arrays;

import mikera.indexz.Index;


/**
 * General purpose vector of arbitrary length, backed by an internal double[] array
 * 
 * @author Mike
 *
 */
public final class Vector extends ArrayVector {
	private static final long serialVersionUID = 6283741614665875877L;

	public final double[] array;

	Vector(double... values) {
		array = values;
	}
	
	Vector(Object... values) {
		int len=values.length;
		array=new double[len];
		for (int i=0; i<len; i++) {
			array[i]=Tools.toDouble(values[i]);
		}
	}

	Vector(int length) {
		array = new double[length];
	}

	/**
	 * Copy constructor from an arbitrary vector
	 * 
	 * @param source
	 */
	public Vector(AVector source) {
		int length = source.length();
		array = new double[length];
		source.copyTo(this.array, 0);
	}
	
	/**
	 * Wraps a double array into a Vector, does *no defensive copy* so use with caution
	 * @param source
	 * @return
	 */
	public static Vector wrap(double[] source) {
		return new Vector(source);
	}
	
	/**
	 * Create a vector with specific component values. 
	 * Creates a copy of the value array
	 * @param values
	 * @return
	 */
	public static Vector of(double... values) {
		int length = values.length;
		double[] data = new double[length];
		System.arraycopy(values, 0, data, 0, length);
		return new Vector(data);
	}
	
	/**
	 * Create an empty (zero-filled) vector of a specified length
	 * @param length
	 * @return
	 */
	public static Vector createLength(int length) {
		return new Vector(length);
	}
	
	@Override
	public int length() {
		return array.length;
	}

	@Override
	public double get(int i) {
		return array[i];
	}

	@Override
	public void set(int i, double value) {
		array[i]=value;
	}
	
	@Override
	public void set(int offset, double[] data, int dataOffset, int length) {
		System.arraycopy(data, dataOffset, array, offset, length);
	}

	@Override
	public double[] getArray() {
		return array;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}
	
	@Override
	public void applyOp(Op op) {
		op.applyTo(array, 0, array.length);
	}
	
	@Override
	public void fill(double value) {
		Arrays.fill(array, value);
	}
	
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (int i=0; i<array.length; i++) {
			result+=array[i];
		}
		return result;
	}
	
	@Override
	public void add(ArrayVector v, int offset) {
		int vlength=v.length();
		int length=length();
		assert(offset>=0);
		assert(offset+length<=vlength);
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset()+offset;
		for (int i = 0; i < length; i++) {
			array[i] += vdata[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(ArrayVector v, double factor) {
		int length=length();
		assert(length==v.length());
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			array[i] += vdata[voffset + i]*factor;
		}
	}
	
	@Override
	public void add(AVector v) {
		if (v instanceof ArrayVector) {
			add(((ArrayVector)v),0); return;
		}
		int vlength=v.length();
		int length=length();
		assert(length==vlength);
		for (int i = 0; i < length; i++) {
			array[i] += v.get(i);
		}
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		int length=length();
		for (int i=0; i<length; i++) {
			array[i]=(factor*array[i])+constant;
		}
	}

	@Override
	public void add(double constant) {
		int length=length();
		for (int i=0; i<length; i++) {
			array[i]=array[i]+constant;
		}
	}
	
	@Override
	public void addProduct(AVector a, AVector b) {
		if((a instanceof Vector)&&(b instanceof Vector)) {
			addProduct((Vector)a,(Vector)b);
			return;
		}
		super.addProduct(a,b);
	}
	
	public void addProduct(Vector a, Vector b) {
		int length=length();
		assert((a.length()==length)&&(b.length()==length));
		for (int i = 0; i < length; i++) {
			array[i]+=(a.array[i]*b.array[i]);
		}
	}
	
	public void addProduct(Vector a, Vector b, double factor) {
		int length=length();
		assert((a.length()==length)&&(b.length()==length));
		for (int i = 0; i < length; i++) {
			array[i]+=(a.array[i]*b.array[i])*factor;
		}
	}
	
	@Override
	public void addAt(int i, double v) {
		array[i]+=v;
	}
	
	@Override
	public void sub(AVector v) {
		if (v instanceof ArrayVector) {sub(((ArrayVector)v)); return;}
		int length=length();
		assert(length==v.length());
		for (int i = 0; i < length; i++) {
			array[i] -= v.get(i);
		}
	}
	
	@Override
	public double dotProduct(AVector v, Index ix) {
		if ((v instanceof Vector)) return dotProduct((Vector)v,ix);
		int vl=v.length();
		assert(v.length()==ix.length());
		double result=0.0;
		int[] idata=ix.getData();
		for (int i=0; i<vl; i++) {
			result+=array[idata[i]]*v.get(i);
		}
		return result;
	}
	
	public double dotProduct(Vector v, Index ix) {
		int vl=v.length();
		assert(v.length()==ix.length());
		double result=0.0;
		int[] idata=ix.getData();
		for (int i=0; i<vl; i++) {
			result+=array[idata[i]]*v.array[i];
		}
		return result;
	}
	
	@Override
	public double dotProduct(AVector v) {
		if ((v instanceof Vector)) return dotProduct((Vector)v);
		int len=length();
		assert(v.length()==len);
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=array[i]*v.get(i);
		}
		return result;
	}
	
	public double dotProduct(Vector v) {
		int len=length();
		assert(v.length()==len);
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=array[i]*v.array[i];
		}
		return result;
	}
	
	public void sub(ArrayVector v) {
		sub(v,0);
	}
	
	public void sub(ArrayVector v,int offset) {
		int length=length();
		assert(length==v.length());
		double[] vdata=v.getArray();
		int voffset=v.getArrayOffset()+offset;
		for (int i = 0; i < length; i++) {
			array[i] -= vdata[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		if (v instanceof ArrayVector) {addMultiple(((ArrayVector)v),factor); return;}
		int length=length();
		assert(length==v.length());
		for (int i = 0; i < length; i++) {
			array[i] += v.get(i)*factor;
		}
	}
	
	@Override
	public void addWeighted(AVector v, double factor) {
		if (v instanceof ArrayVector) {addWeighted(((ArrayVector)v),factor); return;}
		int length=length();
		assert(length==v.length());
		for (int i = 0; i < length; i++) {
			array[i] = (array[i]*(1.0-factor)) + (v.get(i)*factor);
		}
	}
	
	public void addWeighted(ArrayVector v, double factor) {
		int length=length();
		assert(length==v.length());
		double[] arr=v.getArray();
		int offset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			array[i] = (array[i]*(1.0-factor)) + (arr[i+offset]*factor);
		}
	}
	
	@Override
	public void addMultiple(Vector vector, Index index, double factor) {
		int len=vector.length();
		assert(len==index.length());
		for (int i=0; i<len; i++) {
			int j=index.data[i];
			this.array[j]+=vector.array[i]*factor;
		}
	}
	
	@Override
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			array[i]*=factor;
		}	
	}
	
	@Override
	public void multiply(AVector v) {
		if (v instanceof Vector) {multiply(((Vector)v)); return;}
		int len=length();
		assert(len==v.length());
		for (int i = 0; i < len; i++) {
			set(i,get(i)*v.get(i));
		}	
	}
	
	public void multiply(Vector v) {
		int len=length();
		assert(len==v.length());
		for (int i = 0; i < len; i++) {
			set(i,array[i]*v.array[i]);
		}	
	}
	
	public boolean isView() {
		return false;
	}
	
	@Override
	public Vector clone() {
		return new Vector(this);
	}
	
	@Override 
	public Vector exactClone() {
		return clone();
	}
}
