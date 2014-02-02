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
