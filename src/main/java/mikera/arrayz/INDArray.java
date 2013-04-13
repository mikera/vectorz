package mikera.arrayz;

import mikera.vectorz.AVector;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;

/**
 * Interface for general multi-dimensional arrays of doubles
 * @author Mike
 */
public interface INDArray extends Cloneable {
	
	public int dimensionality();
	
	/**
	 * Returns the shape of the array as an array of ints.
	 * @return
	 */
	public int[] getShape();
	
	public double get();
	public double get(int x);
	public double get(int x, int y);
	public double get(int... indexes);

	public void set(double value);
	public void set(int x, double value);
	public void set(int x, int y, double value);
	public void set(int[] indexes, double value);
	public void set(INDArray a);
	public void set(Object o);

	
	/**
	 * Creates a view of the array as a single vector in row-major order.
	 * @return
	 */
	public AVector asVector();
	
	public INDArray reshape(int... dimensions);
	
	public INDArray slice(int majorSlice);
	
	public long elementCount();
	
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
}
