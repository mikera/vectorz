package mikera.matrixx.impl;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract class for regular rectangular matrices that maintain a final fixed row and column count.
 * 
 * Most concrete matrix implementations should inherit from this.
 * 
 * @author Mike
 *
 */
public abstract class ARectangularMatrix extends AMatrix {
	private static final long serialVersionUID = 6429003789294676974L;

	protected final int rows;
	protected final int cols;
	
	protected ARectangularMatrix(int rows, int cols) {
		this.rows=rows;
		this.cols=cols;
	}
		
	@Override
	public final int rowCount() {
		return rows;
	}
	
	@Override
	public final int columnCount() {
		return cols;
	}
	
	@Override
	public final int[] getShape() {
		return new int[] {rows,cols};
	}
	
	@Override
	public final int[] getShapeClone() {
		return new int[] {rows,cols};
	}
	
	@Override
	public int bandLength(int band) {
		return bandLength(rows,cols,band);
	}
	
	@Override
	public final int getShape(int dim) {
		if (dim==0) {
			return rows;
		} else if (dim==1) {
			return cols;
		} else {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
		}
	}	
	
	@Override
	public final boolean isSameShape(INDArray m) {
		return (m.dimensionality()==2)&&(rows==m.getShape(0))&&(cols==m.getShape(1));
	}
	
	@Override
	public int checkSquare() {
		int rc=rows;
		if (rc!=cols) throw new UnsupportedOperationException(ErrorMessages.nonSquareMatrix(this));
		return rc;
	}
	
	@Override
	protected void checkSameShape(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=m.rowCount())||(cc!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	protected int checkColumn(int column) {
		int cc=columnCount();
		if ((column<0)||(column>=cc)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, column));
		return cc;
	}
	
	protected int checkRow(int row) {
		int rc=rowCount();
		if ((row<0)||(row>=rc)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 0, row));
		return rc;
	}
	
	@Override
	protected void checkSameShape(ARectangularMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=m.rowCount())||(cc!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	@Override
	protected final void checkIndex(int i, int j) {
		if ((i<0)||(i>=rows)||(j<0)||(j>=cols)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		}
	}
	
	@Override
	public final boolean isSameShape(AMatrix m) {
		return (rows==m.rowCount())&&(cols==m.columnCount());
	}
	
	public final boolean isSameShape(ARectangularMatrix m) {
		return (rows==m.rows)&&(cols==m.cols);
	}
	
	@Override
	public final long elementCount() {
		return ((long)rows)*cols;
	}
}
