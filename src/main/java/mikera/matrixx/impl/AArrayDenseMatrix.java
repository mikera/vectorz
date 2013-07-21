package mikera.matrixx.impl;

/**
 * Abstract base class for matrices wrapping  a dense (rows*cols) subset of a double[] array
 * @author Mike
 *
 */
public abstract class AArrayDenseMatrix extends AStridedMatrix {

	protected AArrayDenseMatrix(double[] data, int rows, int cols) {
		super(data, rows, cols);
	}

	public abstract int getArrayOffset();
	
	@Override
	public boolean isPackedArray() {
		return (getArrayOffset()==0) && (data.length ==(rows*cols));
	}
}
