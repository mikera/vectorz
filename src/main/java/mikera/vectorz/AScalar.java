package mikera.vectorz;

import java.util.Iterator;
import java.util.List;

import mikera.arrayz.AbstractArray;
import mikera.arrayz.INDArray;
import mikera.randomz.Hash;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.WrappedScalarVector;
import mikera.vectorz.util.VectorzException;

/**
 * Class to represent a wrapped 0-d scalar value.
 * 
 * Can be a view into another vector/matrix/array
 * 
 * @author Mike
 */
public abstract class AScalar extends AbstractArray<Object> implements IScalar {
	
	private static final int[] SCALAR_SHAPE=new int[0];
	private static final long[] SCALAR_LONG_SHAPE=new long[0];

	public abstract double get();
	
	public void set(double value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setElements(double[] values, int offset, int length) {
		if (length!=1) {
			throw new IllegalArgumentException("length must be 1");
		}
		set(values[offset]);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		dest[offset]=get();
	}
	
	@Override
	public int dimensionality() {
		return 0;
	}
	
	@Override
	public INDArray slice(int position) {
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
	
	public void add(double d) {
		set(get()+d);
	}
	
	public void sub(double d) {
		set(get()-d);
	}
	
	public void add(AScalar s) {
		set(get()+s.get());
	}
	
	public void add(INDArray a) {
		if (a instanceof AScalar) {
			add(a.get());
		} else {
			super.add(a);
		}
	}
	
	public void sub(INDArray a) {
		if (a instanceof AScalar) {
			sub(a.get());
		} else {
			super.sub(a);
		}
	}
	
	public void sub(AScalar s) {
		set(get()-s.get());
	}
	
	public INDArray innerProduct(INDArray a) {
		a=a.clone();
		a.scale(get());
		return a;
	}
	
	public INDArray outerProduct(INDArray a) {
		a=a.clone();
		a.scale(get());
		return a;
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
	public long elementCount() {
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
	public void applyOp(IOp op) {
		set(op.apply(get()));
	}
	
	@Override
	public void applyOp(Op op) {
		set(op.apply(get()));
	}
	
	@Override
	public AScalar clone() {
		return (AScalar) super.clone();
	}
	
	@Override 
	public void multiply(double factor) {
		set(factor*get());
	}
	
	@Override
	public double elementSum() {
		return get();
	}
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims==0) {
			return this;
		} else {
			int n=targetShape[tdims-1];
			AVector v=new RepeatedElementVector(n,get());
			return v.broadcast(targetShape);
		}
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
	
	public boolean equals(INDArray o) {
		return (o.dimensionality()==0)&&(o.get(SCALAR_SHAPE)==get());
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
	public abstract AScalar exactClone();
	
	@Override
	public void validate() {
		get();
		super.validate();
	}
}
