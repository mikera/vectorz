package mikera.vectorz;

import java.util.Arrays;

import mikera.indexz.Index;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.VectorzException;


/**
 * General purpose vector of arbitrary length, backed by an internal double[] array
 * 
 * @author Mike
 *
 */
public final class Vector extends AArrayVector {
	private static final long serialVersionUID = 6283741614665875877L;

	public final double[] data;

	Vector(double... values) {
		data = values;
	}
	
	Vector(Object... values) {
		int len=values.length;
		data=new double[len];
		for (int i=0; i<len; i++) {
			data[i]=Tools.toDouble(values[i]);
		}
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
		return Vector.wrap(data);
	}
	
	/**
	 * Create an empty (zero-filled) vector of a specified length
	 * @param length
	 * @return
	 */
	public static Vector createLength(int length) {
		return new Vector(length);
	}

	public static Vector create(AVector a) {
		int n=a.length();
		Vector v=createLength(n);
		a.copyTo(v.data);
		return v;
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
	public double unsafeGet(int i) {
		return data[i];
	}

	@Override
	public void set(int i, double value) {
		data[i]=value;
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[i]=value;
	}
	
	@Override
	public void set(int offset, double[] data, int dataOffset, int length) {
		System.arraycopy(data, dataOffset, this.data, offset, length);
	}
	
	@Override
	public void set(AVector a) {
		if (a instanceof Vector) {
			if (a==this) return;
			Vector v=(Vector)a;
			System.arraycopy(v.data, 0, data, 0, data.length);
		} else {
			super.set(a);
		}
	}

	@Override
	public double[] getArray() {
		return data;
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, data.length);
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}
	
	@Override
	public void applyOp(Op op) {
		op.applyTo(data, 0, data.length);
	}
	
	@Override
	public void fill(double value) {
		Arrays.fill(data, value);
	}
	
	@Override
	public void clamp(double min, double max) {
		DoubleArrays.clamp(data,0,data.length,min,max);
	}
	
	@Override
	public void square() {
		int len=length();
		for (int i=0; i<len; i++) {
			double x=data[i];
			data[i]=x*x;
		}		
	}
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (int i=0; i<data.length; i++) {
			result+=data[i];
		}
		return result;
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data, 0, data.length);
	}	
	
	@Override
	public void signum() {
		for (int i=0; i<data.length; i++) {
			data[i]=Math.signum(data[i]);
		}
	}
	
	@Override
	public void abs() {
		for (int i=0; i<data.length; i++) {
			double val=data[i];
			if (val<0) data[i]=-val;
		}
	}
	
	@Override
	public void add(AArrayVector src, int srcOffset) {
		int length=length();
		assert(srcOffset>=0);
		assert(srcOffset+length<=src.length());
		double[] vdata=src.getArray();
		int voffset=src.getArrayOffset()+srcOffset;
		for (int i = 0; i < length; i++) {
			data[i] += vdata[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(AArrayVector v, double factor) {
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
		if (v instanceof AArrayVector) {
			add(((AArrayVector)v),0); return;
		}
		int length=length();
		if(length!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		for (int i = 0; i < length; i++) {
			data[i] += v.unsafeGet(i);
		}
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		int length=length();
		for (int i=0; i<length; i++) {
			data[i]=(factor*data[i])+constant;
		}
	}

	@Override
	public void add(double constant) {
		DoubleArrays.add(data, 0, data.length, constant);
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
			data[i]+=(a.data[i]*b.data[i]);
		}
	}
	
	public void addProduct(Vector a, Vector b, double factor) {
		int length=length();
		assert((a.length()==length)&&(b.length()==length));
		for (int i = 0; i < length; i++) {
			data[i]+=(a.data[i]*b.data[i])*factor;
		}
	}
	
	@Override
	public void addAt(int i, double v) {
		data[i]+=v;
	}
	
	@Override
	public void sub(AVector v) {
		if (v instanceof AArrayVector) {sub(((AArrayVector)v)); return;}
		int length=length();
		if(length!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		for (int i = 0; i < length; i++) {
			data[i] -= v.unsafeGet(i);
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
			result+=data[idata[i]]*v.unsafeGet(i);
		}
		return result;
	}
	
	public double dotProduct(Vector v, Index ix) {
		int vl=v.length();
		assert(v.length()==ix.length());
		double result=0.0;
		int[] idata=ix.getData();
		for (int i=0; i<vl; i++) {
			result+=data[idata[i]]*v.data[i];
		}
		return result;
	}
	
	@Override
	public double dotProduct(AVector v) {
		if ((v instanceof Vector)) return dotProduct((Vector)v);
		int len=length();
		if(len!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=data[i]*v.unsafeGet(i);
		}
		return result;
	}
	
	public double dotProduct(Vector v) {
		int len=length();
		if(len!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=data[i]*v.data[i];
		}
		return result;
	}
	
	public double distanceSquared(Vector v) {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			double d=data[i]-v.data[i];
			total+=d*d;
		}
		return total;
	}
	
	public double distance(Vector v) {
		return Math.sqrt(distanceSquared(v));
	}
	
	public double distance(AVector v) {
		if (v instanceof Vector) {
			return distance((Vector)v);
		}
		return super.distance(v);
	}
	
	public void sub(AArrayVector v) {
		sub(v,0);
	}
	
	public void sub(AArrayVector src,int srcOffset) {
		int length=length();
		assert(length==src.length());
		double[] srcData=src.getArray();
		int voffset=src.getArrayOffset()+srcOffset;
		for (int i = 0; i < length; i++) {
			data[i] -= srcData[voffset + i];
		}
	}
	
	@Override
	public void addMultiple(AVector v, double factor) {
		if (v instanceof AArrayVector) {addMultiple(((AArrayVector)v),factor); return;}
		v.addMultipleToArray(factor, 0, data, 0, length());
	}
	
	@Override
	public void addWeighted(AVector v, double factor) {
		if (v instanceof AArrayVector) {addWeighted(((AArrayVector)v),factor); return;}
		int length=length();
		if(length!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		for (int i = 0; i < length; i++) {
			data[i] = (data[i]*(1.0-factor)) + (v.unsafeGet(i)*factor);
		}
	}
	
	public void addWeighted(AArrayVector v, double factor) {
		int length=length();
		assert(length==v.length());
		double[] arr=v.getArray();
		int offset=v.getArrayOffset();
		for (int i = 0; i < length; i++) {
			data[i] = (data[i]*(1.0-factor)) + (arr[i+offset]*factor);
		}
	}
	
	@Override
	public void addMultiple(Vector source, Index index, double factor) {
		if (index.length()!=source.length()) throw new VectorzException("Index must match source vector");
		int len=source.length();
		assert(len==index.length());
		for (int i=0; i<len; i++) {
			int j=index.data[i];
			this.data[j]+=source.data[i]*factor;
		}
	}
	
	@Override
	public void addMultiple(Index destToSource, Vector source, double factor) {
		if (destToSource.length()!=this.length()) throw new VectorzException("Index must match this vector");
		int len=this.length();
		assert(len==destToSource.length());
		for (int i=0; i<len; i++) {
			int j=destToSource.data[i];
			this.data[i]+=source.data[j]*factor;
		}
	}
	
	@Override
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i]*=factor;
		}	
	}
	
	@Override
	public void multiply(AVector v) {
		if (v instanceof Vector) {multiply(((Vector)v)); return;}
		int len=length();
		if(len!=v.length()) throw new IllegalArgumentException("Mismatched vector sizes");
		for (int i = 0; i < len; i++) {
			unsafeSet(i,unsafeGet(i)*v.unsafeGet(i));
		}	
	}
	
	public void multiply(Vector v) {
		int len=length();
		assert(len==v.length());
		for (int i = 0; i < len; i++) {
			set(i,data[i]*v.data[i]);
		}	
	}
	
	@Override
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
	
	@Override
	public boolean isPackedArray() {
		return true;
	}

}
