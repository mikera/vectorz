package mikera.arrayz;

import java.io.Serializable;
import java.nio.DoubleBuffer;
import java.util.Iterator;
import java.util.List;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;

/**
 * Interface for general multi-dimensional arrays of doubles
 * 
 * All Vectorz array types (including all vectors and matrices) must implement this interface.
 * 
 * Arrays have the following properties:
 * - They behave as an indexed, multi-dimensional array of doubles
 * - The may sometimes have size 0 dimensions, but not all array types support this.
 * 
 * @author Mike
 */
public interface INDArray extends Cloneable, Serializable {
	
	/**
	 * Returns the dimensionality of the array (number of dimensions in the array shape)
	 * e.g. 0 for a scalar, 1 for a vector, 2 for a matrix etc.
	 */
	public int dimensionality();
	
	/**
	 * Returns the shape of the array as an array of ints.
	 */
	public int[] getShape();
	
	/**
	 * Returns the shape of the array as an array of ints, guaranteed to be a new array
	 * i.e. will perform a defensive copy of the shape array if required.
	 */
	public int[] getShapeClone();
	
	/**
	 * Returns the dimension size for a specific dimension in the array's shape
	 * Throws an error if the dimension does not exist.
	 */
	public int getShape(int dim);
	
	/**
	 * Returns the shape of the array as an array of longs.
	 */
	public long[] getLongShape();
	
	/**
	 * Returns the double value of a scalar array
	 */
	public double get();
	
	/**
	 * Returns the double value at the specified position in a 1D vector
	 */
	public double get(int x);
	
	/**
	 * Returns the double value at the specified position in a 2D matrix
	 */
	public double get(int x, int y);
	
	/**
	 * Returns the double value at the specified index position in an array
	 */
	public double get(int... indexes);

	/**
	 * Sets all elements of an array to a specific double value
	 */
	public void set(double value);
	
	public void set(int i, double value);
	public void set(int i, int j, double value);
	public void set(int[] indexes, double value);
	
	/**
	 * Sets this array to the element values contained in another array
	 */
	public void set(INDArray a);
	
	/**
	 * Sets this array to the element values contained in the given object.
	 * Attempts to interpret the object as an array
	 */
	public void set(Object o);
	
	/**
	 * Adds a double value to all elements in this array
	 */
	public void add(double a);
	
	/**
	 * Adds all the elements of this array to a double array, in row-major order
	 */
	public void addToArray(double[] data, int offset);
	
	/**
	 * Subtracts a double value from all elements in this array
	 */
	public void sub(double a);
	
	/**
	 * Adds all the elements of another array to this array, in an elementwise order.
	 */
	public void add(INDArray a);
	
	/**
	 * Subtracts all the elements of another array from this array, in an elementwise order.
	 */
	public void sub(INDArray a);
	
	/**
	 * Fills the array with a single double value
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
	 */
	public void clamp(double min, double max);

	/**
	 * Calculates the inner product of this array with another array.
	 */
	public INDArray innerProduct(INDArray a);
	
	/**
	 * Calculates the inner product of this array with a double value
	 */
	public INDArray innerProduct(double a);
	
	/**
	 * Calculates the inner product of this array with a scalar
	 */
	public INDArray innerProduct(AScalar a);
	
	/**
	 * Calculates the outer product of this array with another array.
	 */
	public INDArray outerProduct(INDArray a);
	
	/**
	 * Constructs a view of the array as a single vector in row-major order.
	 */
	public AVector asVector();
	
	/**
	 * Returns a list containing all elements of this array
	 */
	public List<Double> asElementList();
	
	/**
	 * Returns a new array by rearranging the elements of this array into the desired shape
	 * Truncates or zero-pads the elements as required to fill the new shape
	 */
	public INDArray reshape(int... shape);

	/**
	 * Returns rotated view of this array
	 */
	public INDArray rotateView(int dimension, int shift);
	
	/**
	 * Returns a view of this array broadcasted up to a larger shape
	 */
	public INDArray broadcast(int... shape);
	
	/**
	 * Broadcasts to match the shape of the target
	 */
	public INDArray broadcastLike(INDArray target);
	
	/**
	 * Creates a clone of the array, broadcasted if necessary to match the shape of the target
	 */
	public INDArray broadcastCloneLike(INDArray target);

	/**
	 * Returns the specified major slice of this array as a view (slice along dimension 0)
	 * 
	 * 0 dimensional slice results are permitted, but attempting to slice a 0 dimensional array
	 * will result in an error.
	 */
	public INDArray slice(int majorSlice);
	
	/**
	 * Joins an array with another array along a specified dimension
	 */
	public INDArray join(INDArray a, int dimension);

	/**
	 * Returns a slice view of this array along the specified dimension
	 */
	public INDArray slice(int dimension, int index);
	
	/**
	 * Returns a subarray view of a larger array
	 */
	public INDArray subArray(int[] offsets, int[] shape);
	
	/**
	 * Returns the transpose of this array. A transpose of an array is equivalent to 
	 * reversing the order of dimensions. 
	 * 
	 * May or may not return a view depending on the array type.
	 */
	public INDArray getTranspose();
	
	/**
	 * Returns a transposed view of the array. May throw UnsupportedOperationException 
	 * if the array type does not support this capability
	 */
	public INDArray getTransposeView();
	
	/**
	 * Returns a transposed copy of the array. Guarantees a new mutable defensive copy.
	 */
	public INDArray getTransposeCopy();

	/**
	 * Returns the number of major slices in this array (i.e. the size of dimension 0)
	 */
	public int sliceCount();
	
	/**
	 * Returns the total number of elements in this array. Equivalent to the product all dimension sizes.
	 */
	public long elementCount();
	
	/**
	 * Returns the sum of all elements in this array.
	 */
	public double elementSum();
	
	/**
	 * Returns the maximum element value in this array. Throws an error if there are no elements.
	 */
	public double elementMax();
	
	/**
	 * Returns the maximum element value in this array. Throws an error if there are no elements.
	 */
	public double elementMin();

	/**
	 * Returns the sum of squared elements in this array.
	 */
	public double elementSquaredSum();
	
	/**
	 * Returns an iterator over all elements in this array, in row-major order
	 */
	public Iterator<Double> elementIterator();
	
	/**
	 * Multiplies all elements by the equivalent elements in a second array, i.e. performs elementwise multiplication.
	 * 
	 * If matrix-style multiplication is required, use innerProduct instead.
	 */
	public void multiply(INDArray a);
	
	/**
	 * Divides all elements by the equivalent elements in a second array
	 */
	public void divide(INDArray a);
	
	/**
	 * Divides all elements by a given factor
	 */
	public void divide(double factor);


	/**
	 * Returns the number of non-zero elements in the array.
	 */
	public long nonZeroCount();
	
	/**
	 * Returns true if the INDArray is mutable (at least partially)
	 */
	public boolean isMutable();
	
	/**
	 * Returns true if the array is boolean (contains only 0.0 or 1.0 values)
	 */
	public boolean isBoolean();
	
	/**
	 * Returns true if the array is in a sparse format
	 */
	public boolean isSparse();
	
	/**
	 * Returns true if the array is in a dense format. 
	 * 
	 * A dense format uses storage proportional to the number of elements
	 */
	public boolean isDense();
	
	/**
	 * If this method returns true, the INDArray is guaranteed to be fully mutable 
	 * in all positions i.e. every position can store any valid double value
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
	 */
	public boolean isView();

	/**
	 * Returns true if the array is zero (all elements equal to zero)
	 */
	public boolean isZero();
	
	/**
	 * Returns a clone of the array, as a new array which will be fully mutable
	 * and may be of a different class to the original.
	 * 
	 * Clone should attempt to return the most efficient possible array type.
	 */
	public INDArray clone();
	
	/**
	 * Returns a defensive copy of the array data. 
	 * 
	 * May return the same array if the original was immutable, otherwise will return a defensive copy.
	 */
	public INDArray copy();


	/**
	 * Ensures the array is a fully mutable, efficient representation that is not 
	 * a view. Returns either the same array or a new clone.
	 */
	public INDArray ensureMutable();
	
	/**
	 * Applies a unary operator to all elements of the array (in-place)
	 */
	void applyOp(Op op);

	/**
	 * Applies a unary operator to all elements of the array (in-place)
	 */
	void applyOp(IOperator op);
	
	/**
	 * Returns true if the two arrays are exactly equal in value and shape
	 */
	public boolean equals(INDArray a);
	
	/**
	 * Returns true if all elements are equal to a specific value
	 */
	public boolean elementsEqual(double value);

	/**
	 * Returns an exact deep clone of an array (i.e. of the same class as the original).
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
	 */
	public void getElements(double[] dest, int offset);
	
	/**
	 * Scales all elements of the array by a given double value
	 */
	public void scale(double d);
	
	/**
	 * Scales all elements of the array by a given double value and adds a constant vale
	 */
	public void scaleAdd(double factor, double constant);
	
	/**
	 * Multiplies all elements of the array by a given double value
	 */
	public void multiply(double d);
	
	/**
	 * Raises all elements of the array to a specified power
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
	 */
	public List<?> getSlices();
	
	/**
	 * Returns a list of all slices of this array along a given dimension.
	 * 
	 * Returns a list of Double values if a 1-dimensional array is sliced.
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
	 */
	public void copyTo(double[] arr);
	
	/**
	 * Copies the elements of this INDArray to the specified double buffer
	 */
	public void toDoubleBuffer(DoubleBuffer dest);
	
	/**
	 * Copies the elements of this INDArray to a new double array
	 */
	public double[] toDoubleArray();
	
	/**
	 * Returns the underlying double array representing the packed elements of this array
	 * Returns nil if there is no such underlying array
	 */
	public double[] asDoubleArray();

	/**
	 * Returns a list of slices as mutable INDArray views.
	 * 
	 * Note: will return a list of AScalar values when this array is a 1D vector
	 * 
	 */
	public List<INDArray> getSliceViews();

	/**
	 * Converts the array into a flattened dense Vector
	 */
	public Vector toVector();
	
	/**
	 * Converts the array into a packed mutable Array instance
	 */
	public Array toArray();

	/**
	 * Tests is this array is approximately equal to another array.
	 * The arrays must have the same shape
	 */
	public boolean epsilonEquals(INDArray a);
	
	/**
	 * Tests is this array is approximately equal to another array, up to a given tolerance (epsilon)
	 * The arrays must have the same shape
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
	
	/**
	 * Returns an immutable version of this INDArray's data. May return the same array if already immutable.
	 */
	public INDArray immutable();

	/**
	 * Coerces this INDArray to a fully mutable format. May return the same INDArray if already fully mutable
	 */
	public INDArray mutable();
	
	/**
	 * Creates a fully mutable clone of this array
	 */
	public INDArray mutableClone();
	
	/**
	 * Coerces this INDArray to a sparse format, without changing its element values.
	 * 
	 * May return the same INDArray if already sparse. May also mutate the internal structure of the original 
	 * NDArray, or create a view over parts of the original INDArray. You should take a defensive copy of the original
	 * NDArray if any of this concerns you.
	 * 
	 * The returned sparse array may not be fully mutable in all elements.
	 */
	public INDArray sparse();
	
	/**
	 * Coerces this INDArray to a dense format, without changing its element values.
	 * 
	 * May return the same INDArray if already dense. May also mutate the internal structure of the original 
	 * NDArray, or create a view over parts of the original INDArray. You should take a defensive copy of the original
	 * NDArray if any of this concerns you.
	 * 
	 * The returned dense array may not be fully mutable in all elements.
	 */
	public INDArray dense();
	
	/**
	 * Creates a fully mutable clone of this array. Will use a sparse format if possible.
	 */
	public INDArray sparseClone();

	/**
	 * Returns true if the elements in this array exactly match the given array
	 * 
	 * @param data
	 * @return
	 */
	public boolean equalsArray(double[] data);	
	
	/**
	 * Returns true if the elements in this array exactly match the given array
	 * 
	 * @param data
	 * @return
	 */
	public boolean equalsArray(double[] data, int offset);	
	
}
