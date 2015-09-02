package mikera.arrayz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;

public class BroadcastScalarArray extends BaseShapedArray {
	private static final long serialVersionUID = 4529531791977491726L;

	private final double value;
	private final int dims;
	
	private BroadcastScalarArray(double d, int[] targetShape) {
		super(targetShape);
		dims=targetShape.length;
		value=d;
	}

	public static INDArray create(double d, int[] targetShape) {
		int tdims=targetShape.length;
		if (tdims==0) {
			return Scalar.create(d);
		} else if (tdims==1) {
			return Vectorz.createRepeatedElement(targetShape[0],d);
		} else if (tdims==2) {
			return Vectorz.createRepeatedElement(targetShape[1],d).broadcast(targetShape);
		} else {
			return new BroadcastScalarArray(d,targetShape);
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
		if (dims>0) {
			return create(value,IntArrays.removeIndex(shape, 0));
		}
		throw new UnsupportedOperationException(ErrorMessages.invalidSlice(this,majorSlice));
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
