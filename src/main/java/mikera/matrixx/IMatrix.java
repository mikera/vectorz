package mikera.matrixx;

import java.util.List;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;

/**
 * Interface for fundamental matrix access operations.
 * 
 * Matrix implementation should generally extend AMatrix which contains important
 * functionality implementations.
 * 
 * @author Mike
 *
 */
public interface IMatrix {
	/**
	 * Gets the number of rows in this matrix (dimension 0)
	 * @return
	 */
	public int rowCount();

	/**
	 * Gets the number of columns in this matrix (dimension 1)
	 * @return
	 */
	public int columnCount();

	/**
	 * Gets the element at the specified position in this matrix
	 * @return
	 */
	public double get(int row, int column);

	/**
	 * Sets the element at the specified position in this matrix
	 * @return
	 */
	public void set(int row, int column, double value);
	
	/**
	 * Gets a matrix row as a vector. May or may not be a mutable view.
	 * @param row
	 * @return
	 */
	public AVector getRow(int row);
	
	/**
	 * Gets a matrix column as a vector. May or may not be a mutable view.
	 * @param row
	 * @return
	 */
	public AVector getColumn(int column);

	/**
	 * Gets a band of a matrix
	 * 
	 * The band is defined such that:
	 * - 0 is the main diagonal
	 * - bands above the main diagonal are 1, 2, 3 etc.
	 * - bands below the main diagonal are -1, -2, -3 etc.
	 * 
	 * @param band
	 * @return
	 */
	public AVector getBand(int band);

	/**
	 * Checks if this is a square matrix
	 * 
	 * @return true if square, false otherwise
	 */
	boolean isSquare();

	/**
	 * Checks if this is an invertible matrix (i.e. a square matrix with a non-zero determinant)
	 * 
	 * @return true if invertible, false otherwise
	 */
	boolean isInvertible();	

	AVector transform(AVector source);

	void transform(AVector source, AVector dest);

	void transformInPlace(AVector v);

	/**
	 * Computes the inverse of this matrix.
	 * 
	 * @return A matrix which is the inverse of this matrix, or null if the inverse does not exist.
	 */
	AMatrix inverse();

	AMatrix addCopy(AMatrix a);

	double diagonalProduct();

	/**
	 * Gets a List of rows of a matrix.
	 * 
	 * May return either copies or views, depending on the specific matrix type.
	 * @return
	 */
	List<AVector> getRows();

	/**
	 * Gets a List of columns of a matrix.
	 * 
	 * May return either copies or views, depending on the specific matrix type.
	 * @return
	 */
	List<AVector> getColumns();

	/**
	 * Returns true if this matrix is symmetric 
	 * 
	 * @return
	 */
	boolean isSymmetric();

	/**
	 * Adds two matrices to this matrix. May be better optimised than adding both matrices individually.
	 * 
	 * @param a
	 * @param b
	 */
	void add2(AMatrix a, AMatrix b);



}
