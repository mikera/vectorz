package mikera.matrixx.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SparseIndexedVector;
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
public class SparseColumnMatrix extends ASparseRCMatrix implements ISparse, IFastColumns {
	private static final long serialVersionUID = -5994473197711276621L;

	private static final long SPARSE_ELEMENT_THRESHOLD = 1000L;
	
	private final AVector emptyColumn;

	protected SparseColumnMatrix(int rowCount, int columnCount) {
		this(new HashMap<Integer,AVector>(),rowCount,columnCount);
	}
	
	protected SparseColumnMatrix(HashMap<Integer,AVector> data, int rowCount, int columnCount) {
		super(rowCount,columnCount,data);
		emptyColumn=Vectorz.createZeroVector(rowCount);
	}

	protected SparseColumnMatrix(AVector[] columns, int rowCount, int columnCount) {
		this(new HashMap<Integer,AVector>(),rowCount,columnCount);
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
	
	public static SparseColumnMatrix create(List<AVector> columns) {
		int cc = columns.size();
		AVector[] rs = new AVector[cc];
		for (int i = 0; i < cc; i++) {
			rs[i] = columns.get(i);
		}
		return create(rs);
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
	protected int lineCount() {
		return cols;
	}

	@Override
	protected int lineLength() {
		return rows;
	}

	@Override
	public double get(int i, int j) {
		return getColumn(j).get(i);
	}

	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		Integer io=j;
		AVector v=data.get(io);
		if (v==null) {
			if (value==0.0) return;
			v=Vectorz.createSparseMutable(rows);
		} else if (v.isFullyMutable()) {
			v.set(i,value);
			return;
		} else {
			v=v.sparseClone();			
		}
		data.put(io,v);
		v.set(i,value);
	}
	
	
	@Override
	public double unsafeGet(int row, int column) {
		return getColumn(column).unsafeGet(row);
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
	public void addToArray(double[] data, int offset) {
		for (Entry<Integer, AVector> e : this.data.entrySet()) {
			AVector v = e.getValue();
			v.addToArray(data, offset+e.getKey(),cols);
		}
	}
	
	
	private AVector ensureMutableColumn(int i) {
		AVector v = data.get(i);
		if (v ==null) {
			AVector nv=SparseIndexedVector.createLength(rows);
			return nv;
		}
		if (v.isFullyMutable()) return v;
		AVector mv=v.mutable();
		data.put(i, mv);
		return mv;
	}
	
	@Override
	public AVector getColumn(int i) {
		if ((i<0)||(i>=cols)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, i));
		AVector v= data.get(i);
		if (v==null) return emptyColumn;
		return v;
	}
	
	@Override
	public AVector getColumnView(int i) {
		return ensureMutableColumn(i);
	}
	
	@Override
	public boolean isLowerTriangular() {
		int cc=columnCount();
		for (int i=1; i<cc; i++) {
			if (!getColumn(i).isRangeZero(0, i)) return false;
		}
		return true;
	}
	
	@Override
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
	public void copyRowTo(int row, double[] data, int offset) {
		Arrays.fill(data, offset,offset+ cols,0.0);
		for (Entry<Integer,AVector> e:this.data.entrySet()) {
			data[offset+e.getKey()]=e.getValue().unsafeGet(row);
		}		
	}
	
	@Override
	public void fill(double value) {
		if (value==0.0) {
			data.clear();
		} else {
			RepeatedElementVector v=RepeatedElementVector.create(rows, value);
			for (int i=0; i<cols; i++) {
				data.put(i, v);
			}
		}
	}

	@Override
	public SparseRowMatrix getTransposeView() {
		return SparseRowMatrix.wrap(data,cols,rows);
	}
	
	@Override
	public AMatrix multiplyCopy(double a) {
		HashMap<Integer,AVector> ndata=new HashMap<Integer,AVector>();
		for (Entry<Integer, AVector> eCol : data.entrySet()) {
			ndata.put(eCol.getKey(), eCol.getValue().innerProduct(a));
		}
		return wrap(ndata,rows,cols);
	}
	
	@Override
	public AVector innerProduct(AVector a) {
		return transform(a);
	}
	
	@Override
	public AVector transform(AVector a) {
		Vector r=Vector.createLength(rows);
		for (int i=0; i<cols; i++) {
			getColumn(i).addMultipleToArray(a.get(i), 0, r.getArray(), 0, rows);
		}
		return r;
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
	public double[] toDoubleArray() {
		Matrix m=Matrix.create(rows, cols);
		for (int i=0; i<cols; i++) {
			AVector v=data.get(i);
			if (v!=null) m.getColumn(i).set(v);
		}
		return m.getArray();
	}
	
	@Override 
	public AMatrix transposeInnerProduct(AMatrix a) {
		return getTranspose().innerProduct(a);	
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
	public AMatrix clone() {
		if (this.elementCount()<SPARSE_ELEMENT_THRESHOLD) return super.clone();
		return exactClone();
	}
	
	@Override
	public SparseColumnMatrix sparse() {
		return this;
	}
	
	@Override
	public void validate() {
		super.validate();
		for (int i=0; i<cols; i++) {
			if (getColumn(i).length()!=rows) throw new VectorzException("Invalid row count at column: "+i);
		}
	}
	
	@Override
	public boolean equals(AMatrix m) {
		if (m instanceof SparseColumnMatrix) {
			return equals((SparseColumnMatrix)m);
		}
		if (!isSameShape(m)) return false;
		for (int i=0; i<cols; i++) {
			AVector v=data.get(i);
			if (v==null) {
				if (!m.getColumn(i).isZero()) return false;
			} else {
				if (!v.equals(m.getColumn(i))) return false;
			}
			
		}
		return true;
	}
	
	public boolean equals(SparseColumnMatrix m) {
		if (m==this) return true;
		if (!isSameShape(m)) return false;
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
