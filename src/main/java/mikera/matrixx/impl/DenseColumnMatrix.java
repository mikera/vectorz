package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.impl.ArraySubVector;

/**
 * A densely packed matrix organised in column-major format.
 * 
 * Transposes to/from a regular dense Matrix
 * 
 * @author Mike
 *
 */
public class DenseColumnMatrix extends AStridedMatrix implements IFastColumns {
	private static final long serialVersionUID = 5459617932072332096L;

	private DenseColumnMatrix(int rowCount, int columnCount, double[] data) {
		super(data, rowCount, columnCount);
	}
	
	private DenseColumnMatrix(int rowCount, int columnCount) {
		this(rowCount, columnCount, Matrix.createStorage(rowCount, columnCount));
	}
	
	public static DenseColumnMatrix wrap(int rows, int cols, double[] data) {
		return new DenseColumnMatrix(rows, cols, data);
	}
	
	@Override
	public int getArrayOffset() {
		return 0;
	}

	@Override
	public int rowStride() {
		return 1;
	}

	@Override
	public int columnStride() {
		return rows;
	}
	
	@Override
	public ArraySubVector getColumnView(int j) {
		return ArraySubVector.wrap(data, j*rows, rows);
	}

	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		for (int i=0; i<cols; i++) {
			dest[destOffset+i]=data[row+i*rows];
		}
	}

	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		System.arraycopy(data, col*rows, dest, destOffset, rows);
	}

	@Override
	protected int index(int i, int j) {
		return i+j*rows;
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public Matrix getTranspose() {
		return getTransposeView();
	}
	
	@Override
	public Matrix getTransposeView() {
		return Matrix.wrap(cols, rows, data);
	}

	@Override
	public AMatrix exactClone() {
		return new DenseColumnMatrix(rows,cols,data.clone());
	}

}
