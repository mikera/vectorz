package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.SubMatrixViewVector;

/**
 * Matrix view class that wraps a SubMatrix of another matrix
 * @author Mike
 *
 */
public class SubMatrixView extends ARectangularMatrix {
	private static final long serialVersionUID = -4687657697203057278L;
	private AMatrix source;
	private int rowStart;
	private int colStart;

	public SubMatrixView(AMatrix source, int rowStart, int colStart, int rows, int cols) {
		super(rows, cols);
		source.checkRow(rowStart);
		source.checkRow(rowStart+rows-1);
		source.checkColumn(colStart);
		source.checkColumn(colStart+cols-1);
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
	public boolean isMutable() {
		return source.isMutable();
	}

	@Override
	public AMatrix exactClone() {
		return new SubMatrixView(source.exactClone(),rowStart,colStart,rows,cols);
	}
	
	@Override
	public AVector asVector() {
		return new SubMatrixViewVector(source, rowStart, colStart, rows, cols);
	}
	
	@Override
	public AVector getRow(int i) {
		checkRow(i);
		return source.getRow(rowStart+i).subVector(colStart, cols);
	}
	
	@Override
	public AVector getColumn(int j) {
		checkColumn(j);
		return source.getColumn(colStart+j).subVector(rowStart, rows);
	}
	
	@Override
	public AVector getRowView(int i) {
		checkRow(i);
		return source.getRowView(rowStart+i).subVector(colStart, cols);
	}
	
	@Override
	public AVector getColumnView(int j) {
		checkColumn(j);
		return source.getColumnView(colStart+j).subVector(rowStart, rows);
	}
	
	@Override
	public AVector getRowClone(int i) {
		checkRow(i);
		return source.getRow(rowStart+i).subVector(colStart, cols).clone();
	}
	
	@Override
	public AVector getColumnClone(int j) {
		checkColumn(j);
		return source.getColumn(colStart+j).subVector(rowStart, rows).clone();
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		source.getRow(rowStart+row).copyTo(colStart, dest, destOffset, cols);
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		source.getColumn(colStart+col).copyTo(rowStart, dest, destOffset, rows);
	}	

}
