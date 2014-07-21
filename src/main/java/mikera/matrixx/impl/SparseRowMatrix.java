package mikera.matrixx.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix stored as a sparse collection of sparse row vectors.
 * 
 * This format is especially efficient for: - innerProduct() with another
 * matrix, especially one with efficient column access like SparseColumnMatrix -
 * access via getRow() operation - transpose into SparseColumnMatrix
 * 
 * @author Mike
 * 
 */
public class SparseRowMatrix extends ASparseRCMatrix implements ISparse,
		IFastRows {
	private static final long serialVersionUID = 8646257152425415773L;

	private static final long SPARSE_ELEMENT_THRESHOLD = 1000L;
	
	private final AVector emptyRow;

	protected SparseRowMatrix(AVector... vectors) {
		this(vectors,vectors.length, vectors[0].length());
	}

	protected SparseRowMatrix(AVector[] vectors, int rowCount, int columnCount) {
		this(new HashMap<Integer, AVector>(),rowCount, columnCount);
		for (int i = 0; i < rows; i++) {
			AVector v = vectors[i];
			if ((v != null) && (!v.isZero()))
				data.put(i, vectors[i]);
		}
	}

	protected SparseRowMatrix(int rowCount, int columnCount) {
		this(new HashMap<Integer, AVector>(),rowCount, columnCount);
	}

	protected SparseRowMatrix(HashMap<Integer, AVector> data, int rowCount,
			int columnCount) {
		super(rowCount, columnCount, data);
		emptyRow=Vectorz.createZeroVector(columnCount);
	}

	public static SparseRowMatrix wrap(HashMap<Integer, AVector> data,
			int rows, int cols) {
		return new SparseRowMatrix(data, rows, cols);
	}

	public static SparseRowMatrix create(int rows, int cols) {
		return new SparseRowMatrix(rows, cols);
	}

	public static SparseRowMatrix create(AMatrix source) {
		int rc = source.rowCount();
		int cc = source.columnCount();
		SparseRowMatrix m = new SparseRowMatrix(rc, cc);
		for (int i = 0; i < rc; i++) {
			AVector row = source.getRow(i);
			if (!row.isZero())
				m.replaceRow(i, Vectorz.createSparse(row));
		}
		return m;
	}

	public static SparseRowMatrix create(AVector... rows) {
		int rc = rows.length;
		int cc = -1;
		for (int i = 0; i < rc; i++) {
			AVector r=rows[i];
			if (r==null) continue;
			if (cc<0) {
				cc=r.length();
			} else {
				if (r.length() != cc)
					throw new IllegalArgumentException(
							"Mismatched column count at row: " + i);
			}
		}
		if (cc==-1) {throw new IllegalArgumentException("All rows are null!");}
		return new SparseRowMatrix(rows.clone(), rc, cc);
	}
	
	private AVector ensureMutableRow(int i) {
		AVector v = data.get(i);
		if (v ==null) {
			AVector nv=SparseIndexedVector.createLength(cols);
			return nv;
		}
		if (v.isFullyMutable()) return v;
		AVector mv=v.mutable();
		data.put(i, mv);
		return mv;
	}

	@Override
	public AVector getRow(int i) {
		AVector v = data.get(i);
		if (v == null) {
			if ((i<0)||(i>=rows)) throw new IndexOutOfBoundsException("Row: " + i);
			return emptyRow;
		}
		return v;
	}
	
	@Override
	public AVector getRowView(int i) {
		return ensureMutableRow(i);
	}
	
	@Override
	public boolean isUpperTriangular() {
		int rc=rowCount();
		for (int i=1; i<rc; i++) {
			if (!getRow(i).isRangeZero(0, i)) return false;
		}
		return true;
	}

	@Override
	public void swapRows(int i, int j) {
		if (i == j)
			return;
		if ((i < 0) || (i >= rows))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(
					this, 0, i));
		if ((j < 0) || (j >= rows))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(
					this, 0, j));
		AVector a = data.get(i);
		AVector b = data.get(j);
		data.put(i, b);
		data.put(j, a);
	}

	@Override
	public void pow(double exponent) {
		for (Entry<Integer, AVector> e : data.entrySet()) {
			AVector v = e.getValue();
			if (!v.isFullyMutable()) {
				v = v.sparseClone();
				data.put(e.getKey(), v);
			}
			v.pow(exponent);
		}
	}

	@Override
	public void square() {
		for (Entry<Integer, AVector> e : data.entrySet()) {
			AVector v = e.getValue();
			if (!v.isFullyMutable()) {
				v = v.sparseClone();
				data.put(e.getKey(), v);
			}
			v.square();
		}
	}

	@Override
	public void exp() {
		AVector rr = RepeatedElementVector.create(lineLength(), 1.0);
		for (int i = 0; i < lineCount(); i++) {
			Integer io = i;
			AVector v = data.get(io);
			if (v == null) {
				data.put(io, rr);
			} else {
				if (!v.isFullyMutable()) {
					v = v.sparseClone();
					data.put(io, v);
				}
				v.exp();
			}
		}
	}
	
	@Override
	public void add(AMatrix a) {
		if (a instanceof SparseRowMatrix) {add((SparseRowMatrix)a); return;}
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			AVector cr=data.get(i);
			AVector ar=a.getRow(i);
			if (cr==null) {
				if (!ar.isZero()) {
					data.put(i,ar.copy());
				}
			} else if (cr.isMutable()) {
				cr.add(ar);
			} else {
				data.put(i,cr.addCopy(ar));
			}
		}
	}
	
	public void add(SparseRowMatrix a) {
		for (Map.Entry<Integer,AVector> e: a.data.entrySet()) {
			Integer i = e.getKey();
			AVector cr=data.get(i);
			AVector ar=e.getValue();
			if (cr==null) {
				if (!ar.isZero()) {
					data.put(i,ar.copy());
				}
			} else if (cr.isMutable()) {
				cr.add(ar);
			} else {
				data.put(i,cr.addCopy(ar));
			}
		}
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		for (Entry<Integer, AVector> e : this.data.entrySet()) {
			AVector v = e.getValue();
			v.addToArray(data, offset+cols*e.getKey());
		}
	}

	@Override
	public void applyOp(Op op) {
		boolean stoch = op.isStochastic();
		AVector rr = (stoch) ? null : RepeatedElementVector.create(
				lineLength(), op.apply(0.0));

		for (int i = 0; i < lineCount(); i++) {
			Integer io = i;
			AVector v = data.get(io);
			if (v == null) {
				if (!stoch) {
					data.put(io, rr);
					continue;
				}
				v = Vector.createLength(lineLength());
				data.put(io, v);
			} else if (!v.isFullyMutable()) {
				v = v.sparseClone();
				data.put(io, v);
			}
			v.applyOp(op);
		}
	}

	public static SparseRowMatrix create(List<AVector> rows) {
		int rc = rows.size();
		AVector[] rs = new AVector[rc];
		for (int i = 0; i < rc; i++) {
			rs[i] = rows.get(i);
		}
		return create(rs);
	}

	@Override
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof SparseColumnMatrix) {
			return innerProduct((SparseColumnMatrix) a);
		}
		AMatrix r = Matrix.create(rows, a.columnCount());

		for (Entry<Integer, AVector> eRow : data.entrySet()) {
			int i = eRow.getKey();
			r.setRow(i,getRow(i).innerProduct(a));
		}
		return r;
	}
	
	@Override
	public AVector innerProduct(AVector a) {
		return transform(a);
	}
	
	@Override
	public AVector transform(AVector a) {
		AVector r=Vector.createLength(rows);
		for (int i=0; i<rows; i++) {
			r.set(i,getRow(i).dotProduct(a));
		}
		return r;
	}
	
	@Override
	public AMatrix multiplyCopy(double a) {
		HashMap<Integer,AVector> ndata=new HashMap<Integer,AVector>();
		for (Entry<Integer, AVector> eRow : data.entrySet()) {
			ndata.put(eRow.getKey(), eRow.getValue().innerProduct(a));
		}
		return wrap(ndata,rows,cols);
	}

	public AMatrix innerProduct(SparseColumnMatrix a) {
		AMatrix r = Matrixx.createSparse(rows, a.cols);

		for (Entry<Integer, AVector> eRow : data.entrySet()) {
			int i = eRow.getKey();
			AVector row = eRow.getValue();
			for (Entry<Integer, AVector> eCol : data.entrySet()) {
				int j = eCol.getKey();
				AVector acol = eCol.getValue();
				double v= row.dotProduct(acol);
				if (v!=0.0) r.unsafeSet(i, j, v);
			}
		}
		return r;
	}

	@Override
	public SparseColumnMatrix getTransposeView() {
		return SparseColumnMatrix.wrap(data, cols, rows);
	}

	@Override
	protected int lineCount() {
		return rows;
	}

	@Override
	protected int lineLength() {
		return cols;
	}

	@Override
	public void replaceRow(int i, AVector row) {
		if ((i < 0) || (i >= rows))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(
					this, i));
		if (row.length() != cols)
			throw new IllegalArgumentException(
					ErrorMessages.incompatibleShape(row));
		data.put(i, row);
	}

	@Override
	public double get(int i, int j) {
		return getRow(i).get(j);
	}

	@Override
	public double unsafeGet(int i, int j) {
		AVector row = data.get(i);
		if (row == null)
			return 0.0;
		return row.unsafeGet(j);
	}

	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		Integer io = i;
		AVector v = data.get(io);
		if (v == null) {
			if (value == 0.0)
				return;
			v = Vectorz.createSparseMutable(cols);
		} else if (v.isFullyMutable()) {
			v.set(j, value);
			return;
		} else {
			v = v.sparseClone();
		}
		data.put(io, v);
		v.set(j, value);
	}

	@Override
	public SparseRowMatrix exactClone() {
		SparseRowMatrix result = new SparseRowMatrix(rows, cols);
		for (Entry<Integer, AVector> e : data.entrySet()) {
			AVector row = e.getValue();
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
		if (this.elementCount() < SPARSE_ELEMENT_THRESHOLD)
			return super.clone();
		return exactClone();
	}

	@Override
	public boolean equals(AMatrix m) {
		if (m instanceof SparseRowMatrix) {
			return equals((SparseRowMatrix) m);
		}
		return equalsByRows(m);
	}
	
	@Override
	protected boolean equalsByRows(AMatrix m) {
		int rc = rowCount();
		for (int i=0; i<rc; i++) {
			AVector row=data.get(i);
			if (row==null) {
				if (!m.getRow(i).isZero()) return false;
			} else {
				if (!row.equals(m.getRow(i))) return false;				
			}
		}
		return true;
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] ds=new double[rows*cols];
		// we use adding to array, since rows themselves are likely to be sparse
		for (Entry<Integer, AVector> e : data.entrySet()) {
			e.getValue().addToArray(ds, e.getKey()*cols);
		}
		return ds;
	}
	
	@Override
	public void copyRowTo(int i, double[] data, int offset) {
		AVector v=this.data.get(i);
		if (v!=null) {
			v.getElements(data, offset);
		} else {
			Arrays.fill(data, offset, offset+cols, 0.0);			
		}
	}
	
	@Override
	public void fill(double value) {
		if (value==0.0) {
			data.clear();
		} else {
			RepeatedElementVector v=RepeatedElementVector.create(cols, value);
			for (int i=0; i<rows; i++) {
				data.put(i, v);
			}
		}
	}
	
	@Override
	public void copyColumnTo(int col, double[] data, int offset) {
		Arrays.fill(data, offset, offset+rows, 0.0);
		for (Entry<Integer,AVector> e:this.data.entrySet()) {
			data[offset+e.getKey()]=e.getValue().unsafeGet(col);
		}		
	}

	public boolean equals(SparseRowMatrix m) {
		if (m == this)
			return true;
		if ((this.rows != m.rows) || (this.cols != m.cols))
			return false;
		HashSet<Integer> checked = new HashSet<Integer>();

		for (Entry<Integer, AVector> e : data.entrySet()) {
			Integer i = e.getKey();
			AVector v = e.getValue();
			AVector ov = m.data.get(i);
			if (ov == null) {
				if (!v.isZero())
					return false;
			} else {
				if (!v.equals(ov))
					return false;
				checked.add(i);
			}
		}

		// check remaining rows from m, these must be zero for equality to hold
		for (Entry<Integer, AVector> e : m.data.entrySet()) {
			Integer i = e.getKey();
			if (checked.contains(i))
				continue; // already checked
			AVector v = e.getValue();
			if (!v.isZero())
				return false;
		}
		return true;
	}

	@Override
	public void validate() {
		super.validate();
	}

}
