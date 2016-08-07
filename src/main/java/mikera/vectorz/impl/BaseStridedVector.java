package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;

/**
 * Base class for strided vectors backed with a double array and fixed offset / stride
 * 
 * Supports both mutable and immutable subclasses
 * @author Mike
 *
 */
public abstract class BaseStridedVector extends AStridedVector {
	private static final long serialVersionUID = 7038506080494281379L;

	protected BaseStridedVector(int length, double[] data, int offset, int stride) {
		super(length, data);
		this.stride=stride;
		this.offset=offset;
		if ((offset<0)) throw new IndexOutOfBoundsException();
		if (length>0) {
			// check if last element is in the array
			int lastOffset=(offset+(length-1)*stride);
			if ((lastOffset>=data.length)||(lastOffset<0)) throw new IndexOutOfBoundsException("StridedVector ends outside array");
		}
	}

	protected final int stride;
	protected final int offset;

	@Override
	public final int getArrayOffset() {
		return offset;
	}

	@Override
	public final int getStride() {
		return stride;
	}

	@Override
	protected final int index(int i) {
		return offset+i*stride;
	}

	@Override
	public final double get(int i) {
		checkIndex(i);
		return data[offset+i*stride];
	}
	
	@Override
	public final double unsafeGet(int i) {
		return data[offset+i*stride];
	}
	
	@Override
	public double dotProduct(double[] ds, int off) {
		return DoubleArrays.dotProduct(ds, off, data, offset, stride, length);
	}
	
	@Override
	public double dotProduct(double[] ds, int doffset, int dstride) {
		return DoubleArrays.dotProduct(data, offset, stride, ds, doffset, dstride, length);
	}
		
	@Override
	public double dotProduct(AVector v) {
		checkLength(v.length());
		return v.dotProduct(getArray(), getArrayOffset(), getStride());
	}
}
