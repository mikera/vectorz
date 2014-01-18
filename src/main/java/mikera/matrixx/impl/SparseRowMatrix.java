package mikera.matrixx.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix stored as a collection of sparse row vectors.
 * 
 * This format is especially efficient for:
 * - innerProduct() with another matrix
 * - access via getRow() operation
 * - transpose into SparseColumnMatrix
 * 
 * @author Mike
 *
 */
public class SparseRowMatrix extends ARectangularMatrix implements ISparse, IFastRows {
	private static final long serialVersionUID = 8646257152425415773L;

	protected final HashMap<Integer,AVector> data;
	
	protected SparseRowMatrix(AVector... vectors) {
		super(vectors.length,vectors[0].length());	
		data=new HashMap<Integer,AVector>();
		for (int i=0; i<rows; i++) {
			AVector v=vectors[i];
			if ((v!=null)&&(!v.isZero())) data.put(i, vectors[i]);
		}
	}
	
	protected SparseRowMatrix(AVector[] vectors,int rowCount, int columnCount) {
		super(rowCount,columnCount);	
		data=new HashMap<Integer,AVector>();
		for (int i=0; i<rows; i++) {
			AVector v=vectors[i];
			if ((v!=null)&&(!v.isZero())) data.put(i, vectors[i]);
		}
	}
	
	protected SparseRowMatrix(int rowCount, int columnCount) {
		super(rowCount,columnCount);
		data=new HashMap<Integer,AVector>();
	}

	protected SparseRowMatrix(HashMap<Integer, AVector> data, int rowCount, int columnCount) {
		super(rowCount,columnCount);
		this.data=data;
	}
	
	public static SparseRowMatrix wrap(HashMap<Integer, AVector> data,
			int rows, int cols) {
		return new SparseRowMatrix(data,rows,cols);
	}
	
	public static SparseRowMatrix create(int rows, int cols) {
		return new SparseRowMatrix(rows,cols);
	}
	
	public static SparseRowMatrix create(AMatrix source) {
		int rc=source.rowCount();
		AVector[] rows=new AVector[rc];
		for (int i=0; i<rc; i++) {
			rows[i]=Vectorz.createSparse(source.getRow(i));
		}
		return new SparseRowMatrix(rows);
	}
	
	public static SparseRowMatrix create(AVector... rows) {
		int rc=rows.length;
		int cc=rows[0].length();
		for (int i=1; i<rc; i++) {
			if (rows[i].length()!=cc) throw new IllegalArgumentException("Mismatched column count at row: "+i);
		}
		return new SparseRowMatrix(rows.clone(),rc,cc);
	}
	
	@Override
	public AVector getRow(int i) {
		AVector v= data.get(i);
		if (v==null) return ZeroVector.create(cols);
		return v;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		for (AVector v:getSlices()) {
			if (!v.isFullyMutable()) return false;
		}
		return true;
	}
	
	@Override
	public double elementSum() {
		double result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSum();
		}
		return result;
	}	
	

	public static SparseRowMatrix create(List<AVector> rows) {
		int rc=rows.size();
		AVector[] rs=new AVector[rc];
		for (int i=0; i<rc; i++) {
			rs[i]=rows.get(i);
		}
		return create(rs);
	}
	
	public static VectorMatrixMN create(Object... vs) {
		return create(Arrays.asList(vs));
	}
		
	@Override 
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof SparseColumnMatrix) {
			return innerProduct((SparseColumnMatrix)a);
		}
		int cc=a.columnCount();
		Matrix r=Matrix.create(rows, cc);
		
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cc; j++) {
				r.unsafeSet(i,j,getRow(i).dotProduct(a.getColumn(j)));
			}
		}
		return r;			
	}
	
	public AMatrix innerProduct(SparseColumnMatrix a) {
		Matrix r=Matrix.create(rows, a.cols);
		for (int i=0; i<rows; i++) {
			for (int j=0; j<a.cols; j++) {
				r.unsafeSet(i,j,getRow(i).dotProduct(a.getColumn(j)));
			}
		}
		return r;
	}

	@Override
	public SparseColumnMatrix getTranspose() {
		return SparseColumnMatrix.wrap(data,cols,rows);
	}
	
	@Override
	public SparseRowMatrix exactClone() {
		SparseRowMatrix result= new SparseRowMatrix(rows,cols);
		for (Entry<Integer,AVector> e:data.entrySet()) {
			AVector row=e.getValue();
			if (!row.isZero()) {
				result.replaceRow(e.getKey(), row.exactClone());
			}
		}
		return result;
	}

	@Override
	public void replaceRow(int i, AVector row) {
		if ((i<0)||(i>=rows)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		if (row.length()!=cols) throw new IllegalArgumentException(ErrorMessages.incompatibleShape(row));
		data.put(i, row);
	}

	@Override
	public double get(int i, int j) {
		if ((i<0)||(i>=rows)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		if ((j<0)||(j>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		return unsafeGet(i,j);
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		AVector row=data.get(i);
		if (row==null) return 0.0;
		return row.unsafeGet(j);
	}

	@Override
	public void set(int row, int column, double value) {
		AVector v=getRow(row);
		if (v.isFullyMutable()) {
			v.set(column,value);
		} else {
			v=v.sparseClone();
			replaceRow(row,v);
			v.set(column,value);
		}
	}

}
