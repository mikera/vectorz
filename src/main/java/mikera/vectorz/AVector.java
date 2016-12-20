package mikera.vectorz;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.ISparse;
import mikera.arrayz.impl.AbstractArray;
import mikera.arrayz.impl.SliceArray;
import mikera.indexz.AIndex;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.BroadcastVectorMatrix;
import mikera.matrixx.impl.RowMatrix;
import mikera.randomz.Hash;
import mikera.util.Maths;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.ASizedVector;
import mikera.vectorz.impl.ASparseVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.ImmutableVector;
import mikera.vectorz.impl.IndexedElementVisitor;
import mikera.vectorz.impl.IndexedSubVector;
import mikera.vectorz.impl.JoinedVector;
import mikera.vectorz.impl.ListWrapper;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.impl.VectorIndexScalar;
import mikera.vectorz.impl.VectorIterator;
import mikera.vectorz.impl.WrappedSubVector;
import mikera.vectorz.ops.Logistic;
import mikera.vectorz.util.Constants;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
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
public abstract class AVector extends AbstractArray<Double> implements IVector {

	// ================================================
	// Abstract interface

	@Override
	public abstract int length();

	@Override
	public abstract double get(int i);
	
	@Override
	public abstract void set(int i, double value);
	
	// ================================================
	// Standard implementations

	@Override
	public double get(long i) {
		if ((i<0)||(i>=length())) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return unsafeGet((int)i);
	}
	
	public void set(long i, double value) {
		if ((i<0)||(i>=length())) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		unsafeSet((int)i,value);
	}
	
	@Override
	public final double getElement(long i) {
		return get(i);
	}
	
	@Override
	public void set(int[] indexes, double value) {
		if (indexes.length==1) {
			set(indexes[0],value);
		} else {
			throw new UnsupportedOperationException(""+indexes.length+"D set not supported on AVector");
		}
	}
	
	@Override
	public void set(long[] indexes, double value) {
		if (indexes.length==1) {
			set(Tools.toInt(indexes[0]),value);
		} else {
			throw new UnsupportedOperationException(""+indexes.length+"D set not supported on AVector");
		}
	}
	
	/**
	 * Like set, but performs no bounds checking.
	 * 
	 * Results are undefined if the index is out of range
	 * @param i
	 * @param value
	 */
	public void unsafeSet(int i, double value) {
		set(i,value);
	}
	
	/**
	 * Like get, but performs no bounds checking.
	 * 
	 * Results are undefined if the index is out of range
	 * @param i
	 * @param value
	 */	
	public double unsafeGet(int i) {
		return get(i);
	}
	
	@Override
	public final double get(int x, int y) {
		throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, x,y));
	}
	
	@Override
	public final int dimensionality() {
		return 1;
	}
	
	@Override
	public final double get(int... indexes) {
		if (indexes.length!=1) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, indexes));
		return get(indexes[0]);
	}
	
	@Override
	public final double get(long[] indexes) {
		if (indexes.length!=1) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, indexes));
		return get(Tools.toInt(indexes[0]));
	}
	
	@Override
	public final double get(AIndex ix) {
		if (ix.length()!=1) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, ix));
		return get(ix.get(0));
	}
	
	@Override
	public double get() {
		throw new UnsupportedOperationException("Can't do 0-d get on a vector!");
	}
	
	@Override
	public AScalar slice(int position) {
		return VectorIndexScalar.wrap(this,position);
	}
	
	@Override
	public Object sliceValue(int i) {
		return get(i);
	}
	
	@Override
	public AScalar slice(int dimension, int index) {
		checkDimension(dimension);
		return slice(index);	
	}	
	
	@Override
	public int sliceCount() {
		return length();
	}
	
	@Override
	public List<Double> getSlices() {
		return new ListWrapper(this);
	}
	
	@Override
	public int[] getShape() {
		return new int[] {length()};
	}
	
	@Override
	public int[] getShapeClone() {
		return new int[] {length()};
	}
	
	@Override
	public final int getShape(int dim) {
		if (dim==0) {
			return length();
		} else {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
		}
	}

	
	@Override
	public long[] getLongShape() {
		return new long[] {length()};
	}
		
	@Override
	public long elementCount() {
		return length();
	}
	
	@Override
	public long nonZeroCount() {
		int n=length();
		long result=0;
		for (int i=0; i<n; i++) {
			if (unsafeGet(i)!=0.0) result++;
		}
		return result;
	}
	
	/**
	 * Return an double array specifying the values in this vector which are non-zero
	 * 
	 * @return
	 */
	public double[] nonZeroValues() {
		int len=length();
		int n=(int)nonZeroCount();
		if (n==0) return DoubleArrays.EMPTY;
		double[] vs=new double[n];
		
		int vi=0;
		for (int i=0; i<len; i++) {
			double d=unsafeGet(i);
			if (d!=0.0) {
				vs[vi++]=d;
				if (vi>=n) return vs;
			}
		}
		return vs;
	}
	
	@Override
	public AVector subArray(int[] offsets, int[] shape) {
		if (offsets.length!=1) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		if (shape.length!=1) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		return subVector(offsets[0],shape[0]);
	}
	
	
	@Override
	public INDArray rotateView(int dimension, int shift) {
		checkDimension(dimension);
		return rotateView(shift);
	}
	
	@Override
	public AVector rotateView(int shift) {
		int n=length();
		if (n==0) return this;
		
		shift = Maths.mod(shift,n);
		if (shift==0) return this;
			
		return subVector(shift,n-shift).join(subVector(0,shift));
	}	
	
	@Override
	public AVector rotateCopy(int shift) {
		return rotateView(shift).copy();
	}	
	
	/**
	 * Obtains a sub-vector view that refers to this vector.
	 * Changes to the sub-vector will be reflected in this vector
	 */
	public AVector subVector(int offset, int length) {
		int len=checkRange(offset,length);

		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		
		return WrappedSubVector.wrap(this,offset,length);
	}

	/**
	 * Returns a new vector that refers to this vector joined to a second vector.
	 * 
	 * Optimises the type of the returned vector to be as efficient as possible.
	 * 
	 * @param second
	 * @return
	 */
	@Override
	public AVector join(INDArray b) {
		if (b.dimensionality()!=1) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, b));
		return join(b.asVector());
	}
	
	public AVector join(AVector second) {
		if (second.length()==0) return this;
		AVector ej=tryEfficientJoin(second);
		if (ej!=null) return ej;
		return JoinedVector.joinVectors(this,second);
	}
	
	/**
	 * Attempts to perform an efficient join with a second vector. An efficient join is guaranteed 
	 * to be better than a simple JoinedVector(left,right) 
	 * 
	 * If possible, returns the joined vector. If not, returns null
	 */
	public AVector tryEfficientJoin(AVector second) {
		return null;
	}
	
	@Override
	public INDArray join(INDArray a, int dimension) {
		checkDimension(dimension);
		if (a instanceof AVector) {
			return join((AVector)a);
		}
		if (a.dimensionality()!=1) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		return join(a.asVector());
	}
	
	@Override
	public int compareTo(INDArray a) {
		if (a instanceof AVector) {
			return compareTo((AVector)a);
		} else if (a.dimensionality()!=1) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		} else {
			return compareTo(a.asVector());
		}
	}
	
	/**
	 * Compares this vector to another vector
	 */
	public int compareTo(AVector a) {
		int len=checkSameLength(a);
		
		for (int i=0; i<len; i++) {
			int r= Double.compare(unsafeGet(i),a.unsafeGet(i));
			if (r!=0) return r;
		}
		return 0;
	}
	
	/**
	 * Test for equality on vectors. Returns true iff all values in the vector
	 * are identical
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof AVector) return equals((AVector)o);
		if (o instanceof INDArray) return equals((INDArray)o);
		return false;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (this==v) return true;
		int len=length();
		if (len != v.length())
			return false;
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector dav=(ADenseArrayVector)v;
			return equalsArray(dav.getArray(),dav.getArrayOffset());
		}
		for (int i = 0; i < len; i++) {
			if (!Tools.equals(unsafeGet(i),v.unsafeGet(i)))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(INDArray v) {
		if (v instanceof AVector) return equals((AVector)v);
		if (v.dimensionality()!=1) return false;
		int len=length();
		if (len != v.getShape(0)) return false;
		
		for (int i = 0; i < len; i++) {
			if (unsafeGet(i) != v.get(i))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this vector exactly matches a double[] array.
	 * @param data
	 * @return
	 */
	@Override
	public boolean equalsArray(double[] data) {
		if (length()!=data.length) return false;
		return equalsArray(data,0);
	}
	
	@Override
	public boolean elementsEqual(double value) {
		int length=length();
		for (int i=0; i<length; i++) {
			if (unsafeGet(i)!=value) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this vector exactly matches the elements in double[] array, starting
	 * from the specified offset
	 * 
	 * @param data
	 * @return
	 */
	@Override
	public boolean equalsArray(double[] data, int offset) {
		int len=length();
		for (int i=0; i<len; i++) {
			if (!Tools.equals(unsafeGet(i),data[offset++])) return false;
		}
		return true;
	}
	
	/**
	 * Returns a java.util.List instance containing all elements of this vector
	 * @return
	 */
	public List<Double> toList() {
		ArrayList<Double> al=new ArrayList<Double>(length());
		double[] ds=asDoubleArray();
		if (ds==null) ds=toDoubleArray();
		for (double d: getElements()) {
			al.add(d);
		}
		return al;
	}
	
	@Override
	public boolean epsilonEquals(INDArray a, double tolerance) {
		if (a instanceof AVector) return epsilonEquals((AVector)a,tolerance);
		if (a.dimensionality()!=1) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		int len=length();
		if (len!=a.getShape(0)) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		for (int i = 0; i < len; i++) {
			if (!Tools.epsilonEquals(unsafeGet(i), a.get(i), tolerance)) return false;
		}		
		return true;
	}
		
	/**
	 * Returns true if this vector is approximately equal to another vector.
	 * A small default tolerance is used.
	 * @param v
	 * @return
	 */
	public boolean epsilonEquals(AVector v) {
		return epsilonEquals(v,Vectorz.TEST_EPSILON);
	}
	
	@Override
	public boolean epsilonEquals(AVector v,double tolerance) {
		if (this == v) return true;
		int len=checkSameLength(v);
		
		for (int i = 0; i < len; i++) {
			if (!Tools.epsilonEquals(unsafeGet(i), v.unsafeGet(i), tolerance)) return false;
		}
		return true;
	}
	
	/**
	 * Computes the hashcode of a vector.
	 * 
	 * Currently defined to be equal to List.hashCode for a equivalent list of Double values, 
	 * this may change in future versions.
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		int len=length();
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + (Hash.hashCode(unsafeGet(i)));
		}
		return hashCode;
	}
		
	@Override
	public double[] toDoubleArray() {
		double[] result=new double[length()];
		getElements(result,0);
		return result;
	}
	
	@Override
	public INDArray[] toSliceArray() {
		int n=sliceCount();
		INDArray[] al=new INDArray[n];
		for (int i=0; i<n; i++) {
			al[i]=slice(i);
		}
		return al;
	}
	
	@Override
	public double[] asDoubleArray() {
		return null;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		int len=length();
		for (int i=0; i<len; i++) {
			dest.put(unsafeGet(i));
		}
	}
	
	/**
	 * Copies a the contents of a vector to a vector at the specified offset
	 */
	public void copyTo(AVector dest, int destOffset) {
		copyTo(0,dest,destOffset,length());
	}
	
	/**
	 * Copies a the contents of a vector to a vector at the specified offset
	 */
	public void copyTo(ADenseArrayVector dest, int destOffset) {
		getElements(dest.getArray(),dest.getArrayOffset()+destOffset);
	}
	
	/**
	 * Copies a subset of this vector to a vector at the specified offset
	 */
	public void copyTo(int offset, AVector dest, int destOffset, int length) {
		checkRange(offset,length);
		dest.checkRange(destOffset,length);
		for (int i=0; i<length; i++) {
			dest.unsafeSet(destOffset+i,unsafeGet(offset+i));
		}
	}
	
	/**
	 * Copies a subset of this vector to a specified destination array offset
	 */
	public void copyTo(int offset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]=unsafeGet(i+offset);
		}
	}
	
	/**
	 * Copies a subset of this vector to a specified destination array offset
	 * using the given stride.
	 * 
	 * Unsafe operation: performs no bounds checking
	 */
	public void copyTo(int offset, double[] dest, int destOffset, int length, int stride) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i*stride]=unsafeGet(i+offset);
		}
	}

	/**
	 * Fills the entire vector with a given value
	 * @param value
	 */
	@Override
	public void fill(double value) {
		int len=length();
		for (int i=0; i<len; i++) {
			unsafeSet(i,value);
		}
	}
	
	public void fillRange(int offset, int length, double value) {
		subVector(offset,length).fill(value);
	}
	
	/**
	 * Clamps all values in the vector to a given range
	 * @param value
	 */
	@Override
	public void clamp(double min, double max) {
		int len=length();
		for (int i = 0; i < len; i++) {
			double v=unsafeGet(i);
			if (v<min) {
				unsafeSet(i,min);
			} else if (v>max) {
				unsafeSet(i,max);
			}
		}
	}
	
	public void clampMax(double max) {
		int len=length();
		for (int i = 0; i < len; i++) {
			double v=unsafeGet(i);
			if (v>max) {
				unsafeSet(i,max);
			}
		}
	}
	
	public void clampMin(double min) {
		int len=length();
		for (int i = 0; i < len; i++) {
			double v=unsafeGet(i);
			if (v<min) {
				unsafeSet(i,min);
			} 
		}
	}
	
	/**
	 * Multiplies the vector by a constant factor
	 * @param factor Factor by which to multiply each component of the vector
	 */
	@Override
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			unsafeSet(i,unsafeGet(i)*factor);
		}	
	}
	
	@Override
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
	
	/**
	 * Multiplies this vector by another vector, elementwise.
	 * @param v
	 */
	public void multiply(AVector v) {
		if (v instanceof ADenseArrayVector) {
			multiply((ADenseArrayVector) v);
			return;
		}
		int len=checkSameLength(v);
		
		for (int i = 0; i < len; i++) {
			unsafeSet(i,unsafeGet(i)*v.unsafeGet(i));
		}	
	}
	
	public final void multiply(ADenseArrayVector v) {
		checkSameLength(v);
		
		multiply(v.getArray(),v.getArrayOffset());
	}
	
	/**
	 * Multiplies the elements in this vector by corresponding elements in the target array
	 * @param data
	 * @param offset The offset into the target array
	 */
	public void multiply(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			unsafeSet(i,unsafeGet(i)*data[i+offset]);
		}	
	}
	
	/**
	 * Multiplies the elements in the target array by corresponding elements in this vector
	 * @param data
	 * @param offset The offset into the target array
	 */
	public void multiplyTo(double[] dest, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			dest[i+offset]*=unsafeGet(i);
		}	
	}
	
	@Override
	public AVector multiplyCopy(double d) {
		AVector r= clone();
		r.multiply(d);
		return r;
	}
	
	@Override
	public AVector divideCopy(double d) {
		AVector r = clone();
		r.multiply(1.0/d);
		return r;
	}
	
	@Override
	public INDArray multiplyCopy(INDArray a) {
		if (a instanceof AVector) {
			return multiplyCopy((AVector)a);
		} else if (a.dimensionality()==1) {
			return multiplyCopy(a.asVector());
		} else {
			return multiplyCopy(a.broadcastLike(this));
		}
	}
	
	@Override
	public AVector multiplyCopy(AVector a) {
		AVector r=this.clone();
		r.multiply(a);
		return r;
	}
	
	@Override
	public INDArray divideCopy(INDArray a) {
		int adims=a.dimensionality();
		if (adims==0) {
			return divideCopy(a.get());
		} else if (a instanceof AVector) {
			return divideCopy((AVector)a);
		} else if (adims==1) {
			return divideCopy(a.asVector());
		} else {
			return divideCopy(a.broadcastLike(this));
		}
	}
	
	@Override
	public AVector divideCopy(AVector a) {
		AVector r=this.clone();
		r.divide(a);
		return r;
	}
	
	@Override
	public void divide(double factor) {
		multiply(1.0/factor);
	}
	
	@Override
	public void divide(INDArray a) {
		if (a instanceof AVector) {
			divide((AVector)a);
		} else {
			super.divide(a);
		}
	}
	
	public void divide(AVector v) {
		int len=checkSameLength(v);
		for (int i = 0; i < len; i++) {
			unsafeSet(i,unsafeGet(i)/v.unsafeGet(i));
		}	
	}
	
	/**
	 * Divides the elements in this vector by corresponding elements in the target array
	 * @param data
	 * @param offset The offset into the target array
	 */
	public void divide(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			unsafeSet(i,unsafeGet(i)/data[i+offset]);
		}	
	}
	
	/**
	 * Divides the elements in the target array by corresponding elements in this vector
	 * @param data
	 * @param offset The offset into the target array
	 */
	public void divideTo(double[] data, int offset) {
		int len=length();
		for (int i = 0; i < len; i++) {
			data[i+offset]/=unsafeGet(i);
		}	
	}
	
	@Override
	public void abs() {
		applyOp(Ops.ABS);
	}
	
	@Override
	public AVector absCopy() {
		AVector result=clone();
		result.abs();
		return result;
	}
	
	@Override
	public void absDiff(INDArray a) {
		if (a instanceof AVector) {
			absDiff((AVector)a);
		} else if (a.dimensionality()==0) {
			absDiff(a.get());
		} else {
			absDiff(a.asVector());
		}
	}
	
	public void absDiff(AVector a) {
		sub(a);
		abs();
	}
	
	public void absDiff(double v) {
		sub(v);
		abs();
	}
	
	@Override
	public INDArray absDiffCopy(INDArray a) {
		INDArray res=subCopy(a).mutable();
		res.abs();
		return res;
	}
	
	@Override
	public void log() {
		applyOp(Ops.LOG);
	}
	
	@Override
	public void signum() {
		applyOp(Ops.SIGNUM);
	}
	
	/**
	 * Computes the softmax function over this vector, returning a new mutable vector
	 */
	public AVector softmaxCopy() {
		AVector result=clone();
		result.softmax();
		return result;
	}
	
	/**
	 * Computes the softmax function over this vector, mutating this vector
	 */
	public void softmax() {
		double max=this.maxElement();
		if (max>100) sub(max); // ensure we don't overflow exp calculation
		applyOp(Ops.EXP);
		divide(elementSum());
	}
	
	@Override
	public void square() {
		applyOp(Ops.SQUARE);
	}
	
	@Override
	public AVector squareCopy() {
		AVector r=clone();
		r.square();
		return r;
	}
	
	@Override
	public AVector sqrtCopy() {
		AVector r=clone();
		r.square();
		return r;
	}
	
    /**
     * Computes the tanh of every element in this vector. Mutates this vector.
     */
	public void tanh() {
		int len=length();
		for (int i=0; i<len; i++) {
			double x=unsafeGet(i);
			unsafeSet(i,Math.tanh(x));
		}			
	}
	
    /**
     * Computes the logistic function for every element in this vector. Mutates this vector.
     */
	public void logistic() {
		int len=length();
		for (int i=0; i<len; i++) {
			double x=unsafeGet(i);
			unsafeSet(i,Logistic.logisticFunction(x));
		}			
	}
	
	/**
	 * Computes the reciprocal of all elements in this vector. Mutates this vector
	 */
	@Override
	public void reciprocal() {
		applyOp(Ops.RECIPROCAL);
	}
	
	/**
	 * Computes the reciprocal of all elements in this vector. Mutates this vector
	 */
	@Override
	public AVector reciprocalCopy() {
		AVector result=clone();
		result.reciprocal();
		return result;
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
	
	/**
	 * Scales this vector then adds another vector
	 * @param factor
	 * @param v
	 */
	public void scaleAdd(double factor, AVector v) {
		multiply(factor);
		add(v);
	}
	
	/**
	 * Interpolates between this vector and a second vector, according to the ratio alpha.
	 * alpha = 0.0 returns this vector. 
	 * alpha = 1.0 returns the other vector
	 * Other values perform linear interpolation.
	 * 
	 * Stores the result in this vector
	 * 
	 * @param v
	 * @param alpha
	 */
	public void interpolate(AVector v, double alpha) {
		scaleAdd(1.0-alpha,v,alpha,0.0);
	}
	
	/**
	 * Computes the squared magnitude (sum of squares) for this vector
	 * @return
	 */
	public final double magnitudeSquared() {
		return elementSquaredSum();
	}
	
	@Override
	public final AVector getTranspose() {return this;}
	
	@Override
	public AVector getTransposeCopy() {
		return clone();
	}
	
	@Override
	public final AVector getTransposeView() {return this;}
	
	/**
	 * Selects a subset of indices from a vector
	 * @param indices
	 * @return
	 */
	public AVector select(int... indices) {
		if (IntArrays.isRange(indices)) {
			if (indices.length==0) return Vector0.INSTANCE;
			return subVector(indices[0],indices.length);
		}
		if (isMutable()) {
			return selectView(indices);
		} else {
			return selectClone(indices);
		}		
	}
	
	/**
	 * Selects a subset of indices from a vector, returning a view
	 * @param indices
	 * @return a view of the selected indices
	 */
	public AVector selectView(int... indices) {
		if (IntArrays.isRange(indices)) {
			if (indices.length==0) return Vector0.INSTANCE;
			return subVector(indices[0],indices.length);
		}
		return IndexedSubVector.wrap(this, indices.clone());
	}
	
	/**
	 * Selects a subset of indices from a vector, returning a mutable clone of data
	 * @param indices
	 * @return a new mutable vector containing the selected indices
	 */	
	public AVector selectClone(int... inds) {
		Vector v=Vector.createLength(inds.length);
		double[] tdata=v.getArray();
		for (int i=0; i<inds.length; i++) {
			int ix=inds[i];
			checkIndex(ix);
			tdata[i]=unsafeGet(ix);
		}
		return v;
	}
	
	/**
	 * Computes the outer product of this vector with another vector 
	 * @param a
	 * @return a matrix representing the outer product
	 */
	public AMatrix outerProduct(AVector a) {
		int rc=length();
		int cc=a.length();
		Matrix m=Matrix.create(rc, cc);
		for (int i=0; i<rc; i++) {
			ArraySubVector row=m.getRowView(i);
			row.addMultiple(a, this.unsafeGet(i));
		}
		return m;
	}
	
	@Override
	public final INDArray outerProduct(INDArray a) {
		if (a instanceof AVector) {
			return outerProduct((AVector)a);
		}
		return super.outerProduct(a);
	}
	
	/**
	 * Computes the inner product of this vector with another vector
	 * @return an AScalar instance representing the inner product
	 */
	@Override
	public AScalar innerProduct(AVector v) {
		checkSameLength(v);
		return Scalar.create(dotProduct(v));
	}

	/**
	 * Overloaded inner product for dense array vector argument
	 * @param v
	 * @return
	 */
	public Scalar innerProduct(ADenseArrayVector v) {
		checkLength(v.length());
		return Scalar.create(dotProduct(v.getArray(),v.getArrayOffset()));
	}
	
	/**
	 * Computes the inner product of this vector with a matrix
	 * @param m
	 * @return a vector representing the inner product
	 */
	public AVector innerProduct(AMatrix m) {
		int cc=m.columnCount();
		int rc=m.rowCount();
		checkLength(rc);
		Vector r=Vector.createLength(cc);
		List<AVector> cols=m.getColumns();
		for (int i=0; i<cc; i++) {
			double v=this.dotProduct(cols.get(i));
			r.unsafeSet(i,v);
		}
		return r;		
	}
	
	/**
	 * Computes the inner product of this vector with a scalar
	 * @param s A scalar instance
	 * @return a vector representing the inner product
	 */
	@Override
	public final AVector innerProduct(AScalar s) {
		return scaleCopy(s.get());
	}
	
	@Override
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AVector) {
			return innerProduct((AVector)a);
		} else if (a instanceof AScalar) {
			return innerProduct((AScalar)a);
		} else if (a instanceof AMatrix) {
			return innerProduct((AMatrix)a);
		} else if (a.dimensionality()<=2) {
			return innerProduct(Arrayz.create(a));
		}
		int len=checkLength(a.sliceCount());
		List<INDArray> al=a.getSliceViews();
		INDArray result=Arrayz.newArray(al.get(0).getShape());
		for (int i=0; i<len; i++) {
			double v=unsafeGet(i);
			if (v!=0.0) result.addMultiple(al.get(i),v);
		}
		return result;
	}
	
	@Override
	public final AVector innerProduct(double a) {
		return multiplyCopy(a);
	}
	
	/**
	 * Returns the dot product of this vector with another vector
	 * 
	 * The vectors must have the same length: if not the result is undefined
	 * 
	 * @param v
	 * @return
	 */
	public double dotProduct(AVector v) {
		if (v instanceof ADenseArrayVector) return dotProduct((ADenseArrayVector)v);
		if (v instanceof ASparseVector) return ((ASparseVector)v).dotProduct(this);
		int len=checkSameLength(v);
		double total=0.0;
		for (int i=0; i<len; i++) {
			total+=unsafeGet(i)*v.unsafeGet(i);
		}
		return total;
	}
	
	/**
	 * Returns the dot product of this vector with a target ADenseArrayVector.
	 * 
	 * @param v
	 * @return
	 */
	public double dotProduct(ADenseArrayVector v) {
		v.checkLength(length());
		return dotProduct(v.getArray(), v.getArrayOffset());
	}
	
	/**
	 * Returns the dotProduct of this vector with the elements of another vector mapped to specified indexes in this vector.
	 * 
	 * @param v
	 * @param ix
	 * @return
	 */
	public double dotProduct(AVector v, Index ix) {
		int vl=v.length();
		if (vl!=ix.length()) throw new IllegalArgumentException("Mismatched source vector and index lengths. Index length should be "+vl);
		double result=0.0;
		for (int i=0; i<vl; i++) {
			result+=unsafeGet(ix.get(i))*v.unsafeGet(i);
		}
		return result;
	}
	
	/**
	 * Fast dot product with a double[] array. Performs no bounds checking. 
	 * 
	 * Likely to be faster than most other dot product operations
	 */
	public abstract double dotProduct(double[] data, int offset);
	
	/**
	 * Fast dot product with a double[] array and a stride. Performs no bounds checking. 
	 * 
	 * Likely to be faster than other dot product operations
	 */
	public double dotProduct(double[] data, int offset, int stride) {
		if (stride==1) return dotProduct(data,offset);
		int length=length();
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset]*unsafeGet(i);
			offset+=stride;
		}
		return result;
	}
	
	/**
	 * Computes the crossProduct of this vector with another vector, and stores the result in this vector.
	 * 
	 * Both vectors must have length 3.
	 * 
	 * @param a
	 */
	public void crossProduct(AVector a) {
		if(!(checkSameLength(a)==3)) throw new IllegalArgumentException("Cross product requires length 3 vectors");
		double x=unsafeGet(0);
		double y=unsafeGet(1);
		double z=unsafeGet(2);
		double x2=a.unsafeGet(0);
		double y2=a.unsafeGet(1);
		double z2=a.unsafeGet(2);
		double tx=y*z2-z*y2;
		double ty=z*x2-x*z2;
		double tz=x*y2-y*x2;			
		unsafeSet(0,tx);
		unsafeSet(1,ty);
		unsafeSet(2,tz);		
	}
	
	/**
	 * Computes the crossProduct of this vector with another vector, and stores the result in this vector.
	 * 
	 * Both vectors must have length 3.
	 * 
	 * @param a
	 */
	public void crossProduct(Vector3 a) {
		if(!(length()==3)) throw new IllegalArgumentException("Cross product requires length 3 vectors");
		double x=unsafeGet(0);
		double y=unsafeGet(1);
		double z=unsafeGet(2);
		double x2=a.x;
		double y2=a.y;
		double z2=a.z;
		double tx=y*z2-z*y2;
		double ty=z*x2-x*z2;
		double tz=x*y2-y*x2;			
		unsafeSet(0,tx);
		unsafeSet(1,ty);
		unsafeSet(2,tz);		
	}
	
	/**
	 * Returns the magnitude (Euclidean length) of the vector
	 * @return
	 */
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}
	
	/**
	 * Returns the squared Euclidean distance to another vector.
	 * @param v
	 * @return
	 */
	public double distanceSquared(AVector v) {
		// we rely on the identity:  (a-b)^2  = a^2 + b^2 - 2a.b
		return this.elementSquaredSum()+v.elementSquaredSum()-2*v.dotProduct(this);
	}
	
	/**
	 * Returns the Euclidean distance to another vector.
	 * @param v
	 * @return
	 */
	public double distance(AVector v) {
		return Math.sqrt(distanceSquared(v));
	}
	
	/**
	 * Returns the distance from this vector to another vector according to the L1 (Taxicab) norm.
	 * 
	 * @param v
	 * @return
	 */
	public double distanceL1(AVector v) {
		AVector t=this.subCopy(v).mutable();
		t.abs();
		return t.elementSum();
	}
	
	/**
	 * Returns the distance from this vector to another vector according to the Linf norm.
	 * 
	 * @param v
	 * @return
	 */
	public double distanceLinf(AVector v) {
		AVector t=this.subCopy(v);
		return t.maxAbsElement();
	}
	
	/**
	 * Returns the maximum absolute element value of a vector
	 * @return
	 */
	public double maxAbsElement() {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			double comp=Math.abs(unsafeGet(i));
			if (comp>result) {
				result=comp;
			} 
		}		
		return result;
	}
	
	/**
	 * Returns the index of the maximum absolute element of a vector
	 * @return
	 */
	public int maxAbsElementIndex() {
		int len=length();
		if (len==0) throw new IllegalArgumentException("Can't find maxAbsElementIndex of a 0-length vector");
		int result=0;
		double best=Math.abs(unsafeGet(0));
		for (int i=1; i<len; i++) {
			double comp=Math.abs(unsafeGet(i));
			if (comp>best) {
				result=i;
				best=comp;
			} 
		}		
		return result;
	}
	
	/**
	 * Returns the maximum element value in a vector. Synonym for elementMax()
	 * @return
	 */
	public final double maxElement() {
		return elementMax();
	}
	
	/**
	 * Returns the index of the maximum element of a vector
	 * @return
	 */
	public int maxElementIndex() {
		int len=length();
		if (len==0) throw new IllegalArgumentException("Can't find maxElementIndex of a 0-length vector");
		int result=0;
		double best=unsafeGet(0);
		for (int i=1; i<len; i++) {
			double comp=unsafeGet(i);
			if (comp>best) {
				result=i;
				best=comp;
			} 
		}		
		return result;
	}
	
	/**
	 * Returns the minimum element value in a vector. Synonym for elementMin()
	 */
	public final double minElement() {
		return elementMin();
	}
	
	/**
	 * Returns the index of the minimum element of a vector
	 */
	public int minElementIndex() {
		int len=length();
		if (len==0) throw new IllegalArgumentException("Can't find minElementIndex of a 0-length vector");
		int result=0;
		double best=unsafeGet(0);
		for (int i=1; i<len; i++) {
			double comp=unsafeGet(i);
			if (comp<best) {
				result=i;
				best=comp;
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
		if (scale!=0.0) scale(1.0/scale);
		return scale;
	}
	
	/**
	 * Returns the sum of all elements in a vector
	 * @return
	 */
	@Override
	public double elementSum() {
		int len=length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=unsafeGet(i);
		}		
		return result;
	}
	
	@Override
	public double elementProduct() {
		int len=length();
		double result=1.0;
		for (int i=0; i<len; i++) {
			result*=unsafeGet(i);
		}		
		return result;
	}
	
	@Override
	public double elementMax(){
		return unsafeGet(maxElementIndex());
	}
	
	@Override
	public double elementMin(){
		return unsafeGet(minElementIndex());
	}
	
	@Override
	public double elementSquaredSum() {
		double result=0.0;
		int len=length();
		for (int i=0; i<len; i++) {
			double x=unsafeGet(i);
			result+=x*x;
		}
		return result;
	}
	
	@Override
	public double elementPowSum(double exponent) {
		int n=length();
		double result=0.0;
		for (int i=0; i<n; i++) {
			double x=unsafeGet(i);
			result+=Math.pow(x,exponent);
		}
		return result;
	}
	
	@Override
	public double elementAbsPowSum(double exponent) {
		int n=length();
		double result=0.0;
		for (int i=0; i<n; i++) {
			double x=Math.abs(unsafeGet(i));
			result+=Math.pow(x,exponent);
		}
		return result;
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
	 * @return the magnitude of the original vector
	 */
	@Override
	public double normalise() {
		double d=magnitude();
		if (d>0) multiply(1.0/d);
		return d;
	}
	
	/**
	 * Returns a copy of this vector normalised to a Euclidean length of 1.0
	 * 
	 * Has no effect on a zero-length vector (i.e. it will return a zero length vector)
	 */
	@Override
	public AVector normaliseCopy() {
		double d=magnitude();
		if (d>0.0) return multiplyCopy(1.0/d);
		return copy();
	}
	
	/**
	 * Negates all emlements of this vector in place.
	 */
	@Override
	public void negate() {
		multiply(-1.0);
	}
	
	@Override
	public AVector negateCopy() {
		return multiplyCopy(-1.0);
	}
	
	@Override
	public final AVector scaleCopy(double d) {
		return multiplyCopy(d);
	}
	
	@Override
	public AVector shiftCopy(int shift) {
		if (shift==0) return copy();
		int n=length();
		Vector v=Vector.createLength(n);
		if ((shift>=n)||(shift<=-n)) return v;
		if (shift<0) {
			v.add(-shift,this,0,n+shift);
		} else {
			v.add(0,this,shift,n-shift);
		}
		return v;
	}
	
	@Override
	public void pow(double exponent) {
		if (exponent==1.0) return;
		if (exponent==0.0) {
			set(1.0);
			return;
		}
		if (exponent==2.0) {
			square();
			return;
		}
		// default for arbitrary exponent
		int len=length();
		for (int i=0; i<len; i++) {
			unsafeSet(i,Math.pow(unsafeGet(i),exponent));
		}				
	}
	
	/**
	 * Sets the vector to equal the value of another vector
	 */
	public void set(AVector src) {
		if (src instanceof ADenseArrayVector) {
			set((ADenseArrayVector)src);
		} else {
			int len=length();
			src.checkLength(len);
			for (int i=0; i<len; i++) {
				unsafeSet(i,src.unsafeGet(i));
			}
		}
	}
	
	/**
	 * Sets the vector equal to the value of an ADenseArrayVector
	 * @param v
	 */
	public void set(ADenseArrayVector v) {
		v.checkLength(length());
		setElements(v.getArray(),v.getArrayOffset());
	}
	
	@Override
	public final void set(double a) {
		fill(a);
	}
		
	@Override
	public void setElements(double... data) {
		checkLength(data.length);
		setElements(data,0);
	}
	
	@Override
	public void setElements(double[] data,int offset) {
		setElements(0,data,offset,length());
	}
	
	@Override
	public void set(INDArray a) {
		if (a instanceof AVector) {set((AVector)a); return;}
		if (a.dimensionality()==1) {
			setElements(a.getElements());	
		} else {
			throw new IllegalArgumentException("Cannot set vector using array of dimensonality: "+a.dimensionality());
		}
	}
	
	@Override
	public void setElements(int pos,double[] values, int offset, int length) {
		checkRange(pos,length);
		for (int i=0; i<length; i++) {
			unsafeSet(i+pos,values[offset+i]);
		}
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		copyTo(0,dest,offset,length());
	}
	
	@Override
	public void getElements(Object[] dest, int offset) {
		int n=length();
		for (int i=0; i<n; i++) {
			dest[offset+i]=Double.valueOf(get(i));
		}
	}
	
	/**
	 * Gets the elements in this vector from the specified indices, storing at the specified 
	 * offset in the destination array
	 * @param dest
	 * @param destOffset
	 * @param indices
	 */
	public void getElements(double[] dest, int destOffset, int[] indices) {
		int n=indices.length;
		for (int i=0; i<n; i++) {
			dest[destOffset+i]=unsafeGet(indices[i]);
		}
	}
	
	/**
	 * Set the vector equal to an offset into another vector
	 */
	public void set(AVector src, int srcOffset) {
		set(src.subVector(srcOffset,length()));
	}
	
	/**
	 * Clones the vector, creating a new mutable copy of all data. 
	 * 
	 * The clone is:
	 *  - not guaranteed to be of the same type. 
	 *  - guaranteed to be fully mutable
	 *  - guaranteed not to contain a reference (i.e. is a full deep copy)
	 */
	@Override
	public AVector clone() {
		return Vector.create(this);
	}
	
	@Override
	public AVector copy() {
		if (!isMutable()) return this;
		return clone();
	}
	
	/**
	 * Clones the vector into a sparse mutable format
	 */
	@Override
	public AVector sparseClone() {
		return Vectorz.createSparseMutable(this);
	}
	
	@Override
	public final AVector asVector() {
		return this;
	}
	
	@Override
	public INDArray reshape(int... dimensions) {
		int ndims=dimensions.length;
		if (ndims==1) {
			return Vector.createFromVector(this, dimensions[0]);
		} else if (ndims==2) {
			return Matrixx.createFromVector(this, dimensions[0], dimensions[1]);
		} else {
			return Arrayz.createFromVector(this,dimensions);
		}
	}
	
	@Override
	public final AVector reorder(int[] order) {
		return select(order);	
	}	
	
	@Override
	public final AVector reorder(int dim, int[] order) {
		checkDimension(dim);
		return reorder(order);
	}	
	
	/**
	 * Returns true if this vector is of a view type that references other vectors / data.
	 */
	@Override
	public boolean isView() {
		return true;
	}
	
	/**
	 * Returns true if this vector is mutable.
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
	 */
	@Override
	public boolean isFullyMutable() {
		return isMutable();
	}
	
	/**
	 * Adds another vector to this one
	 */
	public void add(AVector v) {
		if (v instanceof ADenseArrayVector) {
			add((ADenseArrayVector)v);
			return;
		}
		int length=checkSameLength(v);
		for (int i = 0; i < length; i++) {
			addAt(i,v.unsafeGet(i));
		}
	}
	
	/**
	 * Add a DenseArrayVector to this vector
	 * @param v
	 */
	public final void add(ADenseArrayVector v) {
		checkSameLength(v);
		add(v.getArray(),v.getArrayOffset());
	}
	
	@Override
	public final void add(INDArray a) {
		if (a instanceof AVector) {
			add((AVector)a);
		} else if (a instanceof AScalar) {
			add(a.get());
		}else {
			super.add(a);
		}
	}
	
	@Override
	public final INDArray addCopy(INDArray a) {
		if (a instanceof AVector) {
			return addCopy((AVector)a);
		} else if (a.dimensionality()==1) {
			return addCopy(a.asVector());
		} else if (a.dimensionality()==0) {
			return addCopy(a.get());
		} else {
			// must have higher dimensionality
			return a.addCopy(this);
		}
	}
	
	@Override
	public AVector addCopy(AVector a) {
		// clone ensures mutability
		AVector r=this.clone();
		r.add(a);
		return r;
	}
	
	@Override
	public AVector addCopy(double a) {
		// clone ensures mutability
		AVector r=this.clone();
		r.add(a);
		return r;
	}
	
	@Override
	public final INDArray subCopy(INDArray a) {
		if (a instanceof AVector) {
			return subCopy((AVector)a);
		} else if (a.dimensionality()==1) {
			return subCopy(a.asVector());
		} else if (a.dimensionality()==0) {
			return addCopy(-a.get());
		} else {
			return this.broadcastLike(a).subCopy(a);
		}
	}
	
	@Override
	public AVector subCopy(AVector a) {
		// clone ensures mutability
		AVector r=this.clone();
		r.sub(a);
		return r;
	}
	
	@Override
	public void sub(INDArray a) {
		if (a instanceof AVector) {
			sub((AVector)a);
		} else if (a instanceof AScalar) {
			add(-a.get());
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
		src.checkRange(srcOffset, length);
		for (int i = 0; i < length; i++) {
			addAt(i,src.unsafeGet(srcOffset+i));
		}
	}
	
	/**
	 * Adds another vector into this one, at the specified offset
	 * @param offset
	 * @param src
	 */
	public final void add(int offset, AVector src) {
		add(offset,src,0,src.length());
	}
	
	/**
	 * Adds a segment of another vector into this one, at the specified offset
	 * @param offset
	 * @param src
	 */
	public void add(int offset, AVector src, int srcOffset, int length) {
		for (int i = 0; i < length; i++) {
			addAt(offset+i,src.unsafeGet(i+srcOffset));
		}		
	}
	
	/**
	 * Adds the element-wise product of two vectors to this vector. Mutates this vector.
	 * @param a
	 * @param b
	 */
	public void addProduct(AVector a, AVector b) {
		addProduct(a,b,1.0);
	}
	
	/**
	 * Adds the element-wise product of two vectors to this vector.
	 * @param a
	 * @param b
	 * @return A new vector representing the result
	 */
	public AVector addProductCopy(AVector a, AVector b) {
		AVector r=clone();
		r.addProduct(a,b);
		return r;
	}
	
	/**
	 * Adds the element-wise product of two vectors and a constant factor to this vector.
	 * @param a
	 * @param b
	 * @return A new vector representing the result
	 */
	public AVector addProductCopy(AVector a, AVector b, double factor) {
		AVector r=clone();
		r.addProduct(a,b,factor);
		return r;
	}
	
	/**
	 * Adds the element-wise product of two vectors and a constant factor to this vector. 
	 * Mutates this vector.
	 * @param a
	 * @param b
	 */
	public void addProduct(AVector a, AVector b, double factor) {
		checkSameLength(a,b);
		if (factor==0.0) return;
		
		if (a.isSparse()||b.isSparse()) {
			AVector t=a.multiplyCopy(b);
			addMultiple(t,factor);
		} else {
			addProduct(a,0,b,0,factor);
		}
	}
	
	/**
	 * Checks that a vector is the specified length, throws an exception if not.
	 * @param length
	 * @return The length of the vector
	 */
	public int checkLength(int length) {
		int len=length();
		if (len!=length) throw new IllegalArgumentException("Vector length mismatch, expected length = "+length+", but got length = "+len);
		return len;
	}
	
	@Override
	protected final void checkDimension(int dimension) {
		if (dimension !=0) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this,dimension));
		}
	}
	
	@Override
	public void addMultiple(INDArray src, double factor) {
		addMultiple(src.broadcastLike(this),factor);
	}

	/**
	 * Adds a scaled multiple of another vector to this vector
	 * @param src
	 */
	public void addMultiple(AVector src, double factor) {
		if (src instanceof ASparseVector) {
			addMultiple(((ASparseVector)src).toSparseIndexedVector(),factor); 
			return;
		}
		checkSameLength(src);
		addMultiple(src,0,factor);
	}
	
	/**
	 * Adds a scaled multiple of a sparse indexed vector to this vector
	 * @param src
	 */
	public void addMultiple(SparseIndexedVector src, double factor) {
		checkSameLength(src);
        if (factor==0.0) return;
		Index srcIndex=src.nonSparseIndex();	
		if (srcIndex.length()==0) return;
		Vector nonSparseValues=src.nonSparseValues();
		int n=srcIndex.length();
		for (int i=0; i<n; i++) {
			int ix=srcIndex.get(i);
			addAt(ix,nonSparseValues.unsafeGet(i)*factor);
		}	
	}
	
	/**
	 * Returns the sum of this vector with a scaled multiple of a second vector.
	 * @param src
	 * @param factor
	 * @return
	 */
	public AVector addMultipleCopy(AVector src, double factor) {
		AVector r=clone();
		r.addMultiple(src, factor);
		return r;
	}
	
	/**
	 * Adds a multiple of a subvector of another vector to this vector.
	 * 
	 * Useful as a specialised override for joined vectors etc.
	 * @param src
	 * @param srcOffset
	 * @param factor
	 */
	public void addMultiple(AVector src, int srcOffset, double factor) {
		addMultiple(0,src,srcOffset,length(),factor);
	}
	
	public void addMultiple(int offset, AVector src, int srcOffset, int length, double factor) {
		checkRange(offset,length);
		src.checkRange(srcOffset, length);
		if (factor==0.0) return;
		if (factor==1.0) {
			add(offset,src,srcOffset,length);
		} else {
			for (int i = 0; i < length; i++) {
				addAt(i+offset,src.unsafeGet(i+srcOffset)*factor);
			}
		}
	}
	
	public final void addMultiple(int offset, AVector v, double factor) {
		addMultiple(offset,v,0,v.length(),factor);
	}
	
	/**
	 * Updates a weighted average of this vector with another vector
	 * @param v
	 */
	public final void addWeighted(AVector v, double factor) {
		scaleAdd(1.0-factor,v,factor,0.0);
	}
	
	@Override
	public final void scaleAdd(double factor, INDArray b, double bfactor, double constant) {
		switch (b.dimensionality()) {
		case 0: scaleAdd(factor,b.get()*factor+constant); return;
		case 1: scaleAdd(factor,b.asVector(),bfactor,constant); return;
		default: throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, b));
		}
	}
	
	/**
	 * Scales all elements of the array by a given double value, adds a 
	 * scaled second vector and adds a constant value
	 * @param factor
	 * @param b
	 * @param bfactor
	 * @param constant
	 */
	public void scaleAdd(double factor, AVector b, double bfactor, double constant) {
		multiply(factor);
		if (bfactor==0.0) {
			// do nothing 
		} else {
			addMultiple(b,bfactor);
		}
		if (constant!=0.0) add(constant);
	}
	

	@Override
	public void addPower(INDArray src, double exponent) {
		if (src instanceof AVector) {
			addPower((AVector)src,exponent,1.0);
		} else {
			AVector tmp=src.broadcastLike(this);
			tmp=tmp.clone();
			tmp.pow(exponent);
			add(tmp);
		}
	}

	@Override
	public void addPower(INDArray src, double exponent, double factor) {
		if (src instanceof AVector) {
			addPower((AVector)src,exponent,factor);
		} else {
			AVector tmp=src.broadcastLike(this);
			tmp=tmp.clone();
			tmp.pow(exponent);
			tmp.scale(factor);
			add(tmp);
		}
	}
	
	/**
	 * Adds a multiple of the source vector raised to the specified power to this vector
	 * @param src
	 * @param exponent
	 * @param factor
	 */
	protected void addPower(AVector src, double exponent, double factor) {
		AVector tmp=src.clone();
		tmp.pow(exponent);
		addMultiple(tmp,factor);
	}
	
	/**
	 * Subtracts a vector from this vector
	 * @param v
	 */
	public void sub(AVector v) {
		addMultiple(v,-1.0);
	}
	
	/**
	 * Subtracts a value from the specified element in this vector.
	 * 
	 * @param i
	 * @param v
	 */
	public final void subAt(int i, double v) {
		addAt(i,-v);
	}
	
	/**
	 * Returns true if this vector is a zero vector (all components zero)
	 * @return
	 */
	@Override
	public boolean isZero() {
		return isRangeZero(0,length());
	}
	
	/**
	 * Returns true if a sub-vector range is completely zero.
	 * 
	 * Unsafe operation - does not perform bounds checking, results are undefined if sub-vector is out of range
	 * 
	 * @param start
	 * @param length
	 * @return
	 */
	public boolean isRangeZero(int start, int length) {
		for (int i=0; i<length; i++) {
			if (unsafeGet(start+i)!=0.0) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if the vector has unit length
	 * @return
	 */
	public boolean isUnitLengthVector() {
		return isUnitLengthVector(Vectorz.TEST_EPSILON);
	}
	
	/**
	 * Returns true if the vector has unit length with the given tolerance
	 * @return
	 */
	public boolean isUnitLengthVector(double tolerance) {
		double mag=magnitudeSquared();
		return Math.abs(mag-1.0)<=tolerance;
	}
	
	@Override
	public final boolean isSameShape(INDArray a) {
		if (a instanceof AVector) return isSameShape((AVector)a);
		if (a.dimensionality()!=1) return false;
		return length()==a.getShape(0);
	}
	
	@Override
	public boolean isSameShape(AVector a) {
		return length()==a.length();
	}
	
	/**
	 * Utility function to check vector length and throw an exception if not same shape.
	 * @return The length of both vectors (which must be equals)
	 */
	protected int checkSameLength(AVector v) {
		int len=length();
		if (len!=v.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));		
		return len;
	}
	
	/**
	 * Utility function to check vector length and throw an exception if not same shape.
	 * @return The length of both vectors (which must be equals)
	 */
	protected int checkSameLength(AVector v, AVector w) {
		int len=length();
		if (len!=v.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));		
		if (len!=w.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, w));		
		return len;
	}
	
	/**
	 * Utility function to check vector range and throw an exception if not valid.
	 * 
	 * Returns the length of this vector
	 */
	public int checkRange(int offset, int length) {
		int len=this.length();
		int end=offset+length;
		if ((offset<0)||(end>len)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		return len;
	}
	
	/**
	 * Utility function to check an index and throw an exception in not in bounds.
	 * Returns the vector length
	 */
	public int checkIndex(int i) {
		int len=length();
		if ((i<0)||(i>=len)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return len;
	}
	
	protected int checkSameLength(ASizedVector v) {
		int len=length();
		if (len!=v.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));		
		return len;
	}
	
	/**
	 * Projects the vector to the plane defined by: x.normal=distance
	 * @param normal A Vector of unit length
	 * @param distance 
	 */
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
		if (elementCount()>Constants.PRINT_THRESHOLD) {
			Index shape=Index.create(getShape());
			return "Large vector with shape: "+shape.toString();
		}
		return toStringFull();
	}
	
	@Override
	public String toStringFull() {
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
	
	/**
	 * Coerces this vector to the standard dense Vector format.
	 * 
	 * May return the same vector is this is already a dense Vector, otherwise returns a Vector
	 * containing a new clone of the vector elements.
	 */
	@Override
	public Vector toVector() {
		return Vector.create(this);
	}
	
	/**
	 * Creates an immutable copy of a vector
	 * @return
	 */
	@Override
	public AVector immutable() {
		if (!isMutable()) return this;
		return ImmutableVector.create(this);
	}
	
	/**
	 * Coerces to a mutable version of a vector. May or may not be a copy,
	 * but guaranteed to be fully mutable
	 * @return
	 */
	@Override
	public AVector mutable() {
		if (this.isFullyMutable()) {
			return this;
		} else {
			return this.clone();
		}
	}
	
	@Override
	public AVector sparse() {
		if (this instanceof ISparse) return this;
		return Vectorz.createSparse(this);
	}
	
	@Override
	public AVector dense() {
		return denseClone();
	}
	
	@Override
	public final Vector denseClone() {
		return Vector.wrap(this.toDoubleArray());
	}
	
	/**
	 * Creates a new mutable vector representing the normalised value of this vector
	 * 
	 * Returns null if the vector cannot be normalised (i.e. is a zero length vector)
	 * @return
	 */
	public AVector toNormal() {
		double dd=elementSquaredSum();
		if (dd==0.0) return null;
		return scaleCopy(1.0/Math.sqrt(dd));
	}
	
	@Override
	public List<Double> asElementList() {
		return new ListWrapper(this);
	}
	
	@Override
	public Iterator<Double> iterator() {
		return new VectorIterator(this);
	}
	
	@Override
	public final Iterator<Double> elementIterator() {
		return iterator();
	}

	/**
	 * Adds source vector to this vector at the specified indexes which should map from source->this
	 * @param source
	 * @param sourceToDest
	 * @param factor
	 */
	public void addMultiple(Vector source, Index sourceToDest, double factor) {
		int len=source.length();
		if (len!=sourceToDest.length()) throw new IllegalArgumentException("Index length must match source length.");
		double[] data=source.getArray();
		for (int i=0; i<len; i++) {
			int j=sourceToDest.data[i];
			this.addAt(j,data[i]*factor);
		}
	}
	
	@Override
	public void addSparse(double c) {
		add(c);
	}
	
	/**
	 * Adds the corresponding elements of the source vector to the non-sparse elements of this vector
	 * @param v
	 */
	public void addSparse(AVector v) {
		add(v);
	}
	
	@Override
	public void setSparse(INDArray src) {
		int sdims=src.dimensionality();
		if (sdims==1) {
			setSparse(src.asVector());
		} else {
			super.setSparse(src);
		}
	}
	
	/**
	 * Sets the non-sparse elements of this vector to the corresponding elements of another vector
	 * @param v
	 */
	public void setSparse(AVector src) {
		set(src);
	}
	
	@Override
 	public void setSparse(double v) {
		set(v);
	}
	
	@Override
	public void addMultipleSparse(INDArray a, double factor) {
		if (a instanceof AVector) {
			addMultipleSparse((AVector)a,factor);
			return;
		} 
		int dims=a.dimensionality();
		if (dims==0) {
			addSparse(factor*a.get());
		} else if (dims==1) {
			addMultipleSparse(a.asVector(),factor);
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
	}
	
	/**
	 * Adds a multiple of a source vector to the non-sparse elements of this vector only
	 * @param v
	 * @param factor
	 */
	public void addMultipleSparse(AVector v, double factor) {
		// default is just a simple addMultiple
		addMultiple(v,factor);
	}
	
	/**
	 * Adds a source vector to this vector at the specified indexes which should map from source->this
	 * @param source
	 * @param sourceToDest
	 * @param factor
	 */
	public void addMultiple(AVector source, Index sourceToDest, double factor) {
		int len=source.length();
		if (len!=sourceToDest.length()) throw new IllegalArgumentException("Index length must match source length.");
		for (int i=0; i<len; i++) {
			int j=sourceToDest.data[i];
			this.addAt(j,source.unsafeGet(i)*factor);
		}
	}

	/**
	 * sets the vector using values indexed from another vector
	 */
	public final void set(AVector source, Index indexes) {
		set(source,indexes.data);
	}
	
	/**
	 * sets the vector using values indexed from another vector
	 */
	public void set(AVector source, int[] indexes) {
		int len=length();
		if (len!=indexes.length) throw new IllegalArgumentException("Index length must match this vector length.");
		for (int i=0; i<len ; i++) {
			unsafeSet(i, source.get(indexes[i]));
		}
	}
	
	/**
	 * Adds this vector to a double[] array, starting at the specified offset.
	 * 
	 * @param array
	 * @param offset
	 */
	@Override
	public void addToArray(double[] array, int offset) {
		addToArray(0,array,offset,length());
	}
	
	/**
	 * Adds this vector to a double[] array, using the specified offset and stride into the destination array
	 */
	public void addToArray(double[] dest, int destOffset, int destStride) {
		int n=length();
		for (int i=0; i<n; i++) {
			dest[destOffset+i*destStride]+=unsafeGet(i);
		}
	}

	/**
	 * Adds a subvector of this vector into a double array at the specified offset
	 * @param offset
	 * @param array
	 * @param arrayOffset
	 * @param length
	 */
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		checkRange(offset,length);
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=unsafeGet(i+offset);
		}
	}
	
	/**
	 * Adds a multiple of this vector into a double array at the specified offset
	 * @param offset
	 * @param array
	 * @param arrayOffset
	 * @param length
	 */
	public void addMultipleToArray(double factor, double[] array, int arrayOffset) {
		addMultipleToArray(factor,0,array,arrayOffset,length());
	}
	
	/**
	 * Adds a multiple of a subvector of this vector into a double array at the specified offset
	 * @param offset
	 * @param array
	 * @param arrayOffset
	 * @param length
	 */
	public void addMultipleToArray(double factor, int offset, double[] array, int arrayOffset, int length) {
		checkRange(offset,length);
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*unsafeGet(i+offset);
		}
	}
	
	public void addProductToArray(double factor, int offset, AVector other,int otherOffset, double[] array, int arrayOffset, int length) {
		if (other instanceof ADenseArrayVector) {
			addProductToArray(factor,offset,(ADenseArrayVector)other,otherOffset,array,arrayOffset,length);
			return;
		}
		checkRange(offset,length);
		for (int i=0; i<length; i++) {
			array[i+arrayOffset]+=factor*unsafeGet(i+offset)*other.unsafeGet(i+otherOffset);
		}		
	}
	
	/**
	 * Adds the scaled elementwise product of this vector and another vector to the destimation array
	 * @param factor
	 * @param offset
	 * @param other
	 * @param otherOffset
	 * @param dest
	 * @param destOffset
	 * @param length
	 */
	public void addProductToArray(double factor, int offset, ADenseArrayVector other,int otherOffset, double[] dest, int destOffset, int length) {
		checkRange(offset,length);
		other.checkRange(otherOffset,length);
		double[] otherArray=other.getArray();
		int otherArrayOffset=otherOffset+other.getArrayOffset();
		for (int i=0; i<length; i++) {
			dest[i+destOffset]+=factor*unsafeGet(i+offset)*otherArray[i+otherArrayOffset];
		}		
	}

	/**
	 * Adds the scaled elementwise product of two vectors to this vector
	 * @param a
	 * @param aOffset
	 * @param b
	 * @param bOffset
	 * @param factor
	 */
	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		int length=length();
		a.checkRange(aOffset, length);
		b.checkRange(bOffset, length);
		for (int i=0; i<length; i++) {
			addAt(i, (a.unsafeGet(i+aOffset)* b.unsafeGet(i+bOffset)*factor));
		}
	}
	
	@Override
	public final void setInnerProduct(INDArray a, INDArray b) {
		if ((a instanceof AMatrix)&&(b instanceof AVector)) {
			setInnerProduct((AMatrix)a,(AVector)b);
			return;
		}
		if ((b instanceof AMatrix)&&(a instanceof AVector)) {
			setInnerProduct((AVector)a,(AMatrix)b);
			return;
		}
		set(a.innerProduct(b));
	}
	
	@Override
	public void addInnerProduct(AMatrix a, INDArray b) {
		if (b.dimensionality()!=1) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, b));
		addInnerProduct(a,b.asVector());
	}	
	
	@Override
	public final void addInnerProduct(INDArray a, INDArray b) {
		if ((a instanceof AMatrix)&&(b instanceof AVector)) {
			addInnerProduct((AMatrix)a,(AVector)b);
			return;
		}
		if ((b instanceof AMatrix)&&(a instanceof AVector)) {
			addInnerProduct((AVector)a,(AMatrix)b);
			return;
		}

		super.addInnerProduct(a, b);
	}
	
	/**
	 * Adds the inner product of the arguments (matrix a and vector b) to this vector.
	 * 
	 * @param a A matrix with the same number or rows as this vector
	 * @param b A vector with the same lengths as the number of columns in the parameter a
	 */
	public void addInnerProduct(AMatrix a, AVector b) {
		int length=this.length();
		if (length!=a.rowCount()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		if (b.length()!=a.columnCount()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a, b));
		
		for (int i=0; i<length; i++) {
			double v=a.rowDotProduct(i,b);
			addAt(i,v);
		}
	}
	
	/**
	 * Adds the inner product of the arguments (vector a and matrix b) to this vector.
	 * 
	 * @param a A vector with the same lengths as the number of rows in the matrix parameter b
	 * @param b A matrix with the same number of columns as this vector
	 */
	public void addInnerProduct(AVector a, AMatrix b) {
		addInnerProduct(b.getTranspose(),a);
	}
	
	/**
	 * Sets this vector to the inner product of the arguments (matrix a and vector b).
	 * 
	 * @param a A matrix with the same number or rows as this vector
	 * @param b A vector with the same lengths as the number of columns in the parameter a
	 */
	public void setInnerProduct(AMatrix a, AVector b) {
		a.transform(b, this);
	}
	
	/**
	 * Sets this vector to the inner product of the arguments (vector a and matrix b) to this vector.
	 * 
	 * @param a A vector with the same lengths as the number of rows in the matrix parameter b
	 * @param b A matrix with the same number of columns as this vector
	 */
	public void setInnerProduct(AVector a, AMatrix b) {
		setInnerProduct(b.getTranspose(),a);
	}
	
	@Override
	public final void setMultiple(INDArray a, INDArray b) {
		int bdims=b.dimensionality();
		if (bdims==0) {
			setMultiple(a,b.get());
		} else if ((bdims==1)&&a.dimensionality()==1) {
			setMultiple(a.asVector(), b.asVector());
		} else {
			set(a);
			multiply(b);
		}
	}
	
	/**
	 * Sets this vector to the element-wise product of two otehr vectors
	 * @param a
	 * @param b
	 */
	public final void setMultiple(AVector a, AVector b) {
		set(a);
		multiply(b);
	}

	@Override
	public final void setMultiple(INDArray a, double b) {
		if (a instanceof AVector) {
			setMultiple((AVector)a,b);
		} else if (a.dimensionality()==1){
			set(a.get()*b);
		} else {
			set(a);
			scale(b);
		}
	}
	
	/**
	 * Sets this vector to be a multiple of another vector
	 * @param a
	 * @param b
	 */
	public final void setMultiple(AVector a, double b) {
		set(a);
		scale(b);
	}

	@Override
	public void applyOp(IOperator op) {
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
	
	@Override
	public void applyOp(Op2 op, INDArray b) {
		int dims=b.dimensionality();
		switch (dims) {
		   case 0: applyOp(op,b.get()); return;
		   case 1: applyOp(op,b.asVector()); return;
		   default: throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, b));
		}
	}
	
	/**
	 * Applies a binary operator to this vector and a second vector
	 * @param op
	 * @param b
	 */
	public void applyOp(Op2 op, AVector b) {
		int len=length();
		b.checkLength(len);
		for (int i=0; i<len; i++) {
			double v=this.unsafeGet(i);
			double nv=op.apply(v, b.unsafeGet(i));
			if (v!=nv) this.unsafeSet(i,nv);
		}
	}
	
	@Override
	public void applyOp(Op2 op, double b) {
		int len=length();
		for (int i=0; i<len; i++) {
			double v=unsafeGet(i);
			double nv=op.apply(v, b);
			if (v!=nv) unsafeSet(i,nv);
		}
	}
	
	@Override
	public AVector applyOpCopy(Op op) {
		AVector r=clone();
		r.applyOp(op);
		return r;
	}
	
	@Override
	public final void setApplyOp(Op op, INDArray a) {
		int dims=a.dimensionality();
		switch (dims) {
		    case 0: setApplyOp(op,a.get()); return;
			case 1: setApplyOp(op,a.asVector()); return;
			default: throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
	}
	
	/**
	 * Sets this vector to the result of applying an operator to another vector
	 * @param op
	 * @param a
	 */
	public void setApplyOp(Op op, AVector a) {
		checkSameLength(a);
		set(a);
		applyOp(op);
	}
	
	/**
	 * Sets this vector to the result of applying an operator to a double value
	 * @param op
	 * @param a
	 */
	public void setApplyOp(Op op, double a) {
		if (op.isStochastic()) {
			// we need to handle the stochastic case which may result in different values for each element
			set(a);
			applyOp(op);
		} else {
			// fill with a single value
			fill(op.apply(a));
		}
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		int n=length();
		double result=init;
		for (int i=0; i<n; i++) {
			result=op.apply(result, unsafeGet(i));
		}
		return result;
	}
	
	@Override
	public double reduce(Op2 op) {
		int n=length();
		double result=get(0);
		for (int i=1; i<n; i++) {
			result=op.apply(result, unsafeGet(i));
		}
		return result;
	}
	
	@Override
	public final AVector reduceSlices(Op2 op) {
		return this.clone();
	}
	
	@Override
	public AVector reduceSlices(Op2 op, double init) {
		AVector result=clone();
		int len=length();
		for (int i=0; i<len; i++) {
			result.unsafeSet(i,op.apply(init,result.unsafeGet(i)));
		}
		return result;
	}
	
	/**
	 * Adds a value to a specific element of the vector
	 * 
	 * This function does not perform bounds checking, i.e. is an unsafe operation
	 * 
	 * @param i
	 * @param v
	 */
	public void addAt(int i, double v) {
		if (v==0.0) return;
		unsafeSet(i,unsafeGet(i)+v);
	}

	@Override
	public void addAt(long i, double v) {
		addAt(Tools.toInt(i),v);
	}

	/**
	 * Scales this vector and adds a constant to every element
	 */
	@Override
	public void scaleAdd(double factor, double constant) {
		if (factor==0.0) {
			set(constant);
		} else {
			if (factor!=1.0) multiply(factor);
			if (constant!=0.0) add(constant);
		} 
	}

	@Override
	public void add(double constant) {
		if (constant==0.0) return;
		int len=length();
		for (int i=0; i<len; i++) {
			addAt(i,constant);
		}
	}
	
	/**
	 * Adds to this vector from a given double array.
	 * @param data
	 * @param offset
	 */
	public void add(double[] data, int offset) {
		int len=length();
		for (int i=0; i<len; i++) {
			double v=data[i+offset];
			addAt(i,v);
		}
	}
	
	/**
	 * Adds the values from a double[] array to this vector
	 * @param data
	 */
	public void add(double[] data) {
		checkLength(data.length);
		add(data,0);
	}
	
	/**
	 * Returns an exact clone of this vector, i.e. of the same type
	 * @return
	 */
	@Override
	public abstract AVector exactClone();
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims==0) throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));
		
		int len=this.length();
		if (targetShape[tdims-1]!=len) throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));

		if (tdims==1) {
			return this;
		} else if (tdims==2) {
			int rows=targetShape[0];
			return BroadcastVectorMatrix.wrap(this, rows);
		} else {
			int n=targetShape[0];
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,n);
		}
	}
	
	@Override
	public INDArray broadcastLike(INDArray target) {
		if (target instanceof AVector) {
			return broadcastLike((AVector)target);
		} else if (target instanceof AMatrix) {
			return broadcastLike((AMatrix)target);
		}
		return broadcast(target.getShape());
	}
	
	@Override
	public AVector broadcastLike(AVector target) {
		if (this.length()==target.length()) {
			return this;
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, target));
		}
	}
	
	@Override
	public AMatrix broadcastLike(AMatrix target) {
		int cc=target.columnCount();
		if (length()==cc) {
			int rc=target.rowCount();
			if (rc==1) return RowMatrix.wrap(this);
			return BroadcastVectorMatrix.wrap(this, rc);
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, target));
		}
	}
	
	@Override
	public void validate() {
		if (length()<0) throw new VectorzException("Illegal length! Length = "+length());
		super.validate();
	}

	/**
	 * Returns an Index indicating which elements of this vector are defined as non-sparse.
	 * 
	 * A sparse index must be zero. A non-sparse element may be zero, but is expected be non-zero.
	 * 
	 * @return
	 */
	public Index nonSparseIndex(){
		// by default we just use the non-zero indices
		return Index.of(nonZeroIndices());
	}
	
	/**
	 * Return an int array specifying the positions in this vector which are non-zero
	 * 
	 * @return
	 */
	public int[] nonZeroIndices() {
		int n=(int)nonZeroCount();
		if (n==0) return IntArrays.EMPTY_INT_ARRAY;
		int[] ret=new int[n];
		int length=length();
		int di=0;
		for (int i=0; i<length; i++) {
			if (unsafeGet(i)!=0.0) ret[di++]=i;
		}
		if (di!=n) throw new VectorzException("Invalid non-zero index count. Maybe concurrent modification?");
		return ret;
	}

	@Override
	public boolean hasUncountable() {
		int len = length();
		for(int i=0; i<len; i++) {
			double v=unsafeGet(i);
			if (Vectorz.isUncountable(v)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Visits all non-zero elements of this vector. 
	 * 
	 * Visitor may return a non-zero value to terminate early, which will return the given value.
	 * Returns 0.0 otherwise.
	 * 
	 * @param elementVisitor
	 */
	public abstract double visitNonZero(IndexedElementVisitor elementVisitor);



}
