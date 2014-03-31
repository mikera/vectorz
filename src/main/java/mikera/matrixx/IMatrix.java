package mikera.matrixx;

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



}
