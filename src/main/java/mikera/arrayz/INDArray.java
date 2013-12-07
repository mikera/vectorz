package mikera.arrayz;

import java.nio.DoubleBuffer;
import java.util.Iterator;
import java.util.List;

import mikera.vectorz.AVector;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;

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
	
	/**
	 * Sets this array to the element values contained in another array
	 * @param a
	 */
	public void set(INDArray a);
	
	/**
	 * Sets this array to the element values contained in the given object.
	 * Attempts to interpret the object as an array
	 * @param a
	 */
	public void set(Object o);
	
	/**
	 * Adds a double value to all elements in this array
	 */
	public void add(double a);
	
	/**
	 * Subtracts a double value from all elements in this array
	 */
	public void sub(double a);
	
	/**
	 * Adds all the elements of another array to this array, in an elementwise order.
	 * @param a
	 */
	public void add(INDArray a);
	
	/**
	 * Subtracts all the elements of another array from this array, in an elementwise order.
	 * @param a
	 */
	public void sub(INDArray a);
	
	/**
	 * Fills the array with asingle double value
	 * @param value
	 */
	public void fill(double value);
	
	/**
	 * Negates all elements in the array
	 */
	public void negate();
	
	/**
	 * Replaces all elements in the array with their reciprocal
	 */
	public void reciprocal();
	
	/**
	 * Clamps all the elments of this array within the specified [min,max] range
	 * @param min
	 * @param max
	 */
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
	 * Constructs a view of the array as a single vector in row-major order.
	 * @return
	 */
	public AVector asVector();
	
	/**
	 * Returns a list containing all elements of this array
	 * @return
	 */
	public List<Double> asElementList();
	
	/**
	 * Returns a new array by rearranging the elements of this array into the desired shape
	 * Truncates or zero-pads the elements as required to fill the new shape
	 * @param shape
	 * @return
	 */
	public INDArray reshape(int... shape);

	/**
	 * Returns a view of this array broadcasted up to a larger shape
	 * @param shape
	 * @return
	 */
	public INDArray broadcast(int... shape);
	
	/**
	 * Broadcasts to match the shape of the target
	 * @param target
	 * @return
	 */
	public INDArray broadcastLike(INDArray target);
	
	/**
	 * Creates a clone of the array, broadcasted if necessary to match the shape of the target
	 * @param target
	 * @return
	 */
	public INDArray broadcastCloneLike(INDArray target);

	/**
	 * Returns the specified major slice of this array as a view (slice along dimension 0)
	 * @param majorSlice
	 * @return
	 */
	public INDArray slice(int majorSlice);

	/**
	 * Returns a slice view of this array along the specified dimension
	 * @param majorSlice
	 * @return
	 */
	public INDArray slice(int dimension, int index);
	
	/**
	 * Returns the transpose of this array. A transpose of an array is equivalent to 
	 * reversing the order of dimensions
	 * @return
	 */
	public INDArray getTranspose();
	
	/**
	 * Returns a transposed view of the array. May throw UnsupportedOperationException 
	 * if the array does not support this capability
	 * @return
	 */
	public INDArray getTransposeView();
	
	/**
	 * Returns a transposed copy of the array. 
	 * @return
	 */
	public INDArray getTransposeCopy();

	/**
	 * returns the number of major slices in this array.
	 * @return
	 */
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
	 * Returns an iterator over all elements in this array.
	 * @return
	 */
	public Iterator<Double> elementIterator();
	
	/**
	 * Multiplies all elements by the equivalent elements in a second array
	 * @return
	 */
	public void multiply(INDArray a);
	
	/**
	 * Divides all elements by the equivalent elements in a second array
	 * @return
	 */
	public void divide(INDArray a);
	
	/**
	 * Divides all elements by a given factor
	 * @return
	 */
	public void divide(double factor);


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
	 * Returns true if the array is boolean (contains only 0.0 or 1.0 values)
	 */
	public boolean isBoolean();
	
	/**
	 * If this method returns true, the INDArray is guaranteed to be fully mutable 
	 * in all positions i.e. every position can store any valid double value
	 * @return
	 */
	public boolean isFullyMutable();
	
	/**
	 * Returns true if this array has some constraints on element values
	 */
	public boolean isElementConstrained();
	
	/**
	 * Returns true if this array is the same shape as another array
	 */
	public boolean isSameShape(INDArray a);
	
	/**
	 * Return true if this array is a view type
	 * @return
	 */
	public boolean isView();

	/**
	 * Returns true if the array is zero (all elements equal to zero)
	 * @return
	 */
	public boolean isZero();
	
	/**
	 * Returns a clone of the array, as a new array which will be fully mutable
	 * and may be of a different class to the original.
	 * 
	 * Clone should attempt to return the most efficient possible array type.
	 * @return
	 */
	public INDArray clone();

	/**
	 * Ensures the array is a fully mutable, efficient representation that is not 
	 * a view. Returns either the same array or a new clone.
	 */
	public INDArray ensureMutable();
	
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
	 * Raises all elements of the array to a specified power
	 * 
	 * @param exponent
	 */
	public void pow(double exponent);
	
	/**
	 * Squares all elements of the array
	 */
	public void square();
	
	/**
	 * Computes the square root of all elements in the array
	 */
	public void sqrt();
	
	/**
	 * Calculates the signum of all elements of the array
	 */
	public void signum();

	/**
	 * Returns a list of all major slices of this array.
	 * @return
	 */
	public List<?> getSlices();
	
	/**
	 * Returns a list of all slices of this array along a given dimension
	 * @return
	 */
	public List<?> getSlices(int dimension);
	
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
	 * Copies the elements of this INDArray to a new double array
	 * @param arr
	 */
	public double[] toDoubleArray();
	
	/**
	 * Returns the underlying double array representing the packed elements of this array
	 * Returns nil if there is no such underlying array
	 * @param arr
	 */
	public double[] asDoubleArray();

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
	public Vector toVector();
	
	/**
	 * Converts the array into a packed mutable Array instance
	 * @return
	 */
	public Array toArray();

	/**
	 * Tests is this array is approximately equal to another array.
	 * The arrays must have the same shape
	 * @param a
	 * @return
	 */
	public boolean epsilonEquals(INDArray a);
	
	/**
	 * Tests is this array is approximately equal to another array, up to a given tolerance (epsilon)
	 * The arrays must have the same shape
	 * @param a
	 * @return
	 */
	public boolean epsilonEquals(INDArray a, double epsilon);

	/**
	 * Replaces all elements of this array with their absolute values, according to Math.abs(double)
	 */
	public void abs();

	/**
	 * Computes the natural logarithm (in-place) for all array elements
	 */
	public void log();

	/**
	 * Computes the function e^x (in-place) for all array elements
	 */
	public void exp();


}
