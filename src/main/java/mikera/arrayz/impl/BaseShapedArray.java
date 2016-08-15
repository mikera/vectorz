package mikera.arrayz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.Op2;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Abstract base class for N-dimensional arrays that use a fixed int[] shape vector.
 * 
 * Any dimensionality (including 0) is supported.
 * 
 * @author Mike
 */
public abstract class BaseShapedArray extends AbstractArray<INDArray> {
	private static final long serialVersionUID = -1486048632091493890L;

	protected final int[] shape;
	
	public BaseShapedArray(int [] shape) {
		this.shape=shape;
	}

	@Override
	public int dimensionality() {
		return shape.length;
	}

	@Override
	public int[] getShape() {
		return shape;
	}
	
	@Override
	public int[] getShapeClone() {
		return shape.clone();
	}

	@Override
	public int sliceCount() {
		return shape[0];
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		if (shape.length==0) return op.apply(init, get());
		double result=init;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result=slice(i).reduce(op,result);
		}
		return result;
	}
	
	@Override
	public double reduce(Op2 op) {
		if (shape.length==0) return get();
		return super.reduce(op);
	}
	
	@Override
	public boolean isZero() {
		if (dimensionality()==0) return (get()==0.0);
		return super.isZero();
	}
	
	@Override
	public int getShape(int dim) {
		return shape[dim];
	}	

	@Override
	public long elementCount() {
		return IntArrays.arrayProduct(shape);
	}
	
	@Override
	public double getElement(long i) { 
		if (dimensionality()==0) {
			if (i==0) return get();
			throw new IndexOutOfBoundsException(ErrorMessages.invalidElementIndex(this, i));
		}
		long sliceElementCount=slice(0).elementCount();
		if (sliceElementCount==0) throw new IndexOutOfBoundsException(ErrorMessages.invalidElementIndex(this, i));
		int slice=(int)(i/sliceElementCount);
		return slice(slice).getElement(i-slice*sliceElementCount);
	}
}
