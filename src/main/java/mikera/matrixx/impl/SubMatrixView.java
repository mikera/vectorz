package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * Matrix class that wraps a SubMatrix of another matrix
 * @author Mike
 *
 */
public class SubMatrixView extends ARectangularMatrix {
	private static final long serialVersionUID = -4687657697203057278L;
	private AMatrix source;
	private int rowStart;
	private int colStart;

	protected SubMatrixView(AMatrix source, int rowStart, int colStart, int rows, int cols) {
		super(rows, cols);
		this.source=source;
		this.rowStart=rowStart;
		this.colStart=colStart;
	}

	@Override
	public double get(int i, int j) {
		checkIndex(i,j);
		return unsafeGet(i,j);
	}

	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		unsafeSet(i,j,value);
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return source.unsafeGet(rowStart+i, colStart+j);
	}

	@Override
	public void unsafeSet(int i, int j, double value) {
		source.unsafeSet(rowStart+i, colStart+j,value);
	}

	@Override
	public boolean isFullyMutable() {
		return source.isFullyMutable();
	}

	@Override
	public AMatrix exactClone() {
		return new SubMatrixView(source.exactClone(),rowStart,colStart,rows,cols);
	}

}
