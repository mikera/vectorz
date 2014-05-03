package mikera.arrayz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.util.IntArrays;

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
	public int getShape(int dim) {
		return shape[dim];
	}	

	@Override
	public long elementCount() {
		return IntArrays.arrayProduct(shape);
	}
}
