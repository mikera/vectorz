package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;

/**
 * Class representing a transposed view of another matrix The transposed matrix
 * is a reference to the underlying matrix data
 * 
 * @author Mike
 * 
 */
public class TransposedMatrix extends ADelegatedMatrix {

	private TransposedMatrix(AMatrix source) {
		super(source);
	}

	public static AMatrix wrap(AMatrix m) {
		if (m instanceof TransposedMatrix)
			return ((TransposedMatrix) m).source;
		return new TransposedMatrix(m);
	}

	public int rowCount() {
		return source.columnCount();
	}

	@Override
	public int columnCount() {
		return source.rowCount();
	}

	@Override
	public double get(int row, int column) {
		return source.get(column, row);
	}

	@Override
	public void set(int row, int column, double value) {
		source.set(column, row, value);
	}

	@Override
	public AVector getRow(int row) {
		return source.getColumn(row);
	}

	@Override
	public AVector slice(int rowNumber) {
		return source.getColumn(rowNumber);
	}

	@Override
	public int sliceCount() {
		return source.columnCount();
	}

	@Override
	public Matrix toMatrixTranspose() {
		if (source instanceof Matrix) {
			return (Matrix) source;
		} else {
			return source.toMatrix();
		}
	}

	@Override
	public Matrix toMatrix() {
		return source.toMatrixTranspose();
	}

	@Override
	public AVector getColumn(int column) {
		return source.getRow(column);
	}

	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		source.copyColumnTo(row, dest, destOffset);
	}

	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		source.copyRowTo(col, dest, destOffset);
	}

	@Override
	public double determinant() {
		return source.determinant();
	}

	@Override
	public boolean isSymmetric() {
		return source.isSymmetric();
	}

	@Override
	public boolean isUpperTriangular() {
		return source.isLowerTriangular();
	}

	@Override
	public boolean isLowerTriangular() {
		return source.isUpperTriangular();
	}

	@Override
	public AMatrix getTranspose() {
		// Transposing again just gets us back to the original source matrix
		return source;
	}

	@Override
	public AMatrix getTransposeView() {
		// Transposing again just gets us back to the original source matrix
		return source;
	}

	public Matrix transposeInnerProduct(Matrix s) {
		return source.innerProduct(s);
	}

	@Override
	public TransposedMatrix exactClone() {
		return new TransposedMatrix(source.exactClone());
	}
}
