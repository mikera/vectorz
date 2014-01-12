package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Matrix stored as a collection of sparse column vectors
 * 
 * This format is especially efficient for:
 * - transposeInnerProduct() with another matrix
 * - access via getColumn() operation
 * - transpose into SparseRowMatrix
 * 
 * @author Mike
 *
 */
public class SparseColumnMatrix extends ARectangularMatrix implements ISparse {
	private static final long serialVersionUID = -5994473197711276621L;

	protected final AVector[] columns;

	public SparseColumnMatrix(AVector... columns) {
		this(columns,columns[0].length(),columns.length);
	}

	protected SparseColumnMatrix(AVector[] columns, int rowCount, int columnCount) {
		super(rowCount,columnCount);
		this.columns=columns;
	}
	
	public static SparseColumnMatrix create(AVector... columns) {
		int cc=columns.length;
		int rc=columns[0].length();
		for (int i=1; i<cc; i++) {
			if (columns[i].length()!=rc) throw new IllegalArgumentException("Mismatched row count at column: "+i);
		}
		return new SparseColumnMatrix(columns.clone(),rc,cc);
	}
	
	public static SparseColumnMatrix wrap(AVector... columns) {
		int cc=columns.length;
		int rc=columns[0].length();
		for (int i=1; i<cc; i++) {
			if (columns[i].length()!=rc) throw new IllegalArgumentException("Mismatched row count at column: "+i);
		}
		return new SparseColumnMatrix(columns,rc,cc);
	}
	
	public static AMatrix create(AMatrix source) {
		int cc=source.columnCount();
		AVector[] columns=new AVector[cc];
		for (int i=0; i<cc; i++) {
			columns[i]=Vectorz.createSparse(source.getColumn(i));
		}
		return new SparseColumnMatrix(columns.clone());
	}

	@Override
	public boolean isMutable() {
		for (int i=0; i<cols; i++) {
			AVector v=columns[i];
			if (v.isMutable()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		for (int i=0; i<cols; i++) {
			AVector v=columns[i];
			if (!v.isFullyMutable()) return false;
		}
		return true;
	}
	
	@Override
	public boolean isZero() {
		for (int i=0; i<cols; i++) {
			AVector v=columns[i];
			if (!v.isZero()) return false;
		}
		return true;
	}

	@Override
	public double get(int row, int column) {
		if ((column<0)||(column>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		return columns[column].get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		if ((column<0)||(column>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		columns[column].set(row,value);
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		columns[j].addAt(i, d);
	}
	
	@Override
	public AVector getColumn(int i) {
		return columns[i];
	}
	
	public void replaceColumn(int i, AVector col) {
		if ((i<0)||(i>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		columns[i]=col;
	}
	
	@Override
	public void copyColumnTo(int i, double[] data, int offset) {
		columns[i].getElements(data, offset);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return columns[column].get(row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		columns[column].set(row,value);
	}
	
	@Override
	public long nonZeroCount() {
		int cc=cols;
		long result=0;
		for (int i=0; i<cc; i++) {
			result+=columns[i].nonZeroCount();
		}
		return result;
	}	
	
	@Override
	public double elementSum() {
		int cc=cols;
		double result=0;
		for (int i=0; i<cc; i++) {
			result+=columns[i].elementSum();
		}
		return result;
	}	

	@Override
	public double elementSquaredSum() {
		int cc=cols;
		double result=0;
		for (int i=0; i<cc; i++) {
			result+=columns[i].elementSquaredSum();
		}
		return result;
	}	

	@Override
	public SparseColumnMatrix exactClone() {
		SparseColumnMatrix a=new SparseColumnMatrix(columns.clone());
		for (int i=0; i<cols; i++) {
			columns[i]=columns[i].exactClone();
		}
		return a;
	}
	
	@Override
	public SparseRowMatrix getTranspose() {
		return new SparseRowMatrix(columns,cols,rows);
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<cols; i++) {
			columns[i].applyOp(op);
		}
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		Matrix m=Matrix.create(cols, rows);
		for (int i=0; i<cols; i++) {
			columns[i].getElements(m.data, rows*i);
		}
		return m;
	}
	
	@Override 
	public AMatrix transposeInnerProduct(AMatrix a) {
		int rc=this.columnCount(); // i.e. rowCount of transpose
		int cc=a.columnCount();
		Matrix r=Matrix.create(rc, cc);
		
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				r.unsafeSet(i,j,columns[i].dotProduct(a.getColumn(j)));
			}
		}
		return r;		
	}
	
	@Override
	public void validate() {
		super.validate();
		for (int i=0; i<cols; i++) {
			if (columns[i].length()!=rows) throw new VectorzException("Invalid row count at column: "+i);
		}
	}

}
