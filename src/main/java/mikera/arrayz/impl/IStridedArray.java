package mikera.arrayz.impl;

import mikera.arrayz.INDArray;

public interface IStridedArray extends INDArray {
	
	public double[] getArray();
	
	public int getArrayOffset();
	
	public int[] getShape();
	
	public int[] getStrides();
}
