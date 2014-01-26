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
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

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
		int cc = rows[0].length();
		for (int i = 1; i < rc; i++) {
			if (rows[i].length() != cc)
				throw new IllegalArgumentException(
						"Mismatched column count at row: " + i);
		}
		return new SparseRowMatrix(rows.clone(), rc, cc);
	}

	@Override
	public AVector getRow(int i) {
		AVector v = data.get(i);
		if (v == null) return emptyRow;
		return v;
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
		int cc = a.columnCount();
		Matrix r = Matrix.create(rows, cc);

		for (Entry<Integer, AVector> eRow : data.entrySet()) {
			int i = eRow.getKey();
			AVector row = eRow.getValue();
			for (int j = 0; j < cc; j++) {
				r.unsafeSet(i, j, row.dotProduct(a.getColumn(j)));
			}
		}
		return r;
	}

	public AMatrix innerProduct(SparseColumnMatrix a) {
		AMatrix r = Matrixx.createSparse(rows, a.cols);

		for (Entry<Integer, AVector> eRow : data.entrySet()) {
			int i = eRow.getKey();
			AVector row = eRow.getValue();
			for (Entry<Integer, AVector> eCol : data.entrySet()) {
				int j = eCol.getKey();
				AVector acol = eCol.getValue();
				r.unsafeSet(i, j, row.dotProduct(acol));
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
		if ((i < 0) || (i >= rows))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(
					this, i, j));
		if ((j < 0) || (j >= cols))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(
					this, i, j));
		return unsafeGet(i, j);
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
		if ((i < 0) || (i >= rows))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(
					this, i, j));
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
