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
	public void copyRowTo(int i, double[] dest, int destOffset) {
		for (int j=0; j<cols; j++) {
			dest[destOffset+j]=data[i+j*rows];
		}
	}

	@Override
	public void copyColumnTo(int j, double[] dest, int destOffset) {
		System.arraycopy(data, j*rows, dest, destOffset, rows);
	}

	@Override
	protected int index(int i, int j) {
		return i+j*rows;
	}
	
	@Override
	public double get(int i, int j) {
		if ((i < 0) || (i >= rows))
			throw new IndexOutOfBoundsException();
		return data[(j * rows) + i];
	}
	
	@Override
	public void set(int i, int j, double value) {
		if ((i < 0) || (i >= rows))
			throw new IndexOutOfBoundsException();
		data[(j * rows) + i] = value;
	}

	@Override
	public void unsafeSet(int i, int j, double value) {
		data[(j * rows) + i] = value;
	}

	@Override
	public double unsafeGet(int i, int j) {
		return data[(j * rows) + i];
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		data[(j * rows) + i] += d;
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isPackedArray() {
		return (cols<=1);
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
