package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Abstract class for regular rectangular matrices that maintain a row and column count
 * @author Mike
 *
 */
public abstract class ARectangularMatrix extends AMatrix {
	protected int rows;
	protected int cols;
	
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
}
