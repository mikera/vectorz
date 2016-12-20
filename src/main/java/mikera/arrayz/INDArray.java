package mikera.arrayz;

import java.io.Serializable;
import java.nio.DoubleBuffer;
import java.util.Iterator;
import java.util.List;

import mikera.indexz.AIndex;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector;

/**
 * Interface for general multi-dimensional arrays of doubles
 * 
 * All Vectorz array types (including all vectors and matrices) must implement this interface.
 * 
 * Arrays have the following properties:
 * - They behave as an indexed, multi-dimensional array of doubles
 * - They have a n-dimensional shape vector (conceptually equivalent to a list of integers)
 * - The may sometimes have size 0 dimensions, but not all array types support this.
 * 
 * @author Mike
 */
public interface INDArray extends Cloneable, Serializable, Comparable<INDArray> {
	
	/**
	 * Returns the dimensionality of the array (i.e. the number of dimensions in the array shape)
	 * e.g. 0 for a scalar, 1 for a vector, 2 for a matrix etc.
	 */
	public int dimensionality();
	
	/**
	 * Returns the shape of the array as an array of ints.
	 * 
	 * WARNING: May return the internal int[] array describing the shape. Modifying this may cause undefined behaviour.
	 * If you want to guarantee a new int[] array that can be safely modified, use getShapeClone() instead
	 */
	public int[] getShape();
	
	/**
	 * Returns the shape of the array as an array of ints, guaranteed to be a new array
	 * i.e. will perform a defensive copy of any internal shape array if required.
	 */
	public int[] getShapeClone();
	
	/**
	 * Returns the dimension size for a specific dimension in the array's shape
	 * 
	 * @throws IndexOutOfBoundsException if the dimension does not exist.
	 */
	public int getShape(int dim);
	
	/**
	 * Returns the shape of the array as an array of longs.
	 */
	public long[] getLongShape();
	
	/**
	 * Returns the double value at the specified position in the array
	 */
	public double get(AIndex ix);
	
	/**
	 * Returns the double value at the specified position in the array
	 */
	public double get(Index ix);
	
	/**
	 * Returns the double value of a scalar array
	 */
	public double get();
	
	/**
	 * Returns the double value at the specified position in a 1D vector
	 */
	public double get(int x);
	
	/**
	 * Returns the double value at the specified position in a 1D vector
	 */
	public double get(long x);
	
	/**
	 * Returns the double value at the specified position in a 2D matrix
	 */
	public double get(int x, int y);
	
	/**
	 * Returns the double value at the specified position in a 2D matrix
	 */
	public double get(long x, long y);
	
	/**
	 * Returns the double value at the specified index position in an array
	 */
	public double get(int... indexes);
	
	/**
	 * Returns the double value at the specified index position in an array
	 */
	public double get(long[] indexes);

	/**
	 * Sets all elements of an array to a specific double value
	 */
	public void set(double value);
	
	/**
	 * Sets a value at a given position in a mutable 1D array
	 *
	 * @param i
	 * @param value
	 */
	public void set(int i, double value);
	
	/**
	 * Sets a value at a given position in a mutable 2D array
	 *
	 * @param i
	 * @param value
	 */
	public void set(int i, int j, double value);
	
	/**
	 * Sets the element at the given indexed position in a mutable array
	 *
	 * @param index
	 * @param value
	 */
	public void set(int[] index, double value);
	

	/**
	 * Sets the element at the given indexed position in a mutable array
	 * @param index
	 * @param value
	 */
	public void set(long[] index, double value);

	/**
	 * Sets this array to the element values contained in another array. 
	 * 
	 * Broadcasts the source array if necessary to the shape of this array. Throws
	 * an exception if the shapes are incompatible.
	 */
	public void set(INDArray a);
	
	/**
	 * Sets this array to the element values contained in the given object.
	 * Attempts to interpret the object as an array, and broadcasts the object to the
	 * shape of this array if required.
	 */
	public void set(Object o);
	
	/**
	 * Adds a double value to all elements in this array.
	 * 
	 * Implementations may optimize the case of 0.0
	 */
	public void add(double a);
	
	/**
	 * Creates a new array equal to the sum of this array with another array.
	 * 
	 * @param a
	 * @return
	 */
	public INDArray addCopy(INDArray a);	
	
	/**
	 * Creates a new array equal to this array with a constant value added to every element
	 * 
	 * @param a
	 * @return
	 */
	public INDArray addCopy(double d);
	
	/**
	 * Adds all the elements of this array to a double array at the specified offset, in row-major order
	 */
	public void addToArray(double[] data, int offset);
	
	/**
	 * Subtracts a double value from all elements in this array
	 */
	public void sub(double a);
	
	/**
	 * Creates a new array equal to the subtraction of another array from this array
	 * 
	 * @param a
	 * @return
	 */
	public INDArray subCopy(INDArray a);
	
	/**
	 * Adds all the elements of another array to this array, in an elementwise order.
	 */
	public void add(INDArray a);
	
	/**
	 * Adds to a mutable array, indexed by element position in row major order.
	 *  
	 * This is an unsafe operation: bounds are not checked
	 */
	public void addAt(long i, double v);
	
	/**
	 * Subtracts all the elements of another array from this array, in an elementwise order.
	 */
	public void sub(INDArray a);
	
	/**
	 * Fills this array with a single double value. Requires the array to be mutable.
	 */
	public void fill(double value);
	
	/**
	 * Negates all elements in the array in place
	 */
	public void negate();
	
	/**
	 * Negates all elements in the array, returning a new array
	 */
	public INDArray negateCopy();
	
	/**
	 * Replaces all elements in the array with their reciprocal
	 */
	public void reciprocal();
	
	/**
	 * Returns a new array where every element is the reciprocal of the corresponding element in this array
	 */
	public INDArray reciprocalCopy();
	
	/**
	 * Clamps all the elements of this array within the specified [min,max] range
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
	 * Constructs a view of all elements in the array as a single vector in row-major order.
	 */
	public AVector asVector();
	
	/**
	 * Returns a List<Double> containing all elements of this array
	 */
	public List<Double> asElementList();
	
	/**
	 * Returns a new array by rearranging the elements of this array into the desired shape
	 * Truncates or zero-pads the elements as required to fill the new shape
	 */
	public INDArray reshape(int... shape);

	/**
	 * Returns a re-ordered array by taking the specified order of slices along a given dimension
	 * 
	 * May or may not use views of underlying slices (i.e. may be a reordered view)
	 */
	public INDArray reorder(int dimension, int[] order);
	
	/**
	 * Returns a re-ordered array by taking the specified order of slices along dimension 0
	 */
	public INDArray reorder(int[] order);

	
	/**
	 * Returns rotated view of this array
	 */
	public INDArray rotateView(int dimension, int shift);
	
	/**
	 * Returns a view of this array broadcasted up to a larger shape
	 */
	public INDArray broadcast(int... shape);
	
	/**
	 * Broadcasts an array to match the shape of the target
	 * @param target The target array
	 * @returns An array representing the broadcasted value of this array
	 * @throws IllegalArgumentException if this array cannot be broadcasted to match the target 
	 */
	public INDArray broadcastLike(INDArray target);
	
	/**
	 * Broadcasts an array to match the shape of the target matrix
	 * @param target The target matrix
	 * @returns A matrix representing the broadcasted value of this array
	 * @throws IllegalArgumentException if this array cannot be broadcasted to match the target 
	 */
	public AMatrix broadcastLike(AMatrix target);

	/**
	 * Broadcasts an array to match the shape of the target vector
	 * @param target The target vector
	 * @returns A vector representing the broadcasted value of this array
	 * @throws IllegalArgumentException if this array cannot be broadcasted to match the target 
	 */
	public AVector broadcastLike(AVector target);
	
	/**
	 * Creates a mutable clone of the array, broadcasted upwards if necessary to match the shape of the target.
	 * If no upwards broadcasting is required, returns a regular clone() of this array.
	 * 
	 * @param target The target array
	 * @throws IllegalArgumentException if the array cannot be broadcasted to match the target or vice versa 
	 */
	public INDArray broadcastCloneLike(INDArray target);
	
	/**
	 * Creates a copy of the array, broadcasted upwards if necessary to match the shape of the target
	 * Like broadCastCloneLike, but does not guarantee a fully mutable clone - hence may be faster
	 * when used with immutable or specialized arrays
	 * 
	 * @param target The target array
	 * @throws IllegalArgumentException if the array cannot be broadcasted to match the target or vice versa 
	 */
	public INDArray broadcastCopyLike(INDArray target);

	/**
	 * Returns the specified major slice of this array as a view (slice along dimension 0)
	 * 
	 * 0 dimensional slice results are permitted, but attempting to slice a 0 dimensional array
	 * will result in an error.
	 * 
	 * @throws RuntimeException if the slice does not exist
	 */
	public INDArray slice(int majorSlice);
	
	/**
	 * Returns the value of a major slice of this array (slice along dimension 0)
	 * 
	 * Like 'slice', except returns a Double value for slices of 1D vectors
	 * 
	 * @throws IndexOutOfBoundsException if the slice does not exist
	 */
	public Object sliceValue(int majorSlice);
	
	/**
	 * Joins an array with another array along a specified dimension.
	 * 
	 * Guarantees a view that combines the two joined arrays.
	 */
	public INDArray join(INDArray a, int dimension);

	/**
	 * Joins an array with another array along the major dimension
	 * 
	 * Guarantees a view that combines the two joined arrays.
	 */
	public INDArray join(INDArray a);
	
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
	 * Returns a transposed copy of the array. Guarantees a new defensive copy.
	 */
	public INDArray getTransposeCopy();

	/**
	 * Returns the number of major slices in this array (i.e. the size of dimension 0)
	 */
	public int sliceCount();
	
	/**
	 * Returns the total number of elements in this array. 
	 * Equivalent to the product of all dimension sizes.
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
	 * Multiplies all elements by the equivalent elements in a second array, i.e. performs elementwise multiplication.
	 * Returns a new array.
	 * 
	 * If matrix-style multiplication is required, use innerProduct instead.
	 */
	public INDArray multiplyCopy(INDArray a);
	
	/**
	 * Divides all elements in place by the equivalent elements in a second array
	 */
	public void divide(INDArray a);
	
	/**
	 * Divides all elements by the equivalent elements in a second array. Returns a new array.
	 */
	public INDArray divideCopy(INDArray a);

	
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
	 * A dense format uses efficient storage proportional to the number of elements
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
	 * Return true if this array is considered as a view type. This indicates (but does not guarantee):
	 * a) Data is *likely* to be shared with other arrays, so mutation of one may affect others
	 * b) It is *unlikely* to be in the most efficient general purpose format
	 * 
	 * isView is intended to be used as for heuristics. It should *not* be used where the outcome might
	 * affect correctness.
	 */
	public boolean isView();

	/**
	 * Returns true if the array is zero (all elements equal to zero)
	 */
	public boolean isZero();
	
	/**
	 * Returns a clone of the array data, as a new array which will be fully mutable
	 * and may be of a different class to the original.
	 * 
	 * Clone should attempt to return the most efficient possible array type.
	 * 
	 * Clone should preserve sparsity property where possible, but this is not guaranteed.
	 */
	public INDArray clone();
	
	/**
	 * Returns a defensive copy of the array data. Use this if the original array might change and you 
	 * need a defensive copy.
	 * 
	 * May return the same array if the original was immutable, otherwise will return a defensive copy.
	 */
	public INDArray copy();


	/**
	 * Ensures the array is a fully mutable representation.
	 * Returns either the same array or a new clone.
	 */
	public INDArray ensureMutable();
	
	/**
	 * Applies a unary operator to all elements of the array (in-place)
	 */
	void applyOp(Op op);
	
	/**
	 * Applies a unary operator to all elements of the array (creating a new array)
	 */
	INDArray applyOpCopy(Op op);

	/**
	 * Applies a unary operator to all elements of the array (in-place)
	 * @param operator The operator to apply to the array
	 */
	void applyOp(IOperator operator);
	
	/**
	 * Reduces over all elements of the array in row-major order. Applies the operator to 
	 * the initial/previous result at each step. Returns the final result.
	 */
	double reduce(Op2 op, double init);
	
	/**
	 * Reduces over all elements of the array in row-major order. Applies the operator to 
	 * the previous result at each step. Returns the final result.
	 */
	double reduce(Op2 op);
	
	/**
	 * Checks if two arrays are equal exactly in terms of both value and shape
	 * Element equality checks are consistent with compareTo
	 * 
	 * @returns true if the two arrays are exactly equal, false otherwise
	 */
	public boolean equals(INDArray a);

	/**
	 * Returns true if all elements are equal to a specific value
	 */
	public boolean elementsEqual(double value);

	/**
	 * Creates a deep clone of an array, using the same class implementation as the original array
	 * @return A clone of the original array
	 */
	public INDArray exactClone();
	
	/**
	 * Sets all elements in an array using the given double values
	 * 
	 * @param values Element values in a double[] array. There must be at least as many element values as elements in this array.
	 */
	public void setElements(double... values);
	
	/**
	 * Sets all elements in an array using the given double values

	 * @param values Element values in a double[] array. There must be at least as many element values as elements in this array.
	 * @param offset Position in the values array from which to start taking values.
	 */
	public void setElements(double[] values, int offset);
	
	/**
	 * Sets elements in an array using the given double values. The source array must contain at least the specified length in 
	 * terms of number of elements
	 * 
	 * @param pos Offset into this array, expressed in terms of number of elements in row major order
	 * @param values Element values in a double[] array. There must be at least as many element values as elements in this array.
	 * @param offset Position in the values array from which to start taking values.
	 * @param length Number of elements to set.
	 */
	public void setElements(int pos, double[] values, int offset, int length);

	/**
	 * Copies all elements of this array a double array at the specified offset
	 */
	public void getElements(double[] dest, int offset);
	
	/**
	 * Copies all elements of this array into an object array at the specified offset
	 */
	public void getElements(Object[] dest, int offset);
	
	/**
	 * Copies all elements of this INDArray to the specified double array
	 */
	public void getElements(double[] arr);
	
	/**
	 * Scales all elements of the array in place by a given double value
	 */
	public void scale(double factor);
	
	/**
	 * Scales all elements of the array by a given double value, returning a new array
	 */
	@Deprecated
	public INDArray scaleCopy(double factor);
	
	/**
	 * Scales all elements of the array by a given double value and adds a constant value
	 */
	public void scaleAdd(double factor, double constant);

	/**
	 * Scales all elements of the array by a given double value, adds a scaled second array and adds a constant value
	 */
	public void scaleAdd(double factor, INDArray b, double bfactor, double constant);
	
	/**
	 * Adds a scaled multiple of another array to this array
	 * @param src
	 * @param factor
	 */
	public void addMultiple(INDArray src, double factor);
	
	/**
	 * Adds an array with all elements of the source array raised to the specified power
	 * @param src
	 * @param factor
	 */
	public void addPower(INDArray src, double exponent);
	
	/**
	 * Adds an array with all elements raised to the specified power and scaled by the given factor
	 * @param src
	 * @param factor
	 */
	public void addPower(INDArray src, double exponent, double factor);
	
	/**
	 * Adds the inner product of two arrays to this array.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void addInnerProduct(INDArray a, INDArray b);

	/**
	 * Adds a scalar multiple of the inner product of two arrays to this array.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void addInnerProduct(INDArray a, INDArray b, double d);
	
	/**
	 * Adds the outer product of two arrays to this array.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void addOuterProduct(INDArray a, INDArray b);

	/**
	 * Adds the outer product of two arrays to this array.
	 * Only affects the mutable, non-sparse elements of this array.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void addOuterProductSparse(INDArray a, INDArray b);

	
	/**
	 * Sets this array to the inner product of two arrays. Must be the correct shape.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void setInnerProduct(INDArray a, INDArray b);
	
	/**
	 * Sets this array to the result of applyin an operation to a source array
	 */
	public void setApplyOp(Op op, INDArray a);
	
	/**
	 * Sets this array to the element-wise multiple of two arrays. Must have compatible shapes.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void setMultiple(INDArray a, INDArray b);

	/**
	 * Sets this array to be the multiple of another array. Must have compatible shapes.
	 * 
	 * @param a
	 * @param b
	 * @throws IllegalArgumentException if the inner product is a different shape to this array
	 */
	public void setMultiple(INDArray a, double b);
	
	/**
	 * Sets the non-sparse elements of this array to the corresponding elements of the source array
	 */
	public void setSparse(INDArray src);
	
	/**
	 * Sets the non-sparse elements of this array to the specified value
	 * 
	 * May throw an exception if non-sparse elements are not mutable
	 */
	public void setSparse(double value);
	
	/**
	 * Adds to the non-sparse elements of this array
	 */
	public void addSparse(double c);
	
	/**
	 * Adds the corresponding elements of the source array the non-sparse elements of this array
	 */
	public void addSparse(INDArray a);
	
	/**
	 * Adds a multiple of the source array to the non-sparse elements of this array 
	 */
	public void addMultipleSparse(INDArray src, double factor);
	
	/**
	 * Multiplies all elements of the array in place by a given double value
	 */
	public void multiply(double factor);
	
	/**
	 * Raises all elements of the array to a specified power in place
	 */
	public void pow(double exponent);
	
	/**
	 * Squares all elements of the array in place
	 */
	public void square();
	
	/**
	 * Squares all elements of the array, returning a new array
	 */
	public INDArray squareCopy();
	
	/**
	 * Computes the square root of all elements in the array
	 */
	public void sqrt();
	
	/**
	 * Calculates the signum of all elements of this mutable array
	 * 
	 * Sets each component of the array to its sign value (-1, 0 or 1)
	 */
	public void signum();

	/**
	 * Returns a list of all major slices of this array.
	 * 
	 * Returns a list of Double values if a 1-dimensional array is sliced, otherwise a list of INDArray instances
	 */
	public List<?> getSlices();
	
	/**
	 * Returns a list of all slices of this array along a given dimension.
	 * 
	 * Returns a list of Double values if a 1-dimensional array is sliced, otherwise a list of INDArray instances
	 */
	public List<?> getSlices(int dimension);
	
	/**
	 * Validates the internal data structure of the INDArray. Throws an exception on failure.
	 * 
	 * Failure indicates a serious bug and/or data corruption.
	 */
	public void validate();

	/**
	 * Copies the elements of this INDArray to the specified double buffer.
	 */
	public void toDoubleBuffer(DoubleBuffer dest);
	
	/**
	 * Copies the elements of this array to a new double[] array.
	 */
	public double[] toDoubleArray();
	
	/**
	 * Copies the slices of this array to a new INDArray[]
	 * 
	 * Throws an error if the array has no slices (i.e. is zero-dimensional)
	 */
	public INDArray[] toSliceArray();
	
	/**
	 * Returns the underlying double array representing the densely packed elements of this array.
	 * Modifications to this double array will change the original array.
	 * 
	 * Returns null if there is no such array.
	 */
	public double[] asDoubleArray();

	/**
	 * Returns a list of slices as mutable INDArray views.
	 * 
	 * Note: will return a list of AScalar values when this array is a 1D vector
	 */
	public List<INDArray> getSliceViews();

	/**
	 * Coerces the array into a flattened dense Vector, in row-major order.
	 * May return the same array if it is already a Vector instance.
	 */
	public Vector toVector();
	
	/**
	 * Tests if this array is approximately equal to another array.
	 * The arrays must have the same shape.
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
	 * Returns an immutable version of this array. May return the same array if already immutable.
	 * Guarantees a defensive copy if the array is mutable.
	 */
	public INDArray immutable();

	/**
	 * Coerces this INDArray to a fully mutable format. May return the same INDArray if already fully mutable
	 */
	public INDArray mutable();
	
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
	 * Creates a fully mutable clone of this array. 
	 * 
	 * Will use a sparse format if possible.
	 */
	public INDArray sparseClone();
	
	/**
	 * Creates a fully mutable dense clone of this array. 
	 * 
	 * Will always use a dense format, even if the array data is sparse.
	 */
	public INDArray denseClone();

	/**
	 * Returns true if the elements in this array exactly match the elements in the given array, in
	 * row-major order. Returns false otherwise.
	 * 
	 * @param data Array of double element values to check for equality
	 * @return
	 */
	public boolean equalsArray(double[] data);	
	
	/**
	 * Returns true if the elements in this array exactly match the elements in the given array, in
	 * row-major order
	 * 
	 * @param data Array of double element values to check for equality
	 * @param offset Offset into the data array
	 * @return
	 */
	public boolean equalsArray(double[] data, int offset);

	/**
	 * Computes the inner product of this array with a vector
	 * @param v A vector
	 * @return A new array representing the inner product
	 */
	public INDArray innerProduct(AVector v);

	/**
	 * Returns a copy of the array with the abs operator applied to each element
	 * @return A new array containing the absolute element values
	 */
	public INDArray absCopy();
	
	/**
	 * Sets this array to the absolute values of the difference between this array and another
	 */
	public void absDiff(INDArray a);
	
	
	/**
	 * Returns an array containing the absolute values of the difference between this array and another
	 * @return A new array containing the absolute element values
	 */
	public INDArray absDiffCopy(INDArray a);

	/**
	 * Returns a copy of the array with the signum operator applied to each element
	 * @return A new array containing the signums of element values
	 */
	public INDArray signumCopy();

	/**
	 * Gets all elements of the array as a Java double[] array
	 * @return A Java double[] array containing all elements in row-major order
	 */
	public double[] getElements();

	
	/**
	 * Gets a specific element of this array, indexed in row-major order
	 * @return The element value
	 */
	public double getElement(long i);

	/**
	 * Returns the product of all elements in the array.
	 * @return
	 */
	public double elementProduct();

	/**
	 * Returns a copy of the array with all elements multiplied by a single constant value
	 * @param factor A scalar factor
	 * @return A new array, with all element values scaled by the given factor
	 */
	public INDArray multiplyCopy(double factor);

	/**
	 * Returns a copy of the array with all elements divided by a single constant value
	 * @param factor A scalar factor
	 * @return A new array, with all element values scaled by the given factor
	 */
	public INDArray divideCopy(double d);
	
	/**
	 * Checks if the array has any uncountable element values (i.e. NaN or infinite)
	 * @return True if any element is this array is NaN or infinite, false otherwise
	 */
	public boolean hasUncountable();
	
	/**
	 * Returns the sum of all the elements raised to a specified power
	 * @return
	 */
	public double elementPowSum(double p);
	
	/**
	 * Returns the sum of the absolute values of all the elements raised to a specified power
	 * @return
	 */
	public double elementAbsPowSum(double p);
	
	/**
	 * Returns the number of components of this array.
	 * 
	 * May return 0 if components are not supported
	 * @return
	 */
	public int componentCount();
	
	/**
	 * Gets a component from the array at the specified index
	 * 
	 * Will throw an error if components are not supported, or if the component is out of bounds as defined by 0 <= k <componentCount()
	 * @param k
	 * @return
	 */
	public INDArray getComponent(int k);
	
	/**
	 * Returns a new array of the same shape/structure as the original but with the specified components. 
	 * 
	 * Components must be of a compatible type and identical shape to the original components.
	 */
	public INDArray withComponents(INDArray... components);

	/**
	 * Gets all components in a new array, with length componentCount();
	 * 
	 * @return
	 */
	INDArray[] getComponents();

	/**
	 * Applies a binary operator to this array and a second array.
	 * 
	 * Broadcasts the second array to the shape of this array if required.
	 * 
	 * @param op
	 * @param b
	 */
	public void applyOp(Op2 op, INDArray b);
	
	/**
	 * Applies a binary operator to this array and a double value.
	 * 
	 * Works as if the double value was broadcast to the shape of this array.
	 * 
	 * @param op
	 * @param b
	 */
	public void applyOp(Op2 op, double b);

	/**
	 * Reduces each vector slice of the array by the given binary operator
	 * Reduces down to a single vector
	 * 
	 * @param op
	 * @param b
	 */	
	public AVector reduceSlices(Op2 op);
	
	/**
	 * Reduces each slice of the array by the given binary operator.
	 * Results are returned in a vector with one element for each slice.
	 * Uses the specified initial value for reduction of each slice.
	 * 
	 * @param op
	 * @param b
	 */	
	public AVector reduceSlices(Op2 op, double init);

}
