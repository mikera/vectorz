package mikera.arrayz.impl;

import mikera.arrayz.INDArray;

/**
 * Interface for arrays that support strided access over underlying data
 * 
 * @author Mike
 *
 */
public interface IStridedArray extends INDArray {
	
	public double[] getArray();
	
	public int getArrayOffset();
	
	public int[] getShape();
	
	public int[] getStrides();
	
	public int getStride(int dimension);

	boolean isPackedArray();
}
