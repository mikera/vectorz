package mikera.matrixx.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ZeroVector;
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

	protected final HashMap<Integer,AVector> data;

	protected SparseColumnMatrix(int rowCount, int columnCount) {
		this(new HashMap<Integer,AVector>(),rowCount,columnCount);
	}
	
	protected SparseColumnMatrix(HashMap<Integer,AVector> data, int rowCount, int columnCount) {
		super(rowCount,columnCount);
		this.data=data;
	}

	protected SparseColumnMatrix(AVector[] columns, int rowCount, int columnCount) {
		super(rowCount,columnCount);
		data=new HashMap<Integer,AVector>();
		for (int i=0; i<cols; i++) {
			AVector v=columns[i];
			if ((v!=null)&&(!v.isZero())) {
				data.put(i, columns[i]);
			}
		}
	}
	
	public static SparseColumnMatrix create(AVector... columns) {
		return wrap(columns);
	}
	
	public static SparseColumnMatrix wrap(AVector... columns) {
		int cc=columns.length;
		int rc=columns[0].length();
		return new SparseColumnMatrix(columns,rc,cc);
	}
	
	public static AMatrix create(AMatrix source) {
		int cc=source.columnCount();
		int rc=source.rowCount();
		HashMap<Integer,AVector> data=new HashMap<Integer,AVector>();
		for (int i=0; i<cc; i++) {
			AVector col=source.getColumn(i);
			if (!(col.isZero())) data.put(i, Vectorz.createSparse(col));
		}
		return new SparseColumnMatrix(data,rc,cc);
	}
	
	public static SparseColumnMatrix wrap(HashMap<Integer,AVector> cols, int rowCount, int columnCount) {
		return new SparseColumnMatrix(cols,rowCount,columnCount);
	}


	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isZero() {
		for (Entry<Integer,AVector> e:data.entrySet()) {
			if (!e.getValue().isZero()) return false;
		}
		return true;
	}

	@Override
	public double get(int row, int column) {
		return getColumn(column).get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		AVector v=getColumn(column);
		if (v.isFullyMutable()) {
			v.set(row,value);
		} else {
			v=v.mutable();
			replaceColumn(column,v);
			v.set(row,value);
		}
	}
	
	
	@Override
	public double unsafeGet(int row, int column) {
		return getColumn(column).get(row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		AVector v=getColumn(column);
		if (v.isFullyMutable()) {
			v.unsafeSet(row,value);
		} else {
			v=v.mutable();
			replaceColumn(column,v);
			v.unsafeSet(row,value);
		}
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		AVector v=getColumn(j);
		if (v.isFullyMutable()) {
			v.addAt(i, d);
		} else {
			v=v.mutable();
			v.addAt(i, d);
			replaceColumn(j,v);
		}
	}
	
	@Override
	public AVector getColumn(int i) {
		if ((i<0)||(i>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, i));
		AVector v= data.get(i);
		if (v==null) return ZeroVector.create(rows);
		return v;
	}
	
	public void replaceColumn(int i, AVector col) {
		if ((i<0)||(i>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, i));
		if (col.length()!=rows) throw new IllegalArgumentException(ErrorMessages.incompatibleShape(col));
		data.put(i,col);
	}
	
	@Override
	public void copyColumnTo(int i, double[] data, int offset) {
		getColumn(i).getElements(data, offset);
	}
	
	@Override
	public long nonZeroCount() {
		long result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().nonZeroCount();
		}
		return result;
	}	
	
	@Override
	public double elementSum() {
		double result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSum();
		}
		return result;
	}	

	@Override
	public double elementSquaredSum() {
		double result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSquaredSum();
		}
		return result;
	}	

	@Override
	public SparseColumnMatrix exactClone() {
		SparseColumnMatrix result= new SparseColumnMatrix(rows,cols);
		for (Entry<Integer,AVector> e:data.entrySet()) {
			AVector col=e.getValue();
			if (!col.isZero()) {
				result.replaceColumn(e.getKey(), col.exactClone());
			}
		}
		return result;
	}
	
	@Override
	public SparseRowMatrix getTranspose() {
		return SparseRowMatrix.wrap(data,cols,rows);
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<cols; i++) {
			AVector col=getColumn(i);
			if (col.isFullyMutable()) {
				col.applyOp(op);
			} else {
				col=col.mutable();
				col.applyOp(op);
				replaceColumn(i,col);
			}
		}
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		Matrix m=Matrix.create(cols, rows);
		for (Entry<Integer,AVector> e:data.entrySet()) {
			int i=e.getKey();
			getColumn(i).getElements(m.data, rows*i);
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
				r.unsafeSet(i,j,getColumn(i).dotProduct(a.getColumn(j)));
			}
		}
		return r;		
	}
	
	@Override
	public void validate() {
		super.validate();
		for (int i=0; i<cols; i++) {
			if (getColumn(i).length()!=rows) throw new VectorzException("Invalid row count at column: "+i);
		}
	}

}
