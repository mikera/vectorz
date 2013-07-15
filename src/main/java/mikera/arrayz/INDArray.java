package mikera.arrayz;

import java.nio.DoubleBuffer;
import java.util.List;

import mikera.vectorz.AVector;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;

/**
 * Interface for general multi-dimensional arrays of doubles
 * @author Mike
 */
public interface INDArray extends Cloneable {
	
	/**
	 * Returns the dimensionality of the array (number of dimensions in the array shape)
	 * e.g. 0 for a scalar, 1 for a vector, 2 for a matrix etc.
	 * @return
	 */
	public int dimensionality();
	
	/**
	 * Returns the shape of the array as an array of ints.
	 * @return
	 */
	public int[] getShape();
	
	/**
	 * Returns the dimension size for a specific dimension in the array's shape
	 * @param dim
	 * @return
	 */
	public int getShape(int dim);
	
	/**
	 * Returns the shape of the array as an array of longs.
	 * @return
	 */
	public long[] getLongShape();
	
	/**
	 * Returns the double value of a scalar array
	 * @return
	 */
	public double get();
	
	/**
	 * Returns the double value at the specified position in a 1D vector
	 * @return
	 */
	public double get(int x);
	
	/**
	 * Returns the double value at the specified position in a 2D matrix
	 * @return
	 */
	public double get(int x, int y);
	
	/**
	 * Returns the double value at the specified index position in an array
	 * @return
	 */
	public double get(int... indexes);

	/**
	 * Sets all elements of an array to a specific double value
	 * @param value
	 */
	public void set(double value);
	
	public void set(int i, double value);
	public void set(int i, int j, double value);
	public void set(int[] indexes, double value);
	public void set(INDArray a);
	public void set(Object o);
	
	public void add(double a);
	public void sub(double a);
	public void add(INDArray a);
	public void sub(INDArray a);
	public void negate();
	
	public void clamp(double min, double max);

	/**
	 * Calculates the inner product of this array with another array.
	 * @param a
	 * @return
	 */
	public INDArray innerProduct(INDArray a);
	
	/**
	 * Calculates the outer product of this array with another array.
	 * @param a
	 * @return
	 */
	public INDArray outerProduct(INDArray a);
	
	/**
	 * Creates a view of the array as a single vector in row-major order.
	 * @return
	 */
	public AVector asVector();
	
	public INDArray reshape(int... dimensions);

	public INDArray broadcast(int... dimensions);

	public INDArray slice(int majorSlice);

	public INDArray slice(int dimension, int index);
	
	public INDArray getTranspose();
	
	/**
	 * Returns a transposed view of the array. May throw UnsupportedOperationException 
	 * if the array does not support this capability
	 * @return
	 */
	public INDArray getTransposeView();

	
	public int sliceCount();
	
	/**
	 * Returns the total number of elements in this array. Equivalent to the product all dimension sizes.
	 * @return
	 */
	public long elementCount();
	
	/**
	 * Returns the total sum of elements in this array.
	 * @return
	 */
	public double elementSum();

	/**
	 * Returns the total sum of elements in this array.
	 * @return
	 */
	public double elementSquaredSum();
	
	/**
	 * Multiplies all elements by the equivalent elements in a second array
	 * @return
	 */
	public void multiply(INDArray a);
	
	/**
	 * Returns the number of non-zero elements in the array.
	 * @return
	 */
	public long nonZeroCount();
	
	/**
	 * Returns true if the INDArray is mutable (at least partially)
	 * @return
	 */
	public boolean isMutable();
	
	/**
	 * Returns true if the INDArray is fully mutable in all positions
	 * i.e. every position can store any valid double value
	 * @return
	 */
	public boolean isFullyMutable();
	
	/**
	 * Returns true if the IND has additional constraints on element values
	 */
	public boolean isElementConstrained();
	
	/**
	 * Return true if this is a view
	 * @return
	 */
	public boolean isView();

	/**
	 * Returns a clone of the array, as a new array which will be fully mutable
	 * and may be of a different class to the original.
	 * @return
	 */
	public INDArray clone();

	/**
	 * Applies a unary operator to all elements of the array (in-place)
	 * @param op
	 */
	void applyOp(Op op);

	/**
	 * Applies a unary operator to all elements of the array (in-place)
	 * @param op
	 */
	void applyOp(IOp op);
	
	/**
	 * Returns true if the two arrays are exactly equal in value and shape
	 * @param a
	 * @return
	 */
	public boolean equals(INDArray a);

	/**
	 * Returns an exact deep clone of an array (i.e. of the same class as the original).
	 * @return
	 */
	public INDArray exactClone();
	
	/**
	 * Sets all elements in an array using the given double values
	 */
	public void setElements(double[] values);
	
	/**
	 * Sets all elements in an array using the given double values
	 */
	public void setElements(double[] values, int offset, int length);

	/**
	 * Gets all elements of the array, copying them into a double array
	 * @param d
	 */
	public void getElements(double[] dest, int offset);
	
	/**
	 * Scales all elements of the array by a given double value
	 * @param d
	 */
	public void scale(double d);
	
	/**
	 * Scales all elements of the array by a given double value and adds a constant vale
	 * @param d
	 */
	public void scaleAdd(double factor, double constant);
	
	/**
	 * Multiplies all elements of the array by a given double value
	 * @param d
	 */
	public void multiply(double d);

	/**
	 * Returns a list of all major slices of this array.
	 * @return
	 */
	public List<?> getSlices();
	
	/**
	 * Validates the internal data structure of the INDArray. Throws an exception on failure.
	 * 
	 * Failure indicates a serious bug and/or data corruption.
	 */
	public void validate();

	/**
	 * Copies all the elements of this INDArray to the specified double array
	 * @param arr
	 */
	public void copyTo(double[] arr);
	
	/**
	 * Copies the elements of this INDArray to the specified double buffer
	 * @param arr
	 */
	public void toDoubleBuffer(DoubleBuffer dest);

	/**
	 * Returns a list of slices as mutable INDArray views.
	 * 
	 * Note: will return a list of AScalar values when this array is a 1D vector
	 * 
	 * @return
	 */
	public List<INDArray> getSliceViews();

	/**
	 * Converts the array into a flattened vector
	 * @return
	 */
	public AVector toVector();
}
