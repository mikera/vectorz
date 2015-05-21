package mikera.arrayz.impl;

import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Base class for NDArray implementations.
 * 
 * @author Mike
 *
 */
public abstract class BaseNDArray extends BaseShapedArray implements IStridedArray { 
	private static final long serialVersionUID = -4221161437647016169L;

	protected final int dimensions;
	protected int offset; // not final, in case we want to do "sliding window" trick :-)
	protected final double[] data;
	protected final int[] stride;
	
	protected BaseNDArray(double[] data, int dimensions, int offset, int[] shape, int[] stride) {
		super(shape);
		this.data=data;
		this.offset=offset;
		this.stride=stride;
		this.dimensions=dimensions;
	}
	
	@Override
	public int dimensionality() {
		return dimensions;
	}
	
	@Override
	public final int getStride(int dim) {
		return stride[dim];
	}
	
	@Override
	public final int getShape(int dim) {
		return shape[dim];
	}

	@Override
	public long[] getLongShape() {
		long[] sh=new long[dimensions];
		IntArrays.copyIntsToLongs(shape,sh);
		return sh;
	}
	
	public int getIndex(int... indexes) {
		int ix = offset;
		for (int i = 0; i < dimensions; i++) {
			ix += indexes[i] * getStride(i);
		}
		return ix;
	}

	@Override
	public double get(int... indexes) {
		int ix=offset;
		for (int i=0; i<dimensions; i++) {
			ix+=indexes[i]*getStride(i);
		}
		return data[ix];
	}
	
	@Override
	public double get() {
		if (dimensions==0) {
			return data[offset];
		} else {
			throw new UnsupportedOperationException(ErrorMessages.invalidIndex(this));
		}
	}

	@Override
	public double get(int x) {
		if (dimensions==1) {
			return data[offset+x*getStride(0)];
		} else {
			throw new UnsupportedOperationException(ErrorMessages.invalidIndex(this,x));
		}
	}

	@Override
	public double get(int x, int y) {
		if (dimensions==2) {
			return data[offset+x*getStride(0)+y*getStride(1)];
		} else {
			throw new UnsupportedOperationException(ErrorMessages.invalidIndex(this,x,y));
		}
	}
	
	@Override
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
	public long elementCount() {
		return IntArrays.arrayProduct(shape);
	}
	
	@Override
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int[] getStrides() {
		return stride;
	}
	@Override
	protected final void checkDimension(int dimension) {
		if ((dimension < 0) || (dimension >= dimensions))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this,dimension));
	}
}
