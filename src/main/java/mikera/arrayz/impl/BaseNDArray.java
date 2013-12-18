package mikera.arrayz.impl;

import mikera.arrayz.INDArray;

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
	
}
