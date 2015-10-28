package mikera.vectorz.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * A vector that wraps a sub-matrix of an arbitrary matrix
 * @author Mike
 */
public class SubMatrixViewVector extends AMatrixViewVector {
	private static final long serialVersionUID = -839770692590916488L;
	
	private final int rowStart;
	private final int colStart;
	private final int rows;
	private final int cols;

	public SubMatrixViewVector(AMatrix source, int rowStart, int colStart, int rows, int cols) {
		super(source,rows*cols);
		source.checkRow(rowStart);
		source.checkRow(rowStart+rows-1);
		source.checkColumn(colStart);
		source.checkColumn(colStart+cols-1);
		
		this.rowStart=rowStart;
		this.colStart=colStart;
		this.rows=rows;
		this.cols=cols;
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return unsafeGet(i);
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		unsafeSet(i,value);
	}
	
	@Override
	public double unsafeGet(int i) {
		return source.unsafeGet(calcRow(i), calcCol(i));
	}

	@Override
	public void unsafeSet(int i, double value) {
		source.unsafeSet(calcRow(i), calcCol(i),value);
	}

	@Override
	public AVector exactClone() {
		return new SubMatrixViewVector(source.exactClone(),rowStart,colStart,rows,cols);
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

	@Override
	protected int calcRow(int i) {
		return rowStart+i/cols;
	}

	@Override
	protected int calcCol(int i) {
		return colStart+i%cols;
	}

}
