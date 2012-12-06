package mikera.matrixx;

import mikera.matrixx.impl.MatrixSubVector;
import mikera.matrixx.impl.TransposedMatrix;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.transformz.AAffineTransform;
import mikera.transformz.ATransform;
import mikera.transformz.ATranslation;
import mikera.transformz.AffineMN;
import mikera.transformz.Transformz;
import mikera.vectorz.AVector;
import mikera.vectorz.Tools;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ZeroLengthVector;
import mikera.vectorz.util.VectorzException;

/**
 * General abstract matrix class.
 * 
 * Implements generic version of most key matrix operations.
 * 
 * @author Mike
 */
public abstract class AMatrix extends AAffineTransform implements IMatrix {
	// ==============================================
	// Abstract interface

	/**
	 * Returns the number of rows in the matrix
	 */
	public abstract int rowCount();

	/**
	 * Returns the number of columns in the matrix
	 */
	public abstract int columnCount();

	/**
	 * Returns a specified element in the matrix
	 */
	public abstract double get(int row, int column);

	/**
	 * Sets a specified element in the matrix
	 */
	public abstract void set(int row, int column, double value);

	// =============================================
	// Standard implementations

	@Override
	public AAffineTransform toAffineTransform() {
		return new AffineMN(new VectorMatrixMN(this),
				Transformz.identityTransform(outputDimensions()));
	}

	@Override
	public AMatrix getMatrixComponent() {
		return this;
	}

	@Override
	public ATranslation getTranslationComponent() {
		return Transformz.identityTransform(rowCount());
	}

	public boolean isSquare() {
		return rowCount() == columnCount();
	}

	@Override
	public int inputDimensions() {
		return columnCount();
	}

	@Override
	public int outputDimensions() {
		return rowCount();
	}

	@Override
	public void transform(AVector source, AVector dest) {
		int rc = rowCount();
		int cc = columnCount();
		for (int row = 0; row < rc; row++) {
			double total = 0.0;
			for (int column = 0; column < cc; column++) {
				total += get(row, column) * source.get(column);
			}
			dest.set(row, total);
		}
	}
	
	

	@Override
	public void transformInPlace(AVector v) {
		double[] temp = new double[v.length()];
		int rc = rowCount();
		int cc = columnCount();
		if (rc != cc)
			throw new UnsupportedOperationException(
					"Cannot transform in place with a non-square transformation");
		for (int row = 0; row < rc; row++) {
			double total = 0.0;
			for (int column = 0; column < cc; column++) {
				total += get(row, column) * v.get(column);
			}
			temp[row] = total;
		}
		v.setValues(temp);
	}

	@SuppressWarnings("serial")
	private class MatrixRow extends MatrixSubVector {
		private final int row;

		private MatrixRow(int row) {
			this.row = row;
		}

		@Override
		public int length() {
			return columnCount();
		}

		@Override
		public double get(int i) {
			return AMatrix.this.get(row, i);
		}

		@Override
		public void set(int i, double value) {
			AMatrix.this.set(row, i, value);
		}
	}

	@SuppressWarnings("serial")
	private class MatrixColumn extends MatrixSubVector {
		private final int column;

		private MatrixColumn(int column) {
			this.column = column;
		}

		@Override
		public int length() {
			return rowCount();
		}

		@Override
		public double get(int i) {
			return AMatrix.this.get(i, column);
		}

		@Override
		public void set(int i, double value) {
			AMatrix.this.set(i, column, value);
		}
	}

	/**
	 * Returns a row of the matrix as a vector reference
	 */
	public AVector getRow(int row) {
		return new MatrixRow(row);
	}

	/**
	 * Returns a column of the matrix as a vector reference
	 */
	public AVector getColumn(int column) {
		return new MatrixColumn(column);
	}

	public AVector cloneRow(int row) {
		int cc = columnCount();
		AVector v = Vectorz.newVector(cc);
		for (int i = 0; i < cc; i++) {
			v.set(i, get(row, i));
		}
		return v;
	}

	public void set(AMatrix a) {
		int rc = rowCount();
		if (a.rowCount() != rc)
			throw new IllegalArgumentException(
					"Source matrix has wrog number of rows: " + a.rowCount());
		int cc = columnCount();
		if (a.columnCount() != cc)
			throw new IllegalArgumentException(
					"Source matrix has wrong number of columns: "
							+ a.columnCount());
		for (int row = 0; row < rc; row++) {
			for (int column = 0; column < cc; column++) {
				set(row, column, a.get(row, column));
			}
		}
	}

	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public AMatrix clone() {
		return Matrixx.deepCopy(this);
	}

	/**
	 * Calculates the determinant of the matrix.
	 */
	public double determinant() {
		if (!isSquare())
			throw new UnsupportedOperationException(
					"Cannot take determinant of non-square matrix!");

		int rc = rowCount();
		int[] inds = new int[rc];
		for (int i = 0; i < rc; i++) {
			inds[i] = i;
		}
		return calcDeterminant(inds, 0);
	}

	private static void swap(int[] inds, int a, int b) {
		int temp = inds[a];
		inds[a] = inds[b];
		inds[b] = temp;
	}

	private double calcDeterminant(int[] inds, int offset) {
		int rc = rowCount();
		if (offset == (rc - 1))
			return get(offset, inds[offset]);

		double det = get(offset, inds[offset])
				* calcDeterminant(inds, offset + 1);
		for (int i = 1; i < (rc - offset); i++) {
			swap(inds, offset, offset + i);
			det -= get(offset, inds[offset])
					* calcDeterminant(inds, offset + 1);
			swap(inds, offset, offset + i);
		}
		return det;
	}

	public AMatrix toMutableMatrix() {
		return Matrixx.create(this);
	}

	public void transposeInPlace() {
		if (!isSquare())
			throw new Error("Only square matrixes can be transposed in place!");
		int dims = rowCount();
		for (int i = 0; i < dims; i++) {
			for (int j = i + 1; j < dims; j++) {
				double temp = get(i, j);
				set(i, j, get(j, i));
				set(j, i, temp);
			}
		}
	}

	/**
	 * Returns a transposed version of this matrix. May or may not be a reference.
	 * @return
	 */
	public AMatrix getTranspose() {
		return TransposedMatrix.wrap(this);
	}
	
	/**
	 * Adds another matrix to this matrix. Matrices must be the same size.
	 * @param m
	 */
	public void add(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		assert(rc==m.rowCount());
		assert(cc==m.columnCount());

		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				set(i,j,get(i,j)+m.get(i, j));
			}
		}
	}
	
	/**
	 * Multiplies this matrix in-place by another in an entrywise manner (Hadamard product).
	 * @param m
	 */
	public void entrywiseMul(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		assert(rc==m.rowCount());
		assert(cc==m.columnCount());

		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				set(i,j,get(i,j)*m.get(i, j));
			}
		}
	}
	
	/**
	 * "Multiplies" this matrix by another, composing the transformation
	 * @param a
	 */
	public void mul(AMatrix a) {
		this.composeWith(a);
	}
	
	@Override
	public void composeWith(ATransform a) {
		if (a instanceof AMatrix) {
			composeWith((AMatrix)a);
		}
		super.composeWith(a);
	}
	
	public void composeWith(AMatrix a) {
		AMatrix t=compose(a);
		this.set(t);
	}
	
	@Override
	public AMatrix copyOfSquareMatrix() {
		assert(this.isSquare());
		return this.clone();
	}
	
	@Override
	public AVector copyOfTranslationVector() {
		assert(this.isSquare());
		return Vectorz.createZeroVector(this.rowCount());
	}
	
	public void addMultiple(AMatrix m, double factor) {
		int rc=rowCount();
		int cc=columnCount();
		assert(rc==m.rowCount());
		assert(cc==m.columnCount());
		
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				set(i,j,get(i,j)+(m.get(i, j)*factor));
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AMatrix))
			return false;
		return equals((AMatrix) o);
	}

	public boolean equals(AMatrix a) {
		int rc = rowCount();
		if (rc != a.rowCount())
			return false;
		int cc = columnCount();
		if (cc != a.columnCount())
			return false;
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				if (get(i, j) != a.get(i, j))
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if this matrix is approximately equal to 
	 * a second matrix, up to a default tolerance level
	 */
	public boolean epsilonEquals(AMatrix a) {
		int rc = rowCount();
		int cc = columnCount();
		if ((rc != a.rowCount())||(cc!=a.columnCount()))
			throw new VectorzException("Mismatched matrix sizes!");
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				if (!Tools.epsilonEquals(get(i, j), a.get(i, j)))
					return false;
			}
		}
		return true;
	}

	public boolean equals(AAffineTransform a) {

		return a.getTranslationComponent().isIdentity()
				&& this.equals(a.getMatrixComponent());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int rc = rowCount();
		sb.append("[");
		for (int i = 0; i < rc; i++) {
			sb.append(getRow(i).toString());
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		// hashcode is hashcode of all doubles, row by row
		int hashCode = 1;
		int rc = rowCount();
		int cc = columnCount();
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				hashCode = 31 * hashCode + (Tools.hashCode(get(i, j)));
			}
		}
		return hashCode;
	}

	/**
	 * Returns the matrix values as a single reference Vector in the form [row0
	 * row1 row2....]
	 * 
	 * @return
	 */
	public AVector asVector() {
		int rc = rowCount();
		if (rc == 0)
			return ZeroLengthVector.INSTANCE;

		AVector v = getRow(0);
		for (int i = 1; i < rc; i++) {
			v = Vectorz.join(v, getRow(i));
		}
		return v;
	}
	
	@Override
	public ATransform compose(ATransform a) {
		if (!(a instanceof AMatrix)) return super.compose(a);
		return compose((AMatrix)a);
	}
	
	public AMatrix compose(AMatrix a) {
		if ((this.columnCount()!=a.rowCount())) {
			throw new VectorzException("Matrix sizes not compatible!");
		}
		int rc=this.rowCount();
		int cc=a.columnCount();
		int ic=this.columnCount();
		AMatrix result=Matrixx.newMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				double acc=0.0;
				for (int k=0; k<ic; k++) {
					acc+=this.get(i, k)*a.get(k, j);
				}
				result.set(i,j,acc);
			}
		}
		return result;
	}

	@Override
	public AMatrix inverse() {
		AMatrix result = Matrixx.createInverse(this);
		return result;
	}

	/**
	 * Swaps two rows of the matrix in place
	 */
	public void swapRows(int i, int j) {
		if (i == j)
			return;
		AVector a = getRow(i);
		AVector b = getRow(j);
		int cc = columnCount();
		for (int k = 0; k < cc; k++) {
			double t = a.get(k);
			a.set(k, b.get(k));
			b.set(k, t);
		}
	}

	/**
	 * Swaps two columns of the matrix in place
	 */
	public void swapColumns(int i, int j) {
		if (i == j)
			return;
		AVector a = getColumn(i);
		AVector b = getColumn(j);
		int rc = rowCount();
		for (int k = 0; k < rc; k++) {
			double t = a.get(k);
			a.set(k, b.get(k));
			b.set(k, t);
		}
	}

	/**
	 * Converts the matrix to a single flattened vector
	 * in row major order.
	 */
	public AVector toVector() {
		int rc = rowCount();
		int cc = columnCount();
		AVector v = Vectorz.newVector(rc * cc);
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				v.set(i * cc + j, get(i, j));
			}
		}
		return v;
	}
}
