package mikera.vectorz;

import java.nio.DoubleBuffer;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.INDArray;
import mikera.arrayz.impl.AbstractArray;
import mikera.arrayz.impl.BroadcastScalarArray;
import mikera.arrayz.impl.IDense;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.SingleDoubleIterator;
import mikera.vectorz.impl.WrappedScalarVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.LongArrays;

/**
 * Abstract base class for scalar arrays that represent a wrapped 0-d scalar value.
 * 
 * These may be a 0-d view into another vector/matrix/array
 * 
 * @author Mike
 */
public abstract class AScalar extends AbstractArray<Object> implements IScalar, IDense {
	private static final long serialVersionUID = -8285351135755012093L;
	private static final int[] SCALAR_SHAPE=IntArrays.EMPTY_INT_ARRAY;
	private static final long[] SCALAR_LONG_SHAPE=LongArrays.EMPTY_LONG_ARRAY;

	@Override
	public abstract double get();
	
	@Override
	public abstract void set(double value);
	
	@Override
 	public void setSparse(double v) {
		set(v);
	}
	
	@Override
	public double get(int x) {
		throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, x));
	}

	@Override
	public double get(int x, int y) {
		throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, x));
	}
	
	@Override
	public void setElements(int pos,double[] values, int offset, int length) {
		if (length==0) return;
		if (length!=1) {
			throw new IllegalArgumentException("Length must be 0 or 1");
		}
		if (pos!=0) throw new IllegalArgumentException("Element position must be zero for any scalar");
		set(values[offset]);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		dest[offset]=get();
	}
	
	@Override
	public final AScalar getTranspose() {return this;}
	
	@Override
	public final AScalar getTransposeView() {return this;}

	
	@Override
	public final int dimensionality() {
		return 0;
	}
	
	@Override
	public INDArray slice(int position) {
		throw new UnsupportedOperationException("Can't slice a scalar!");
	}
	
	@Override
	public INDArray slice(int dimension, int index) {
		throw new UnsupportedOperationException("Can't slice a scalar!");
	}	
	
	@Override
	public final int sliceCount() {
		return 0;
	}
	
	@Override
	public List<Object> getSlices() {
		throw new UnsupportedOperationException("Can't slice a scalar!");
	}
	
	@Override
	public AScalar subArray(int[] offsets, int[] shape) {
		if (offsets.length!=0) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		if (shape.length!=0) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		return this;
	}

	@Override
	public Iterator<Object> iterator() {
		throw new UnsupportedOperationException("Can't slice a scalar!");
	}
	
	@Override
	public boolean isMutable() {
		// scalars are generally going to be mutable, so express this in default
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return isMutable();
	}
	
	@Override
	public boolean isElementConstrained() {
		return false;
	}
	
	@Override
	public boolean isZero() {
		return get()==0.0;
	}
	
	@Override
	public boolean isBoolean() {
		return Tools.isBoolean(get());
	}

	@Override
	public void add(double d) {
		set(get()+d);
	}
	
	@Override
	public AScalar addCopy(double d) {
		return Scalar.create(get()+d);
	}
	
	@Override
	public final INDArray addCopy(INDArray a) {
		return a.addCopy(get());
	}

	@Override
	public void addAt(long i, double v) {
		// Note: this is an unsafe operation, so ignore the index
		add(v);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		data[offset]+=get();
	}
	
	@Override
	public final void addInnerProduct(INDArray a, INDArray b) {
		int adims=a.dimensionality();
		int bdims=b.dimensionality();
		if ((adims==1)&&(bdims==1)) {
			add(a.asVector().dotProduct(b.asVector()));
		} else if ((adims==0)&&(bdims==0)) {
			add(a.get()*b.get());
		} else {
			throw new IllegalArgumentException("Inner product must be a scalar");
		}
	}
	
	@Override
	public void addInnerProduct(INDArray a, INDArray b, double d) {
		int adims=a.dimensionality();
		int bdims=b.dimensionality();
		if ((adims==1)&&(bdims==1)) {
			add(d*a.asVector().dotProduct(b.asVector()));
		} else if ((adims==0)&&(bdims==0)) {
			add(d*a.get()*b.get());
		} else {
			throw new IllegalArgumentException("Inner product must be a scalar");
		}
	}
	
	@Override
	public void addMultiple(INDArray src, double factor) {
		add(src.get()*factor);
	}
	
	@Override
	public void addPower(INDArray src, double factor) {
		add(Math.pow(src.get(),factor));
	}
	
	@Override
	public void sub(double d) {
		set(get()-d);
	}
	
	@Override
	public void add(INDArray a) {
		add(a.get());
	}
	
	@Override
	public void sub(INDArray a) {
		sub(a.get());
	}
		
	@Override
	public void negate() {
		set(-get());
	}
	
	@Override
	public void square() {
		double v=get();
		set(v*v);
	}
	
	@Override
	public void pow(double exponent) {
		double v=get();
		set(Math.pow(v,exponent));
	}
	
	@Override
	public void clamp(double min, double max) {
		double v=get();
		if (v<min) {
			set(min);
		} else if (v>max) {
			set(max);
		}
	}
	
	@Override
	public INDArray innerProduct(INDArray a) {
		return a.multiplyCopy(get());
	}
	
	@Override
	public Scalar innerProduct(AScalar a) {
		return Scalar.create(get()*a.get());
	}
	
	@Override
	public Scalar innerProduct(double a) {
		return Scalar.create(get()*a);
	}
	
	@Override
	public AVector innerProduct(AVector a) {
		return a.multiplyCopy(get());
	}
	
	@Override
	public INDArray outerProduct(INDArray a) {
		return a.multiplyCopy(get());
	}
	
	@Override 
	public double get(int... indexes) {
		if (indexes.length!=0) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, indexes));
		return get();
	}
	
	@Override 
	public void set(int[] indexes, double value) {
		if (indexes.length==0) {
			set(value);
		} else {
			throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, indexes));
		}
	}
	
	@Override
	public int[] getShape() {
		return SCALAR_SHAPE;
	}
	
	@Override
	public int getShape(int dim) {
		throw new IndexOutOfBoundsException("Scalar does not have dimension: "+dim);
	}
	
	@Override
	public long[] getLongShape() {
	 	return SCALAR_LONG_SHAPE;
	}
	
	@Override
	public final long elementCount() {
		return 1;
	}
	
	@Override
	public long nonZeroCount() {
		return (get()==0)?0:1;
	}
	
	@Override
	public double getElement(long i) {
		if (i!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidElementIndex(this, i));
		return get();
	}
	
	@Override
	public void getElements(double[] arr) {
		if (arr.length!=1) throw new IllegalArgumentException(ErrorMessages.wrongElementCount());
		arr[0]=get();
	}
	
	@Override
	public void setElements(double... values) {
		int vl=values.length;
		if (vl!=1) throw new IllegalArgumentException("Wrong number of elements in source array: "+vl);
		set(values[0]);
	}
	
	@Override
	public void setElements(double[] values, int offset) {
		set(values[offset]);
	}
	
	@Override
	public AVector asVector() {
		return new WrappedScalarVector(this);
	}
	
	@Override
	public INDArray reshape(int... dimensions) {
		return asVector().reshape(dimensions);
	}
	
	@Override
	public void applyOp(IOperator op) {
		set(op.apply(get()));
	}
	
	@Override
	public void applyOp(Op op) {
		set(op.apply(get()));
	}
	
	@Override
	public void setApplyOp(Op op, INDArray a) {
		set(op.apply(a.get()));
	}
	
	@Override
	public void applyOp(Op2 op, INDArray b) {
		applyOp(op,b.get());
	}

	@Override
	public void applyOp(Op2 op, double b) {
		set(op.apply(get(),b));
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		return op.apply(init, get());
	}
	
	@Override
	public final double reduce(Op2 op) {
		return get();
	}
	
	@Override
	public Scalar clone() {
		return Scalar.create(get());
	}
	
	@Override 
	public void multiply(double factor) {
		set(factor*get());
	}
	
	@Override
	public void divide(double factor) {
		set(get()/factor);
	}
	
	@Override 
	public void multiply(INDArray a) {
		multiply(a.get());
	}
	
	@Override 
	public final INDArray multiplyCopy(INDArray a) {
		return a.multiplyCopy(get());
	}
	
	@Override
	public final double elementSum() {
		return get();
	}
	
	@Override
	public final double elementProduct() {
		return get();
	}
	
	@Override
	public double elementMax(){
		return get();
	}
	
	@Override
	public double elementMin(){
		return get();
	}
	
	@Override public final double elementSquaredSum() {
		double value=get();
		return value*value;
	}
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims==0) {
			return this;
		} 
		// note that BroadcastScalarArray handles vector and matrix special cases
		double value=get();
		return BroadcastScalarArray.create(value,targetShape);
	}
	
	@Override
	public INDArray broadcastLike(INDArray v) {
		if (v instanceof AVector) return broadcastLike((AVector) v);
		if (v instanceof AMatrix) return broadcastLike((AMatrix) v);
		return broadcast(v.getShape());
	}
	
	@Override
	public AVector broadcastLike(AVector v) {
		return Vectorz.createRepeatedElement(v.length(), get());
	}
	
	@Override
	public AMatrix broadcastLike(AMatrix v) {
		return Vectorz.createRepeatedElement(v.columnCount(), get()).broadcastLike(v);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AScalar) {
			return equals((AScalar)o);
		} else if (o instanceof INDArray) {
			return equals((INDArray) o);
		}
		return false;
	}
		
	@Override
	public boolean epsilonEquals(INDArray a, double epsilon) {
		if (a.dimensionality()!=0) {
			return false;
		} else {
			return Tools.epsilonEquals(get(), a.get(), epsilon);
		}
	}
	
	@Override
	public int compareTo(INDArray a) {
		return Double.compare(get(), a.get());
	}
	
	@Override
	public boolean equals(INDArray o) {
		return (o.dimensionality()==0)&&(Tools.equals(o.get(),get()));
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return Tools.equals(data[offset],get());
	}
	
	/**
	 * Returns true if this scalar is equal in value to another scalar.
	 * 
	 * @param o
	 * @return
	 */
	public boolean equals(AScalar o) {
		return get()==o.get();
	}

	@Override
	public int hashCode() {
		return 31+Hash.hashCode(get());
	}
	
	@Override
	public String toString() {
		return Double.toString(get());
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new SingleDoubleIterator(get());
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(get());
	}
	
	@Override
	public double[] asDoubleArray() {
		return null;
	}
	
	@Override
	public double[] toDoubleArray() {
		return new double[] {get()};
	}
	
	@Override
	public INDArray[] toSliceArray() {
		throw new UnsupportedOperationException(ErrorMessages.noSlices(this));
	}
	
	@Override
	public abstract AScalar exactClone();
	
	@Override
	public AScalar mutable() {
		if (isFullyMutable()) {
			return this;
		} else {
			return Scalar.create(get());
		}
	}
	
	@Override
	public boolean elementsEqual(double value) {
		return get()==value;		
	}
	
	@Override
	public AScalar sparse() {
		double v=get();
		if (v==0.0) return ImmutableScalar.ZERO;
		if (v==1.0) return ImmutableScalar.ONE;
		return this;
	}
	
	@Override
	public INDArray dense() {
		return this;
	}
	
	@Override
	public final Scalar denseClone() {
		return Scalar.create(get());
	}
	
	@Override
	public final Scalar sparseClone() {
		return Scalar.create(get());
	}

	@Override
	public ImmutableScalar immutable() {
		return ImmutableScalar.create(get());
	}
	
	@Override
	public void validate() {
		get();
		super.validate();
	}
	
	@Override
	protected final void checkDimension(int dimension) {
		// scalar has no valid dimensions, so always an error
		throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this,dimension));
	}

	@Override
	public void abs() {
		set(Math.abs(get()));
	}

	@Override
	public boolean hasUncountable() {
		return Vectorz.isUncountable(get());
	}
}
