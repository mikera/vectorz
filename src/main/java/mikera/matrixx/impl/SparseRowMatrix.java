package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Matrix stored as a collection of sparse row vectors
 * 
 * @author Mike
 *
 */
public class SparseRowMatrix extends VectorMatrixMN implements ISparse {

	protected SparseRowMatrix(AVector... vectors) {
		super(vectors);
	}
	
	public SparseRowMatrix(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	protected SparseRowMatrix(AVector[] rows, int rowCount, int columnCount) {
		super(rows,rowCount,columnCount);
	}
	
	public static SparseRowMatrix create(AMatrix source) {
		int rc=source.rowCount();
		AVector[] rows=new AVector[rc];
		for (int i=0; i<rc; i++) {
			rows[i]=Vectorz.createSparse(source.getRow(i));
		}
		return new SparseRowMatrix(rows);
	}
	
	@Override
	public boolean isMutable() {
		for (AVector v:rows) {
			if (v.isMutable()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		for (AVector v:rows) {
			if (!v.isFullyMutable()) return false;
		}
		return true;
	}
	
	public static AMatrix create(AVector... rows) {
		int rc=rows.length;
		int cc=rows[0].length();
		for (int i=1; i<rc; i++) {
			if (rows[i].length()!=cc) throw new IllegalArgumentException("Mismatched column count at row: "+i);
		}
		return new SparseRowMatrix(rows.clone());
	}

	@Override
	public SparseColumnMatrix getTranspose() {
		return new SparseColumnMatrix(rows,columnCount,rowCount);
	}
	
	@Override
	public SparseRowMatrix exactClone() {
		AVector[] vs=rows.clone();
		for (int i=0; i<rowCount; i++) {
			vs[i]=vs[i].exactClone();
		}
		return new SparseRowMatrix(vs,rowCount,columnCount);
	}
}
