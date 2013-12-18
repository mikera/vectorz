package mikera.arrayz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Base class for NDArray implementations.
 * 
 * @author Mike
 *
 */
public abstract class BaseNDArray extends AbstractArray<INDArray> { 
	protected final int dimensions;
	protected final int[] shape;
	protected int offset; // not final, in case we want to do "sliding window" trick :-)
	protected final double[] data;
	protected final int[] stride;
	
	protected BaseNDArray(double[] data, int dimensions, int offset, int[] shape, int[] stride) {
		this.data=data;
		this.offset=offset;
		this.shape=shape;
		this.stride=stride;
		this.dimensions=dimensions;
	}
	
	@Override
	public int dimensionality() {
		return dimensions;
	}

	@Override
	public int[] getShape() {
		return shape;
	}
	
	@Override
	public int[] getShapeClone() {
		return shape.clone();
	}
	
	public int getStride(int dim) {
		return stride[dim];
	}
	
	@Override
	public int getShape(int dim) {
		return shape[dim];
	}

	@Override
	public long[] getLongShape() {
		long[] sh=new long[dimensions];
		IntArrays.copyIntsToLongs(shape,sh);
		return sh;
	}

	@Override
	public double get(int... indexes) {
		int ix=offset;
		for (int i=0; i<dimensions; i++) {
			ix+=indexes[i]*getStride(i);
		}
		return data[ix];
	}
	
	public boolean isPackedArray() {
		if (offset!=0) return false;
		
		int st=1;
		for (int i=dimensions-1; i>=0; i--) {
			if (getStride(i)!=st) return false;
			int d=shape[i];
			st*=d;
		}
			
		return (st==data.length);
	}
	
	@Override
	public int sliceCount() {
		if (dimensions==0) {
			throw new IllegalArgumentException(ErrorMessages.noSlices(this));
		} else {
			return getShape(0);
		}
	}

	@Override
	public long elementCount() {
		return IntArrays.arrayProduct(shape);
	}

}
