package mikera.vectorz;

import java.nio.DoubleBuffer;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.INDArray;
import mikera.arrayz.impl.AbstractArray;
import mikera.arrayz.impl.IDense;
import mikera.matrixx.AMatrix;
import mikera.randomz.Hash;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SingleDoubleIterator;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.impl.WrappedScalarVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.LongArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Class to represent a wrapped 0-d scalar value.
 * 
 * Can be a view into another vector/matrix/array
 * 
 * @author Mike
 */
public abstract class AScalar extends AbstractArray<Object> implements IScalar, IDense {
	private static final long serialVersionUID = -8285351135755012093L;
	private static final int[] SCALAR_SHAPE=IntArrays.EMPTY_INT_ARRAY;
	private static final long[] SCALAR_LONG_SHAPE=LongArrays.EMPTY_LONG_ARRAY;

	public abstract double get();
	
	public void set(double value) {
		throw new UnsupportedOperationException();
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
	public AScalar getTranspose() {return this;}
	
	@Override
	public final AScalar getTransposeView() {return this;}

	
	@Override
	public int dimensionality() {
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
	public int sliceCount() {
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
	public void add(double d) {
		set(get()+d);
	}

	@Override
	public void addAt(int i, double v) {
		// Note: this is an unsafe operation, so ignore the index
		add(v);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		data[offset]+=get();
	}
	
	@Override
	public void sub(double d) {
		set(get()-d);
	}
	
	public void add(AScalar s) {
		set(get()+s.get());
	}
	
	@Override
	public void add(INDArray a) {
		if ((a instanceof AScalar)||(a.dimensionality()==0)) {
			add(a.get());
		} else {
			super.add(a);
		}
	}
	
	@Override
	public void sub(INDArray a) {
		if ((a instanceof AScalar)||(a.dimensionality()==0)) {
			sub(a.get());
		} else {
			super.sub(a);
		}
	}
	
	public void sub(AScalar s) {
		set(get()-s.get());
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
		return a.scaleCopy(get());
	}
	
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
		return a.scaleCopy(get());
	}
	
	@Override 
	public double get(int... indexes) {
		assert(indexes.length==0);
		return get();
	}
	
	@Override 
	public void set(int[] indexes, double value) {
		if (indexes.length==0) {
			set(value);
		} else {
			throw new VectorzException(""+indexes.length+"D set not supported on AScalar");
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
	public void copyTo(double[] arr) {
		arr[0]=get();
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
		} else {
			int n=targetShape[tdims-1];
			if (n==0) return Vector0.INSTANCE;
			AVector v=RepeatedElementVector.create(n,get());
			return v.broadcast(targetShape);
		}
	}
	
	@Override
	public INDArray broadcastLike(INDArray v) {
		int dims=v.dimensionality();
		if (dims==0) return this;
		int lastShape=v.getShape(dims-1);
		AVector rv=Vectorz.createRepeatedElement(lastShape,get());
		return rv.broadcastLike(v);
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
	public boolean epsilonEquals(INDArray a) {
		return epsilonEquals(a,Vectorz.TEST_EPSILON);
	}
	
	@Override
	public boolean epsilonEquals(INDArray a, double epsilon) {
		if (a.dimensionality()!=0) {
			return false;
		} else {
			double d=get()-a.get();
			return (Math.abs(d)<=epsilon);
		}
	}
	
	@Override
	public boolean equals(INDArray o) {
		return (o.dimensionality()==0)&&(o.get()==get());
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return data[offset]==get();
	}
	
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
	public AScalar sparseClone() {
		return Scalar.create(get());
	}

	@Override
	public AScalar immutable() {
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
