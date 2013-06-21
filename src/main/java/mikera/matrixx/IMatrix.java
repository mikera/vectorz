package mikera.matrixx;

import mikera.arrayz.INDArray;

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
	
	public abstract int rowCount();

	public abstract int columnCount();

	public abstract double get(int row, int column);

	public abstract void set(int row, int column, double value);	

}
