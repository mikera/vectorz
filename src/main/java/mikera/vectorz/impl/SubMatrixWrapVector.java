package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * A vector that wraps a submatrix of an arbitrary matrix
 * @author Mike
 *
 */
public class SubMatrixWrapVector extends ASizedVector {
	private static final long serialVersionUID = -839770692590916488L;
	
	private int rowStart;
	private int colStart;
	private int rows;
	private int cols;
	private AMatrix source;

	protected SubMatrixWrapVector(AMatrix source, int rowStart, int colStart, int rows, int cols) {
		super(rows*cols);
		source.checkRow(rowStart);
		source.checkRow(rowStart+rows-1);
		source.checkColumn(colStart);
		source.checkColumn(colStart+cols-1);
		
		this.source=source;
		this.rowStart=rowStart;
		this.colStart=colStart;
		this.rows=rows;
		this.cols=cols;
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return source.unsafeGet(rowStart+i/cols, colStart+i%cols);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		source.unsafeSet(rowStart+i/cols, colStart+i%cols,value);
	}

	@Override
	public AVector exactClone() {
		return new SubMatrixWrapVector(source.exactClone(),rowStart,colStart,rows,cols);
	}

	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<rows; i++) {
			result+=source.getRow(rowStart+i).subVector(colStart, cols).dotProduct(data, offset);
			offset+=cols;
		}
		return result;
	}

}
