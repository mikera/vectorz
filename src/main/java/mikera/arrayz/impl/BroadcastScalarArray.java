package mikera.arrayz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

/**
 * Specialized array class representing the broadcasting of a scalar value to fill an entire array.
 * 
 * @author Mike
 *
 */
public class BroadcastScalarArray extends BaseShapedArray {
	private static final long serialVersionUID = 4529531791977491726L;

	private final double value;
	private final int dims;
	
	// this implements caching of major slices to improve performance and avoid allocations
	private final INDArray majorSlice;
	
	private BroadcastScalarArray(double d, int[] targetShape) {
		super(targetShape);
		dims=targetShape.length;
		value=d;
		majorSlice=(dims>0)?createMajorSlice():null;
	}

	public static INDArray create(double value, int[] targetShape) {
		int tdims=targetShape.length;
		if (tdims==0) {
			return Scalar.create(value);
		} else if (tdims==1) {
			return Vectorz.createRepeatedElement(targetShape[0],value);
		} else if (tdims==2) {
			return Vectorz.createRepeatedElement(targetShape[1],value).broadcast(targetShape);
		} else if (value==0) {
			return ZeroArray.create(targetShape);
		} else {
			return new BroadcastScalarArray(value,targetShape);
		}
	}

	@Override
	public double get(int... indexes) {
		return value;
	}

	@Override
	public void set(int[] indexes, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public INDArray slice(int majorSlice) {
		if ((majorSlice>=0)&&(majorSlice<shape[0])) return this.majorSlice;
		throw new UnsupportedOperationException(ErrorMessages.invalidSlice(this,majorSlice));
	}
	
	private INDArray createMajorSlice() {
		if (dims>0) {
			return create(value,IntArrays.removeIndex(shape, 0));
		}
		throw new UnsupportedOperationException("Can't slice a scalar array");
	}

	@Override
	public INDArray slice(int dimension, int index) {
		if ((dimension>=0)&&(dimension<dims)) {
			return create(value,IntArrays.removeIndex(shape, dimension));
		}
		throw new UnsupportedOperationException(ErrorMessages.invalidSlice(this, dimension,index));
	}

	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}

	@Override
	public double get() {
		if (dims==0) return value;
		throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, new int[]{}));
	}

	@Override
	public double get(int x) {
		if ((dims==0)&&(x>=0)&&(x<shape[0])) return value;
		throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, new int[]{x}));
	}

	@Override
	public double get(int x, int y) {
		if ((dims==0)&&(x>=0)&&(y>=0)&&(x<shape[0])&&(y<shape[1])) return value;
		throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, new int[]{x,y}));
	}
	
	@Override
	public BroadcastScalarArray exactClone() {
		return new BroadcastScalarArray(value,shape);
	}
}
