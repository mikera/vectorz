package mikera.matrixx.impl;

import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix class that wraps an arbitrary vector as a single-row matrix
 * @author Mike
 */
public class RowMatrix extends AVectorAsMatrixView implements IFastColumns, IFastRows {
	private static final long serialVersionUID = 2636365975400418264L;

	public RowMatrix(AVector v) {
		super(v,1,v.length());
	}
	
	public static RowMatrix wrap(AVector v) {
		return new RowMatrix(v);
	}

	@Override
	public double get(int row, int column) {
		if (row!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		return vector.get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		if (row!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		vector.set(column,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return vector.unsafeGet(column);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		vector.unsafeSet(column,value);
	}
	
	@Override
	public AVector getRowView(int i) {
		if (i!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		return vector;
	}
	
	@Override
	public AVector getColumnView(int i) {
		return vector.subVector(i, 1);
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		assert(i==0);
		vector.addAt(j,d);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		vector.addToArray(data, offset);
	}
	
	@Override
	public ColumnMatrix getTranspose() {
		return new ColumnMatrix(vector);
	}
	
	@Override
	public ColumnMatrix getTransposeView() {
		return new ColumnMatrix(vector);
	}

	@Override
	public RowMatrix exactClone() {
		return new RowMatrix(vector.exactClone());
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		if (row!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, row));
		vector.getElements(dest, destOffset);
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		dest[destOffset]=vector.get(col);
	}
	
	@Override
	public Matrix transposeInnerProduct(Matrix s) {
		if (s.rowCount()!=1) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, s));
		int rc=this.columnCount();
		int cc=s.columnCount();
		Matrix m=Matrix.create(rc,cc);
		for (int i=0; i<rc; i++) {
			double ti=vector.unsafeGet(i);
			DoubleArrays.addMultiple(m.data, i*cc, s.data, 0, cc, ti);
		}
		return m;
	}
}
