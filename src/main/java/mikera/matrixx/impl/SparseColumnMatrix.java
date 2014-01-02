package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix stored as a collection of sparse column vectors
 * 
 * @author Mike
 *
 */
public class SparseColumnMatrix extends AMatrix implements ISparse {
	protected final int rowCount;	
	protected int columnCount;	
	protected AVector[] cols;

	public SparseColumnMatrix(AVector... columns) {
		this(columns,columns[0].length(),columns.length);
	}

	protected SparseColumnMatrix(AVector[] columns, int rowCount, int columnCount) {
		cols=columns;
		this.rowCount=rowCount;
		this.columnCount=columnCount;
	}

	@Override
	public int rowCount() {
		return rowCount;
	}

	@Override
	public int columnCount() {
		return columnCount;
	}

	@Override
	public double get(int row, int column) {
		if ((column<0)||(column>=columnCount)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		return cols[column].get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		if ((column<0)||(column>=columnCount)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		cols[column].set(row,value);
	}
	
	@Override
	public AVector getColumn(int i) {
		return cols[i];
	}
	
	@Override
	public void copyColumnTo(int i, double[] data, int offset) {
		cols[i].getElements(data, offset);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return cols[column].get(row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		cols[column].set(row,value);
	}

	@Override
	public boolean isMutable() {
		for (AVector v:cols) {
			if (v.isMutable()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		for (AVector v:cols) {
			if (!v.isFullyMutable()) return false;
		}
		return true;
	}

	@Override
	public SparseColumnMatrix exactClone() {
		SparseColumnMatrix a=new SparseColumnMatrix(cols.clone());
		for (int i=0; i<columnCount; i++) {
			cols[i]=cols[i].exactClone();
		}
		return a;
	}
	
	@Override
	public SparseRowMatrix getTranspose() {
		return new SparseRowMatrix(cols,columnCount,rowCount);
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<columnCount; i++) {
			cols[i].applyOp(op);
		}
	}

	public static AMatrix create(AVector... columns) {
		int cc=columns.length;
		int rc=columns[0].length();
		for (int i=1; i<cc; i++) {
			if (columns[i].length()!=rc) throw new IllegalArgumentException("Mismatched row count at column: "+i);
		}
		return new SparseColumnMatrix(columns.clone());
	}

}
