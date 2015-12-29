package mikera.matrixx.impl;

import java.util.List;

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
	private static final long serialVersionUID = 4350297037540121584L;

	private TransposedMatrix(AMatrix source) {
		super(source.columnCount(),source.rowCount(),source);
	}

	public static AMatrix wrap(AMatrix m) {
		if (m instanceof TransposedMatrix)
			return ((TransposedMatrix) m).source;
		return new TransposedMatrix(m);
	}
	
	@Override
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}
	
	@Override
	public boolean isMutable() {
		return source.isMutable();
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
	public double unsafeGet(int row, int column) {
		return source.unsafeGet(column, row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		source.unsafeSet(column, row, value);
	}

	@Override
	public AVector getRow(int row) {
		return source.getColumn(row);
	}

	@Override
	public AVector getColumn(int column) {
		return source.getRow(column);
	}
	
	@Override
	public AVector getRowClone(int row) {
		return source.getColumnClone(row);
	}

	@Override
	public AVector getColumnClone(int column) {
		return source.getRowClone(column);
	}
	
	@Override
	public AVector getRowView(int row) {
		return source.getColumnView(row);
	}

	@Override
	public AVector getColumnView(int column) {
		return source.getRowView(column);
	}
	
	@Override
	public List<AVector> getRows() {
		return source.getColumns();
	}
	
	@Override
	public List<AVector> getColumns() {
		return source.getRows();
	}

	@Override
	public int sliceCount() {
		return source.columnCount();
	}
	
	@Override
	public double trace() {
		return source.trace();
	}
	
	@Override
	public double diagonalProduct() {
		return source.diagonalProduct();
	}

	@Override
	public Matrix toMatrixTranspose() {
		return source.toMatrix();
	}

	@Override
	public Matrix toMatrix() {
		return source.toMatrixTranspose();
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
	public void getElements(double[] dest, int destOffset) {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			source.copyColumnTo(i, dest, destOffset+i*cc);
		}
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
	public boolean isSparse() {
		return source.isSparse();
	}
	
	@Override
	public boolean isZero() {
		return source.isZero();
	}

	@Override
	public boolean isUpperTriangular() {
		return source.isLowerTriangular();
	}
	
	@Override
	public int lowerBandwidthLimit() {
		return source.upperBandwidthLimit();
	}
	
	@Override
	public int lowerBandwidth() {
		return source.upperBandwidth();
	}
	
	@Override
	public int upperBandwidthLimit() {
		return source.lowerBandwidthLimit();
	}
	
	@Override
	public int upperBandwidth() {
		return source.lowerBandwidth();
	}
	
	@Override
	public AVector getBand(int i) {
		return source.getBand(-i);
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
	
	@Override
	public AMatrix getTransposeCopy() {
		return source.copy();
	}
	
	@Override
	public AMatrix transposeInnerProduct(AMatrix s) {
		return source.innerProduct(s);
	}

	@Override
	public AMatrix transposeInnerProduct(Matrix s) {
		return source.innerProduct(s);
	}
	
	@Override
	public AMatrix innerProduct(AMatrix s) {
		return source.transposeInnerProduct(s);
	}
	
	@Override
	public AMatrix sparseClone() {
		if (source instanceof IFastColumns) {
			return SparseRowMatrix.create(source.getColumns());
		} else if (source instanceof IFastRows) {
			return SparseColumnMatrix.create(source.getRows());
		}
		return SparseRowMatrix.create(source.getColumns());
	}

	@Override
	public TransposedMatrix exactClone() {
		return new TransposedMatrix(source.exactClone());
	}
	
	@Override
	public boolean equals(AMatrix m) {
		return m.equalsTranspose(source);
	}
	
	@Override
	public boolean equalsTranspose(AMatrix a) {
		return source.equals(a);
	}
}
