package mikera.vectorz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.INDArray;
import mikera.indexz.Index;
import mikera.matrixx.Matrixx;
import mikera.vectorz.impl.JoinedVector;
import mikera.vectorz.impl.ListWrapper;
import mikera.vectorz.impl.WrappedSubVector;
import mikera.vectorz.util.VectorzException;

/**
 * Main abstract base class for all types of vector
 * 
 * Contains default implementations for most vector operations which can be
 * overriden to achieve better performance in derived classes.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class AVector implements IVector, Cloneable, Comparable<AVector>, Serializable, Iterable<Double> {
	
	// ================================================
	// Abstract interface
	public abstract int length();

	public abstract double get(int i);
	
	public abstract void set(int i, double value);
	
	
	// ================================================
	// Standard implementations
	
	
	@Override
	public int dimensionality() {
		return 1;
	}
	
	@Override
	public double get(int... indexes) {
		assert(indexes.length==1);
		return get(indexes[0]);
	}
	
	/**
	 * Obtains a sub-vector that refers to this vector.
	 * Changes to the sub-vector will be reflected in this vector
	 */
	public AVector subVector(int offset, int length) {
		return new WrappedSubVector(this,offset,length);
	}

	/**
	 * Returns a new vector that refers to this vector joined to a second vector
	 * @param second
	 * @return
	 */
	public AVector join(AVector second) {
		return JoinedVector.join(this,second);
	}
	
	public int compareTo(AVector a) {
		int len=length();
		if (len!=a.length()) throw new IllegalArgumentException("Vectors must be same length for comparison");
		for (int i=0; i<len; i++) {
			double diff=get(i)-a.get(i);
			if (diff<0.0) return -1;
			if (diff>0.0) return 1;
		}
		return 0;
	}
	
	/**
	 * Test for equality on vectors. Returns true iff all values in the vector
	 * are identical
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AVector)) return false;
		
		AVector v = (AVector) o;
		int len=length();
		if (len != v.length())
			return false;
		for (int i = 0; i < len; i++) {
			if (get(i) != v.get(i))
				return false;
		}
		return true;
	}
	
	public List<Double> toList() {
		ArrayList<Double> al=new ArrayList<Double>();
		int len=length();
		for (int i=0; i<len; i++) {
			al.add(get(i));
		}
		return al;
	}
	
	public boolean epsilonEquals(AVector v) {
		return epsilonEquals(v,Vectorz.TEST_EPSILON);
	}
	
	public boolean epsilonEquals(AVector v,double tolerance) {
		if (this == v) return true;
		int len=length();
		if (len!=v.length())
			throw new VectorzException("Mismatched vector sizes!");
		for (int i = 0; i < len; i++) {
			if (!Tools.epsilonEquals(get(i), v.get(i), tolerance)) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		int len=length();
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + (Tools.hashCode(get(i)));
		}
		return hashCode;
	}

	/**
	 * Copies a the contents of a vector to a double array at the specified offset
	 */
	public void copyTo(double[] data, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			data[i+offset]=get(i);
		}
	}
	
	public double[] toArray() {
		double[] result=new double[length()];
		copyTo(result,0);
		return result;
	}
	
	/**
	 * Copies a the contents of a vector to a vector at the specified offset
	 */
	public void copyTo(AVector dest, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			dest.set(offset+i,get(i));
		}
	}
	
	/**
	 * Copies a subset of this vector to a vector at the specified offset
	 */
	public void copy(int start, int length, AVector dest, int destOffset) {
		for (int i=0; i<length; i++) {
			dest.set(destOffset+i,get(start+i));
		}
	}

	/**
	 * Fills the entire vector with a given value
	 * @param value
	 */
	public void fill(double value) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,value);
		}
	}
	
	public void fillRange(int offset, int length, double value) {
		subVector(offset,length).fill(value);
	}
	
	/**
	 * Clamps all values in the vector to a given range
	 * @param value
	 */
	public void clamp(double min, double max) {
		int len=length();
		for (int i = 0; i < len; i++) {
			double v=get(i);
			if (v<min) {
				set(i,min);
			} else if (v>max) {
				set(i,max);
			}
		}
	}
	
	public void clampMax(double max) {
		int len=length();
		for (int i = 0; i < len; i++) {
			double v=get(i);
			if (v>max) {
				set(i,max);
			}
		}
	}
	
	public void clampMin(double min) {
		int len=length();
		for (int i = 0; i < len; i++) {
			double v=get(i);
			if (v<min) {
				set(i,min);
			} 
		}
	}
	
	/**
	 * Multiplies the vector by a constant factor
	 * @param factor Factor by which to multiply each component of the vector
	 */
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,get(i)*factor);
		}	
	}
	
	public void multiply(AVector v) {
		int len=length();
		assert(len==v.length());
		for (int i = 0; i < len; i++) {
			set(i,get(i)*v.get(i));
		}	
	}
	
	public void multiply(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,get(i)*data[i+offset]);
		}	
	}
	
	public void multiplyTo(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i+offset]*=get(i);
		}	
	}
	
	public void divide(double factor) {
		multiply(1.0/factor);
	}
	
	public void divide(AVector v) {
		int len=length();
		assert(len==v.length());
		for (int i = 0; i < len; i++) {
			set(i,get(i)/v.get(i));
		}	
	}
	
	public void divide(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,get(i)/data[i+offset]);
		}	
	}
	
	public void divideTo(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i+offset]/=get(i);
		}	
	}
	
	/**
	 * Sets each component of the vector to its absolute value
	 */
	public void absolute() {
		int len=length();
		for (int i=0; i<len; i++) {
			set(i,Math.abs(get(i)));
		}
	}
	
	/**
	 * Scales the vector by a scalar factor
	 * @param factor
	 */
	public void scale(double factor) {
		multiply(factor);
	}
	
	/**
	 * Scales the vector by another vector of the same size
	 * @param v
	 */
	public void scale(AVector v) {
		multiply(v);
	}
	
	/**
	 * Scales the vector up to a specific target magnitude
	 * @return the old magnitude of the vector
	 */
	public double scaleToMagnitude(double targetMagnitude) {
		double oldMagnitude=magnitude();
		multiply(targetMagnitude/oldMagnitude);
		return oldMagnitude;
	}
	
	public void scaleAdd(double factor, AVector v) {
		multiply(factor);
		add(v);
	}
	
	public void interpolate(AVector v, double alpha) {
		multiply(1.0-alpha);
		addMultiple(v,alpha);
	}
	
	public void interpolate(AVector a, AVector b, double alpha) {
		set(a);
		interpolate(b,alpha);
	}
	
	public double magnitudeSquared() {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			double x=get(i);
			total+=x*x;
		}
		return total;
	}
	
	public double dotProduct(AVector v) {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			total+=get(i)*v.get(i);
		}
		return total;
	}
	
	public double dotProduct(AVector v, Index ix) {
		int vl=v.length();
		assert(v.length()==ix.length());
		double result=0.0;
		for (int i=0; i<vl; i++) {
			result+=get(ix.get(i))*v.get(i);
		}
		return result;
	}
	
	/**
	 * Returns the magnitude (Euclidean length) of the vector
	 * @return
	 */
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}
	
	public double distanceSquared(AVector v) {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			double d=get(i)-v.get(i);
			total+=d*d;
		}
		return total;
	}
	
	public double distance(AVector v) {
		return Math.sqrt(distanceSquared(v));
	}
	
	public double distanceL1(AVector v) {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			double d=get(i)-v.get(i);
			total+=Math.abs(d);
		}
		return total;
	}
	
	public double distanceLinf(AVector v) {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			double d=Math.abs(get(i)-v.get(i));
			result=Math.max(result,d);
		}
		return result;
	}
	
	/**
	 * Returns the maximum absolute component of a vector
	 * @return
	 */
	public double maxAbsComponent() {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			double comp=get(i);
			if (comp>result) {
				result=comp;
			} else if (-comp>result) {
				result=-comp;
			}
		}		
		return result;
	}
	
	/**
	 * Normalises so that the maximum absolute component is 1.0
	 * Returns the previous maximum absolute component.
	 */
	public double normaliseMaxAbsComponent() {
		double scale=maxAbsComponent();
		scale(1.0/scale);
		return scale;
	}
	
	/**
	 * Returns the Euclidean angle between this vector and another vector
	 * @return angle in radians
	 */
	public double angle(AVector v) {
		return Math.acos(dotProduct(v)/(v.magnitude()*this.magnitude()));
	}
	
	public double normalise() {
		double d=magnitude();
		if (d>0) multiply(1.0/d);
		return d;
	}
	
	public void negate() {
		multiply(-1.0);
	}
	
	/**
	 * Sets the vector to equal the value of another vector
	 */
	public void set(AVector a) {
		int len=length();
		if (a.length()!=len) throw new IllegalArgumentException("Source Vector of wrong size: "+a.length());
		for (int i=0; i<len; i++) {
			set(i,a.get(i));
		}
	}
	
	/**
	 * Set the vector equal to an offset into another vector
	 * @param a
	 * @param offset
	 */
	public void set(AVector a, int offset) {
		int len=length();
		assert(len+offset<=a.length());
		for (int i=0; i<len; i++) {
			set(i,a.get(offset+i));
		}
	}
	
	public void setValues(double... values) {
		int len=length();
		for (int i=0; i<len; i++) {
			set(i,values[i]);
		}		
	}
	
	/**
	 * Clones the vector, creating a new copy of all data. 
	 * 
	 * The clone is:
	 *  - not guaranteed to be of the same type. 
	 *  - guaranteed to be fully mutable
	 *  - guaranteed not to contain a reference (i.e. is a full deep copy)
	 */
	@Override
	public AVector clone() {
		// use a deep copy in case this vector is a reference vector type		
		AVector nv=Vectorz.newVector(length());
		this.copyTo(nv, 0);
		return nv;
	}
	
	@Override
	public AVector asVector() {
		return this;
	}
	
	@Override
	public INDArray reshape(int... dimensions) {
		int ndims=dimensions.length;
		if (ndims==1) {
			return clone();
		} else if (ndims==2) {
			return Matrixx.createFromVector(this, dimensions[0], dimensions[1]);
		} else {
			throw new UnsupportedOperationException("Can't reshape to dimensionality: "+ndims);
		}
	}
	
	/**
	 * Returns true if this vector is of a type that references other vectors / data.
	 * @return
	 */
	public boolean isReference() {
		return true;
	}
	
	/**
	 * Returns true if this vector is mutable.
	 * @return
	 */
	public boolean isMutable() {
		return true;
	}
	
	/**
	 * Returns true if this vector is fully mutable, i.e. can contain any unconstrained double values
	 * @return
	 */
	public boolean isFullyMutable() {
		return isMutable();
	}
	
	/**
	 * Adds another vector to this one
	 * @param v
	 */
	public void add(AVector v) {
		int vlength=v.length();
		int length=length();
		if (vlength != length) {
			throw new IllegalArgumentException("Source vector has different size: " + vlength);
		}
		for (int i = 0; i < length; i++) {
			double x=get(i)+v.get(i);
			set(i,x);
		}
	}
	
	/**
	 * Adds another vector to this one
	 * @param v
	 */
	public void add(AVector v, int offset) {
		int length=length();
		assert(offset>=0);
		assert(offset+length<=v.length());
		for (int i = 0; i < length; i++) {
			double x=get(i)+v.get(offset+i);
			set(i,x);
		}
	}
	
	public void addProduct(AVector a, AVector b) {
		addProduct(a,b,1.0);
	}
	
	public void addProduct(AVector a, AVector b, double factor) {
		int length=length();
		assert((a.length()==length)&&(b.length()==length));
		for (int i = 0; i < length; i++) {
			double x=get(i)+(a.get(i)*b.get(i)*factor);
			set(i,x);
		}
	}
	
	/**
	 * Adds a scaled multiple of another vector to this one
	 * @param v
	 */
	public void addMultiple(AVector v, double factor) {
		if (v.length()!=length()) throw new RuntimeException("Source vector has different size!" + v.length());
		addMultiple(v,factor,0);
	}
	
	public void addMultiple(AVector v, double factor, int srcOffset) {
		int length=length();
		for (int i = 0; i < length; i++) {
			double x=get(i)+v.get(i+srcOffset)*factor;
			set(i,x);
		}
	}
	
	/**
	 * Updates a weighted average of this vector with another vector
	 * @param v
	 */
	public void addWeighted(AVector v, double factor) {
		multiply(1.0-factor);
		addMultiple(v,factor);
	}
	
	/**
	 * Subtracts a vector from this vector
	 * @param v
	 */
	public void sub(AVector v) {
		addMultiple(v,-1.0);
	}
	
	/**
	 * Returns true if this vector is a zero vector (all components zero)
	 * @return
	 */
	public boolean isZeroVector() {
		int len=length();
		for (int i=0; i<len; i++) {
			if (get(i)!=0.0) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if the vector has unit length
	 * @return
	 */
	public boolean isUnitLengthVector() {
		double mag=magnitudeSquared();
		return Math.abs(mag-1.0)<Vectorz.TEST_EPSILON;
	}
	
	public void projectToPlane(AVector normal, double distance) {
		assert(Tools.epsilonEquals(normal.magnitude(), 1.0));
		double d=dotProduct(normal);
		addMultiple(normal,distance-d);
	}
	
	/**
	 * Subtracts a scaled multiple of another vector from this vector
	 * @param v
	 */
	public void subMultiple(AVector v, double factor) {
		addMultiple(v,-factor);
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int length=length();
		sb.append('[');
		if (length>0) {
			sb.append(get(0));
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(get(i));
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	public List<Double> asList() {
		return new ListWrapper(this);
	}
	
	public Iterator<Double> iterator() {
		return new VectorIterator(this);
	}

	public void set(IVector vector) {
		int len=length();
		assert(len==vector.length());
		for (int i=0; i<len; i++) {
			this.set(i,vector.get(i));
		}
	}

	public void addMultiple(Vector vector, Index index, double factor) {
		int len=vector.length();
		assert(len==index.length());
		for (int i=0; i<len; i++) {
			int j=index.data[i];
			this.set(j,this.get(j)+vector.data[i]*factor);
		}
	}

	/**
	 * sets the vector using values indexed from another vector
	 */
	public void set(AVector v, Index indexes) {
		int len=length();
		assert(indexes.length()==len);
		for (int i=0; i<len ; i++) {
			set(i, v.get(indexes.get(i)));
		}
	}

	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		assert(offset+length<=length());
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=get(i+offset);
		}
	}
	
	public void addMultipleToArray(double factor, int offset, double[] array, int arrayOffset, int length) {
		assert(offset+length<=length());
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*get(i+offset);
		}
	}

	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		int length=length();
		for (int i=0; i<length; i++) {
			set(i, get(i)+ (a.get(i+aOffset)* b.get(i+bOffset)*factor));
		}
	}
}
