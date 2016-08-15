package mikera.matrixx;

import java.util.List;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Interface to specify matrix-specific operations.
 * 
 * Matrix implementations should extend AMatrix which implements this interface,
 * contains important functionality implementations, and serves as the abstract base class 
 * for all Vectorz matrices.
 * 
 * @author Mike
 *
 */
public interface IMatrix extends INDArray {
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
	@Override
	public double get(int row, int column);

	/**
	 * Sets the element at the specified position in this matrix
	 * @return
	 */
	@Override
	public void set(int row, int column, double value);
	
	/**
	 * Returns a row of the matrix. May or may not be a view, depending on matrix type.
	 * 
	 * Intended for the fastest possible read access of the row. This often means a view, 
	 * but might not be (e.g. getRow on a Matrix33 returns a Vector3).
	 */
	public AVector getRow(int row);
	
	/**
	 * Returns a column of the matrix. May or may not be a view, depending on matrix type.
	 * 
	 * Intended for the fastest possible read access of the column. This often means a view, 
	 * but might not be (e.g. getColumn on a Matrix33 returns a Vector3).
	 */
	public AVector getColumn(int column);
	
	/**
	 * Returns a row of the matrix as a vector view. May be used to modify the original matrix.
	 */
	public AVector getRowView(int row);

	/**
	 * Returns a column of the matrix as a vector view. May be used to modify the original matrix.
	 */
	public AVector getColumnView(int column);
	
	/**
	 * Returns a row of the matrix as a new cloned, mutable vector.
	 * 
	 * The cloned row may be modified without affecting the original matrix.
	 */
	public AVector getRowClone(int row);

	/**
	 * Returns a column of the matrix as a new cloned, mutable vector
	 * 
	 * The cloned column may be modified without affecting the original matrix.
	 */
	public AVector getColumnClone(int column);

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
	 * Returns the transpose of this matrix. 
	 * 
	 * Will be a transposed view by default, but specialised matrix type may override this if they are able to provide
	 * a better implementation.
	 * 
	 * @return the transpose of this matrix
	 */
	@Override
	public AMatrix getTranspose();
	
	/**
	 * Returns a transposed view of this matrix. 
	 * 
	 * @return the transpose of this matrix, as a view
	 */
	@Override
	public AMatrix getTransposeView();
	
	/**
	 * Transposes a matrix in place, if possible.
	 * Throws an exception if this is not possible (e.g. if the matrix is not square or not sufficiently mutable)
	 */
	public void transposeInPlace();

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
	
	/**
	 * Checks if this is an identity matrix (i.e. 1.0 in leading diagonal, 0.0 elsewhere)
	 * 
	 * @return true if this matrix is a square identity matrix, false otherwise.
	 */
	boolean isIdentity();

	/**
	 * Transforms a source vector into a using matrix multiplication (inner product).
	 * 
	 * @return A new vector
	 */
	@Override
	public AVector innerProduct(AVector source);

	/**
	 * Transforms a dense source Vector using matrix multiplication (inner product).
	 * 
	 * @return A new dense vector
	 */
	public Vector innerProduct(Vector source);
	
	/**
	 * Transforms a source vector into a destination vector, using matrix multiplication (inner product).
	 * The destination vector is overwritten.
	 */
	void transform(AVector source, AVector dest);

	void transformInPlace(AVector v);

	/**
	 * Computes the inverse of this matrix.
	 * 
	 * @return A matrix which is the inverse of this matrix, or null if the inverse does not exist.
	 */
	AMatrix inverse();
	
	/**
	 * Computes the trace of a matrix, i.e. the sum of all elements on the leading diagonal
	 * 
	 * @return
	 */
	public double trace();

	/**
	 * Adds another matrix to this matrix, returning a new matrix
	 * @param v
	 * @return
	 */
	public AMatrix addCopy(AMatrix a);
	
	/**
	 * Adds a vector to every row of this matrix, returning a new matrix
	 * @param v
	 * @return
	 */
	public AMatrix addCopy(AVector v);

	/**
	 * Returns the product of all elements on the leading diagonal of this matrix
	 * @return
	 */
	double diagonalProduct();
	
	/**
	 * Computes the inner product of this matrix with another.
	 * 
	 * Equivalent to matrix x matrix multiplication
	 * @param a
	 * @return
	 */
	public AMatrix innerProduct(AMatrix a);

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

	/**
	 * Returns true iff this matrix is a square diagonal matrix
	 */
	boolean isDiagonal();
	
	/**
	 * Returns true iff this matrix is a diagonal matrix (might not be square)
	 */
	boolean isRectangularDiagonal();

	/**
	 * Converts this matrix to nested double[][] array
	 * 
	 * @return
	 */
	double[][] toNestedDoubleArrays();

	/**
	 * Gets a rectangular submatrix view of part of a matrix
	 */
	AMatrix subMatrix(int rowStart, int rows, int colStart, int cols);

	/**
	 * Adds a value at a specific position in the matrix, mutating the matrix
	 * 
	 * Does not perform bounds checking - this in an unsafe operation
	 */
	void addAt(int i, int j, double d);



}
