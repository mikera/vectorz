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
	public final boolean isSquare() {
		return rows==cols;
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
	public void set(int i, int j,double value) {
		checkIndex(i,j);
		unsafeSet(i,j,value);
	}
	
	@Override
	public final int bandLength(int band) {
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
	public final int checkColumn(int column) {
		if ((column<0)||(column>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, column));
		return cols;
	}
	
	@Override
	public final int checkRow(int row) {
		if ((row<0)||(row>=rows)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 0, row));
		return rows;
	}
	
	@Override
	protected final void checkSameShape(AMatrix m) {
		if((rows!=m.rowCount())||(cols!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	@Override
	protected final void checkSameShape(ARectangularMatrix m) {
		if((rows!=m.rows)||(cols!=m.cols)) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	@Override
	protected final int checkRowCount(int expected) {
		int rc=rowCount();
		if (rc!=expected) throw new IllegalArgumentException("Unexpected row count: "+rc+" expected: "+expected);
		return rc;
	}
	
	@Override
	protected final int checkColumnCount(int expected) {
		int cc=columnCount();
		if (cc!=expected) throw new IllegalArgumentException("Unexpected column count: "+cc+" expected: "+expected);
		return cc;
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
	
	@Override
	public final double getElement(long i) { 
		if ((i<0)||(i>=(((long)rows)*cols))) throw new IndexOutOfBoundsException(ErrorMessages.invalidElementIndex(this,i));
		return unsafeGet((int)(i/cols),(int)(i%cols));
	}
}
