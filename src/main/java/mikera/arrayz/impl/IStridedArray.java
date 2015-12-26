package mikera.arrayz.impl;

import mikera.arrayz.INDArray;

/**
 * Interface for arrays that support strided access over underlying data
 * 
 * @author Mike
 *
 */
public interface IStridedArray extends INDArray {
	/**
	 * Gets the underlying data array for this strided array
	 * @return
	 */
	public double[] getArray();
	
	/**
	 * Gets the data array offset for the first element of this strided array
	 * @return
	 */
	public int getArrayOffset();
	
	/**
	 * Gets the strides for each dimension of this strided array. 
	 * 
	 * The returned array should not be modified
	 * @return
	 */
	public int[] getStrides();
	
	/**
	 * Gets the stide for a specific dimension in this strided array.
	 * @param dimension
	 * @return
	 */
	public int getStride(int dimension);

	/**
	 * Checks if this array is fully packed in row major order.
	 * @return
	 */
	boolean isPackedArray();
}
