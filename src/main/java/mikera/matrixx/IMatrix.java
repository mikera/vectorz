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
public interface IMatrix extends INDArray {
	
	public int rowCount();

	public int columnCount();

	public double get(int row, int column);

	public void set(int row, int column, double value);
	
	public AVector getRow(int row);
	
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

	boolean isSquare();

	boolean isInvertible();	

	AVector transform(AVector source);

	void transform(AVector source, AVector dest);

	void transformInPlace(AVector v);

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

	boolean isSymmetric();

	/**
	 * Adds two matrices to this matrix. May be better optimised than adding both matrices individually.
	 * 
	 * @param a
	 * @param b
	 */
	void add2(AMatrix a, AMatrix b);



}
