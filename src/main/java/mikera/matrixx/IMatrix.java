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

	public AVector getBand(int band);

	boolean isSquare();

	boolean isInvertible();	

	AVector transform(AVector source);

	void transform(AVector source, AVector dest);

	void transformInPlace(AVector v);

	AMatrix inverse();

	AMatrix addCopy(AMatrix a);

	double diagonalProduct();

	List<AVector> getRows();

	List<AVector> getColumns();

	boolean isSymmetric();

	/**
	 * Adds two matrices to this matrix. May be better optimised than adding both matrices individually.
	 * 
	 * @param a
	 * @param b
	 */
	void add(AMatrix a, AMatrix b);



}
