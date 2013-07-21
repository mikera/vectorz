package mikera.vectorz;

import java.io.Serializable;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.impl.AbstractArray;
import mikera.arrayz.impl.SliceArray;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.randomz.Hash;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.impl.JoinedVector;
import mikera.vectorz.impl.ListWrapper;
import mikera.vectorz.impl.VectorIndexScalar;
import mikera.vectorz.impl.VectorIterator;
import mikera.vectorz.impl.WrappedSubVector;
import mikera.vectorz.ops.Logistic;
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
public abstract class AVector extends AbstractArray<Double> implements IVector, Comparable<AVector>, Serializable {
	
	// ================================================
	// Abstract interface
	public abstract int length();

	public abstract double get(int i);
	
	public abstract void set(int i, double value);
	
	// ================================================
	// Standard implementations

	@Override
	public void set(int[] indexes, double value) {
		if (indexes.length==1) {
			set(indexes[0],value);
		} if (indexes.length==0) {
			fill(value);
		} else {
			throw new VectorzException(""+indexes.length+"D set not supported on AVector");
		}
	}
	
	public void unsafeSet(int i, double value) {
		set(i,value);
	}
	
	public double unsafeGet(int i) {
		return get(i);
	}
	
	@Override
	public int dimensionality() {
		return 1;
	}
	
	@Override
	public double get(int... indexes) {
		assert(indexes.length==1);
		return get(indexes[0]);
	}
	
	@Override
	public AScalar slice(int position) {
		return new VectorIndexScalar(this,position);
	}
	
	@Override
	public AScalar slice(int dimension, int index) {
		if (dimension!=0) throw new IllegalArgumentException("Dimension out of range!");
		return slice(index);	
	}	
	
	@Override
	public int sliceCount() {
		return length();
	}
	
	@Override
	public List<Double> getSlices() {
		ArrayList<Double> al=new ArrayList<Double>();
		int l=length();
		for (int i=0; i<l; i++) {
			al.add(get(i));
		}
		return al;
	}
	
	@Override
	public int[] getShape() {
		return new int[] {length()};
	}
	
	@Override
	public int getShape(int dim) {
		if (dim==0) {
			return length();
		} else {
			throw new IndexOutOfBoundsException("Vector does not have dimension: "+dim);
		}
	}

	
	@Override
	public long[] getLongShape() {
		return new long[] {length()};
	}
	
	@Override
	public final long elementCount() {
		return length();
	}
	
	@Override
	public long nonZeroCount() {
		int n=length();
		long result=0;
		for (int i=0; i<n; i++) {
			if (get(i)!=0.0) result++;
		}
		return result;
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
		return JoinedVector.joinVectors(this,second);
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
		if (o instanceof AVector) return equals((AVector)o);
		if (o instanceof INDArray) return equals((INDArray)o);
		return false;
	}
	
	public boolean equals(AVector v) {
		int len=length();
		if (len != v.length())
			return false;
		for (int i = 0; i < len; i++) {
			if (get(i) != v.get(i))
				return false;
		}
		return true;
	}
	
	public boolean equals(INDArray v) {
		if (v.dimensionality()!=1) return false;
		int len=length();
		if (len != v.getShape()[0]) return false;
		
		int[] ind = new int[1];
		for (int i = 0; i < len; i++) {
			ind[0]=i;
			if (get(i) != v.get(ind))
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
			hashCode = 31 * hashCode + (Hash.hashCode(get(i)));
		}
		return hashCode;
	}

	@Override
	public void copyTo(double[] arr) {
		copyTo(arr,0);
	}
	
	/**
	 * Copies a the contents of a vector to a double array at the specified offset
	 */
	public void copyTo(double[] data, int offset) {
		copyTo(0,data,offset,length());
	}

	public void copyTo(int offset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[i+destOffset]=get(i+offset);
		}
	}
	
	public double[] toDoubleArray() {
		double[] result=new double[length()];
		copyTo(result,0);
		return result;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		int len=length();
		for (int i=0; i<len; i++) {
			dest.put(get(i));
		}
	}
	
	/**
	 * Copies a the contents of a vector to a vector at the specified offset
	 */
	public void copyTo(AVector dest, int destOffset) {
		if (dest instanceof AArrayVector) {
			copyTo((AArrayVector) dest,destOffset);
			return;
		}
		int len = length();
		for (int i=0; i<len; i++) {
			dest.set(destOffset+i,get(i));
		}
	}
	
	/**
	 * Copies a the contents of a vector to a vector at the specified offset
	 */
	public void copyTo(AArrayVector dest, int destOffset) {
		copyTo(dest.getArray(),dest.getArrayOffset()+destOffset);
	}
	
	/**
	 * Copies a subset of this vector to a vector at the specified offset
	 */
	public void copyTo(int offset, AVector dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest.set(destOffset+i,get(offset+i));
		}
	}

	/**
	 * Fills the entire vector with a given value
	 * @param value
	 */
	public void fill(double value) {
		fillRange(0,length(),value);
	}
	
	public void fillRange(int offset, int length, double value) {
		for (int i = 0; i < length; i++) {
			set(i+offset,value);
		}
	}
	
	/**
	 * Clamps all values in the vector to a given range
	 * @param value
	 */
	@Override
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
	
	public void multiply(INDArray a) {
		if (a instanceof AVector) {
			multiply((AVector)a);
		} else if (a instanceof AScalar) {
			multiply(((AScalar)a).get());
		} else {
			int dims=a.dimensionality();
			switch (dims) {
				case 0: multiply(a.get()); return;
				case 1: multiply(a.asVector()); return;
				default: throw new VectorzException("Can't multiply vector with array of dimensionality: "+dims);
			}
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
	public void abs() {
		int len=length();
		for (int i=0; i<len; i++) {
			double val=get(i);
			if (val<0) set(i,-val);
		}
	}
	
	@Override
	public void log() {
		int len=length();
		for (int i=0; i<len; i++) {
			double val=get(i);
			set(i,Math.log(val));
		}
	}
	
	/**
	 * Sets each component of the vector to its sign value (-1, 0 or 1)
	 */
	public void signum() {
		int len=length();
		for (int i=0; i<len; i++) {
			set(i,Math.signum(get(i)));
		}
	}
	
	/**
	 * Squares all elements of the vector
	 */
	public void square() {
		int len=length();
		for (int i=0; i<len; i++) {
			double x=unsafeGet(i);
			unsafeSet(i,x*x);
		}		
	}
	
	public void tanh() {
		int len=length();
		for (int i=0; i<len; i++) {
			double x=unsafeGet(i);
			unsafeSet(i,Math.tanh(x));
		}			
	}
	
	public void logistic() {
		int len=length();
		for (int i=0; i<len; i++) {
			double x=unsafeGet(i);
			unsafeSet(i,Logistic.logisticFunction(x));
		}			
	}
	
	/**
	 * Scales the vector by another vector of the same size
	 * @param v
	 */
	public final void scale(AVector v) {
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
			double x=unsafeGet(i);
			total+=x*x;
		}
		return total;
	}
	
	@Override
	public AVector getTranspose() {return this;}
	
	@Override
	public final AVector getTransposeView() {return this;}
	
	public AMatrix outerProduct(AVector a) {
		int rc=length();
		int cc=a.length();
		Matrix m=Matrix.create(rc, cc);
		int di=0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				m.data[di++]=unsafeGet(i)*a.unsafeGet(j);
			}
		}
		return m;
	}
	
	public INDArray outerProduct(INDArray a) {
		if (a instanceof AVector) {
			return outerProduct((AVector)a);
		}
		return super.outerProduct(a);
	}
	
	public Scalar innerProduct(AVector v) {
		return Scalar.create(dotProduct(v));
	}
	
	public AVector innerProduct(AMatrix m) {
		int cc=m.columnCount();
		int rc=m.rowCount();
		if (rc!=length()) throw new VectorzException("Incompatible sizes for inner product: ["+length()+ "] x ["+rc+","+cc+"]");
		Vector r=Vector.createLength(cc);
		for (int i=0; i<cc; i++) {
			double y=0.0;
			for (int j=0; j<rc; j++) {
				y+=unsafeGet(j)*m.unsafeGet(j,i);
			}
			r.unsafeSet(i,y);
		}
		return r;
	}
	
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AVector) {
			return Scalar.create(dotProduct((AVector)a));
		}
		return super.innerProduct(a);
	}
	
	public double dotProduct(AVector v) {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			total+=unsafeGet(i)*v.unsafeGet(i);
		}
		return total;
	}
	
	public double dotProduct(Vector v) {
		if(v.length()!=length()) throw new IllegalArgumentException("VEctor size mismatch");
		return dotProduct(v.data, 0);
	}
	
	public double dotProduct(AVector v, Index ix) {
		int vl=v.length();
		if (v.length()!=ix.length()) throw new IllegalArgumentException("Mismtached source vector and index sizes");
		double result=0.0;
		for (int i=0; i<vl; i++) {
			result+=get(ix.get(i))*v.unsafeGet(i);
		}
		return result;
	}
	
	public double dotProduct(double[] data, int offset) {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=unsafeGet(i)*data[offset+i];
		}
		return result;
	}
	
	public void crossProduct(AVector a) {
		if(!((length()==3)&&(a.length()==3))) throw new IllegalArgumentException("Cross product requires length 3 vectors");
		double x=unsafeGet(0);
		double y=unsafeGet(1);
		double z=unsafeGet(2);
		double x2=a.unsafeGet(0);
		double y2=a.unsafeGet(1);
		double z2=a.unsafeGet(2);
		double tx=y*z2-z*y2;
		double ty=z*x2-x*z2;
		double tz=x*y2-y*x2;			
		set(0,tx);
		set(1,ty);
		set(2,tz);		
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
			double d=unsafeGet(i)-v.unsafeGet(i);
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
			double d=unsafeGet(i)-v.unsafeGet(i);
			total+=Math.abs(d);
		}
		return total;
	}
	
	public double distanceLinf(AVector v) {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			double d=Math.abs(unsafeGet(i)-v.unsafeGet(i));
			result=Math.max(result,d);
		}
		return result;
	}
	
	/**
	 * Returns the maximum absolute element of a vector
	 * @return
	 */
	public double maxAbsElement() {
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
	 * Normalises so that the maximum absolute element is 1.0
	 * Returns the previous maximum absolute element.
	 */
	public double normaliseMaxAbsElement() {
		double scale=maxAbsElement();
		scale(1.0/scale);
		return scale;
	}
	
	/**
	 * Returns the sum of all elements in a vector
	 * @return
	 */
	public double elementSum() {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=unsafeGet(i);
		}		
		return result;
	}
	
	@Override public final double elementSquaredSum() {
		return magnitudeSquared();
	}
	
	/**
	 * Returns the Euclidean angle between this vector and another vector
	 * @return angle in radians
	 */
	public double angle(AVector v) {
		return Math.acos(dotProduct(v)/(v.magnitude()*this.magnitude()));
	}
	
	/**
	 * Normalises this vector to a magnitude of 1.0
	 * 
	 * Has no effect on a zero-length vector (i.e. it will remain zero)
	 * 
	 * @return
	 */
	public double normalise() {
		double d=magnitude();
		if (d>0) multiply(1.0/d);
		return d;
	}
	
	public void negate() {
		multiply(-1.0);
	}
	
	@Override
	public void pow(double exponent) {
		int len=length();
		for (int i=0; i<len; i++) {
			unsafeSet(i,Math.pow(unsafeGet(i),exponent));
		}				
	}
	
	/**
	 * Sets the vector to equal the value of another vector
	 */
	public void set(AVector src) {
		int len=length();
		if (src.length()!=len) throw new IllegalArgumentException("Source Vector of wrong size: "+src.length());
		for (int i=0; i<len; i++) {
			unsafeSet(i,src.unsafeGet(i));
		}
	}
	
	public final void set(double a) {
		throw new UnsupportedOperationException("0d set not supported for vectors - use fill instead?");
	}
	
	@Deprecated
	public void set(double[] data) {
		setElements(data,0,length());
	}
	
	@Override
	public void setElements(double[] data) {
		setElements(data,0,length());
	}
	
	public void set(INDArray a) {
		if (a instanceof AVector) {set((AVector)a); return;}
		if (a.dimensionality()==1) {
			int len=length();
			for (int i=0; i<len; i++) {
				unsafeSet(i,a.get(i));
			}		
		} else {
			throw new IllegalArgumentException("Cannot set vector using array of dimensonality: "+a.dimensionality());
		}
	}
	
	@Override
	public void setElements(double[] values, int offset, int length) {
		if (length!=length()) {
			throw new IllegalArgumentException("Incorrect length: "+length);
		}
		for (int i=0; i<length; i++) {
			unsafeSet(i,values[offset+i]);
		}
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		copyTo(dest,offset);
	}
	
	/**
	 * Set the vector equal to an offset into another vector
	 * @param src
	 * @param srcOffset
	 */
	public void set(AVector src, int srcOffset) {
		int len=length();
		if ((srcOffset<0)||(len+srcOffset>src.length())) throw new IndexOutOfBoundsException();
		for (int i=0; i<len; i++) {
			unsafeSet(i,src.unsafeGet(srcOffset+i));
		}
	}
	
	public void setValues(double... values) {
		int len=length();
		if (values.length!=len) throw new VectorzException("Trying to set vectors with incorrect number of doubles: "+values.length);
		for (int i=0; i<len; i++) {
			unsafeSet(i,values[i]);
		}		
	}
	
	public long zeroCount() {
		return elementCount()-nonZeroCount();
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
	public final AVector asVector() {
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
			return Arrayz.createFromVector(this,dimensions);
		}
	}
	
	/**
	 * Returns true if this vector is of a view type that references other vectors / data.
	 * @return
	 */
	@Override
	public boolean isView() {
		return true;
	}
	
	/**
	 * Returns true if this vector is mutable.
	 * @return
	 */
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isElementConstrained() {
		return false;
	}
	
	
	/**
	 * Returns true if this vector is fully mutable, i.e. can contain any unconstrained double values
	 * @return
	 */
	@Override
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
			addAt(i,v.unsafeGet(i));
		}
	}
	
	@Override
	public void add(INDArray a) {
		if (a instanceof AVector) {
			add((AVector)a);
		} else if (a instanceof AScalar) {
			add(a.get());
		}else {
			super.add(a);
		}
	}
	
	@Override
	public void sub(INDArray a) {
		if (a instanceof AVector) {
			sub((AVector)a);
		} else if (a instanceof AScalar) {
			sub(a.get());
		}else {
			super.sub(a);
		}	
	}
	
	/**
	 * Adds part another vector to this one, starting at the specified offset in the source vector
	 * @param src
	 */
	public void add(AVector src, int srcOffset) {
		int length=length();
		assert(srcOffset>=0);
		assert(srcOffset+length<=src.length());
		for (int i = 0; i < length; i++) {
			addAt(i,src.get(srcOffset+i));
		}
	}
	
	/**
	 * Adds another vector into this one, at the specified offset
	 * @param offset
	 * @param a
	 */
	public void add(int offset, AVector a) {
		add(offset,a,0,a.length());
	}
	
	/**
	 * Adds another vector into this one, at the specified offset
	 * @param offset
	 * @param a
	 */
	public void add(int offset, AVector a, int aOffset, int length) {
		for (int i = 0; i < length; i++) {
			addAt(offset+i,a.get(i+aOffset));
		}		
	}
	
	public void addProduct(AVector a, AVector b) {
		addProduct(a,b,1.0);
	}
	
	public void addProduct(AVector a, AVector b, double factor) {
		int length=length();
		if((a.length()!=length)||(b.length()!=length)) {
			throw new IllegalArgumentException("Unequal vector sizes for addProduct");
		}
		for (int i = 0; i < length; i++) {
			addAt(i,(a.unsafeGet(i)*b.unsafeGet(i)*factor));
		}
	}
	
	/**
	 * Adds a scaled multiple of another vector to this one
	 * @param src
	 */
	public void addMultiple(AVector src, double factor) {
		if (src.length()!=length()) throw new RuntimeException("Source vector has different size!" + src.length());
		addMultiple(src,0,factor);
	}
	
	public void addMultiple(AVector src, int srcOffset, double factor) {
		addMultiple(0,src,srcOffset,length(),factor);
	}
	
	public void addMultiple(int offset, AVector src, int srcOffset, int length, double factor) {
		if ((offset+length)>length()) throw new IndexOutOfBoundsException();
		for (int i = 0; i < length; i++) {
			addAt(i+offset,src.get(i+srcOffset)*factor);
		}
	}
	
	public void addMultiple(int offset, AVector v, double factor) {
		addMultiple(offset,v,0,v.length(),factor);
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
	
	@Override
	public void sub(double d) {
		add(-d);
	}
	
	/**
	 * Returns true if this vector is a zero vector (all components zero)
	 * @return
	 */
	public boolean isZeroVector() {
		int len=length();
		for (int i=0; i<len; i++) {
			if (unsafeGet(i)!=0.0) return false;
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
			sb.append(unsafeGet(0));
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(unsafeGet(i));
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public Vector toVector() {
		return Vector.create(this);
	}
	
	public List<Double> asElementList() {
		return new ListWrapper(this);
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new VectorIterator(this);
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return iterator();
	}

	public void set(IVector vector) {
		int len=length();
		assert(len==vector.length());
		for (int i=0; i<len; i++) {
			this.unsafeSet(i,vector.get(i));
		}
	}

	/**
	 * Adds source vector to this vector at the specified indexes which should map from source->this
	 * @param source
	 * @param sourceToDest
	 * @param factor
	 */
	public void addMultiple(Vector source, Index sourceToDest, double factor) {
		if (sourceToDest.length()!=source.length()) throw new VectorzException("Index must match source vector");
		int len=source.length();
		assert(len==sourceToDest.length());
		for (int i=0; i<len; i++) {
			int j=sourceToDest.data[i];
			this.addAt(j,source.data[i]*factor);
		}
	}
	
	/**
	 * Adds source vector to this vector at the specified indexes which should map from source->this
	 * @param source
	 * @param sourceToDest
	 * @param factor
	 */
	public void addMultiple(AVector source, Index sourceToDest, double factor) {
		if (sourceToDest.length()!=source.length()) throw new VectorzException("Index must match source vector");
		int len=source.length();
		assert(len==sourceToDest.length());
		for (int i=0; i<len; i++) {
			int j=sourceToDest.data[i];
			this.addAt(j,source.unsafeGet(i)*factor);
		}
	}
	
	/**
	 * Adds to this vector at taking values from source at the specified indexes which should map from this->source
	 * @param source
	 * @param destToSource
	 * @param factor
	 */
	public void addMultiple(Index destToSource, Vector source, double factor) {
		if (destToSource.length()!=this.length()) throw new VectorzException("Index must match this vector");
		int len=this.length();
		assert(len==destToSource.length());
		for (int i=0; i<len; i++) {
			int j=destToSource.data[i];
			this.addAt(i,source.data[j]*factor);
		}
	}
	
	/**
	 * Adds to this vector at taking values from source at the specified indexes which should map from this->source
	 * @param source
	 * @param destToSource
	 * @param factor
	 */
	public void addMultiple(Index destToSource, AVector source, double factor) {
		int len=this.length();
		if (destToSource.length()!=len) throw new VectorzException("Index must match this vector");
		for (int i=0; i<len; i++) {
			int j=destToSource.data[i];
			this.addAt(i,source.get(j)*factor);
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
	
	public void addToArray(double[] array, int offset) {
		addToArray(0,array,offset,length());
	}

	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		assert(offset+length<=length());
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=get(i+offset);
		}
	}
	
	public void addMultipleToArray(double factor, int offset, double[] array, int arrayOffset, int length) {
		assert(offset>=0);
		assert(offset+length<=length());
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*get(i+offset);
		}
	}
	
	public void addProductToArray(double factor, int offset, AVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		if (other instanceof AArrayVector) {
			addProductToArray(factor,offset,(AArrayVector)other,otherOffset,array,arrayOffset,length);
			return;
		}
		assert(offset>=0);
		assert(offset+length<=length());
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*get(i+offset)*other.get(i+otherOffset);
		}		
	}
	
	public void addProductToArray(double factor, int offset, AArrayVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		assert(offset>=0);
		assert(offset+length<=length());
		double[] otherArray=other.getArray();
		otherOffset+=other.getArrayOffset();
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*get(i+offset)*otherArray[i+otherOffset];
		}		
	}

	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		int length=length();
		for (int i=0; i<length; i++) {
			addAt(i, (a.get(i+aOffset)* b.get(i+bOffset)*factor));
		}
	}
	

	@Override
	public void applyOp(IOp op) {
		if (op instanceof Op) {
			applyOp((Op) op);
		}
		int len=length();
		for (int i=0; i<len; i++) {
			unsafeSet(i,op.apply(unsafeGet(i)));
		}
	}

	@Override
	public void applyOp(Op op) {
		int len=length();
		for (int i=0; i<len; i++) {
			unsafeSet(i,op.apply(unsafeGet(i)));
		}
	}
	
	/**
	 * Adds a value to a specific element of the vector
	 * 
	 * This function does not perform bounds checking
	 * 
	 * @param i
	 * @param v
	 */
	public void addAt(int i, double v) {
		unsafeSet(i,unsafeGet(i)+v);
	}

	public void scaleAdd(double factor, double constant) {
		scale(factor);
		add(constant);
	}

	@Override
	public void add(double constant) {
		int len=length();
		for (int i=0; i<len; i++) {
			addAt(i,constant);
		}
	}
	
	/**
	 * Returns an exact clone of this vector, i.e. of the same type
	 * @return
	 */
	public abstract AVector exactClone();

	public boolean equalsArray(double[] data) {
		int len=length();
		if (len!=data.length) return false;
		for (int i=0; i<len; i++) {
			if (unsafeGet(i)!=data[i]) return false;
		}
		return true;
	}

	/**
	 * Set part of this vector from a double array
	 */
	public void set(int offset, double[] data, int dataOffset, int length) {
		if ((offset<0)||(offset+length>this.length())) throw new IndexOutOfBoundsException("Offset: "+offset+" , Length: "+length +" on vector with total length "+length());
		for (int i=0; i<length; i++) {
			unsafeSet(offset+i,data[dataOffset+i]);
		}
	}
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims<1) {
			throw new VectorzException("Can't broadcast to a smaller shape!");
		} else if (tdims==1) {
			if (targetShape[0]!=this.length()) {
				throw new VectorzException("Can't broadcast to different length: "+targetShape[0]);
			}
			return this;
		} else if (tdims==2) {
			int n=targetShape[0];
			AVector[] vs=new AVector[n];
			for (int i=0; i<n; i++) {vs[i]=this;}
			return Matrixx.createFromVectors(vs);
		} else {
			int n=targetShape[0];
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,n);
		}
	}

	@Override
	public void validate() {
		super.validate();
	}

}
