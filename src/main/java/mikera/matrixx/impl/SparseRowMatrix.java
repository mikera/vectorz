package mikera.matrixx.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
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

	private static final long SPARSE_ELEMENT_THRESHOLD = 1000L;

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
		double result=0.0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSum();
		}
		return result;
	}	
	
	@Override
	public double elementSquaredSum() {
		double result=0.0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSquaredSum();
		}
		return result;
	}	
	
	@Override
	public double elementMin() {
		if (data.size()==0) return 0.0;
		double result=Double.MAX_VALUE;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			double v=e.getValue().elementMin();
			if (v<result) result=v;
		}
		if ((result>0)&&(data.size()<rowCount())) return 0.0;
		return result;
	}	
	
	@Override
	public double elementMax() {
		if (data.size()==0) return 0.0;
		double result=-Double.MAX_VALUE;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			double v=e.getValue().elementMax();
			if (v>result) result=v;
		}
		if ((result<0)&&(data.size()<rowCount())) return 0.0;
		return result;
	}	
	
	@Override
	public long nonZeroCount() {
		long result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().nonZeroCount();
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
		AMatrix r=Matrixx.createSparse(rows, a.cols);
		
		for (Entry<Integer,AVector> eRow:data.entrySet()) {
			int i=eRow.getKey();
			AVector row=eRow.getValue();
			for (Entry<Integer,AVector> eCol:data.entrySet()) {
				int j=eCol.getKey();
				AVector acol=eCol.getValue();
				r.unsafeSet(i,j,row.dotProduct(acol));
			}
		}
		return r;
	}

	@Override
	public SparseColumnMatrix getTranspose() {
		return SparseColumnMatrix.wrap(data,cols,rows);
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
	public AMatrix sparse() {
		return this;
	}
	
	@Override
	public AMatrix clone() {
		if (this.elementCount()<SPARSE_ELEMENT_THRESHOLD) return super.clone();
		return exactClone();
	}
	
	@Override
	public boolean equals(AMatrix m) {
		if (m instanceof SparseRowMatrix) {
			return equals((SparseRowMatrix)m);
		}
		return super.equals(m);
	}
	
	public boolean equals(SparseRowMatrix m) {
		if (m==this) return true;
		if ((this.rows!=m.rows)||(this.cols!=m.cols)) return false;
		HashSet<Integer> checked=new HashSet<Integer>();
		
		for (Entry<Integer,AVector> e:data.entrySet()) {
			Integer i=e.getKey();
			AVector v=e.getValue();
			AVector ov=m.data.get(i);
			if (ov==null) {
				if (!v.isZero()) return false;
			} else {
				if (!v.equals(ov)) return false;
				checked.add(i);
			}
		}
		
		// check remaining rows from m, these must be zero for equality to hold
		for (Entry<Integer,AVector> e:m.data.entrySet()) {
			Integer i=e.getKey();
			if (checked.contains(i)) continue; // already checked
			AVector v=e.getValue();
			if (!v.isZero()) return false;
		}
		return true;
	}

}
