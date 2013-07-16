package mikera.matrixx;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.SliceArray;
import mikera.matrixx.impl.MatrixIterator;
import mikera.matrixx.impl.MatrixSubVector;
import mikera.matrixx.impl.TransposedMatrix;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.randomz.Hash;
import mikera.transformz.AAffineTransform;
import mikera.transformz.ALinearTransform;
import mikera.transformz.ATransform;
import mikera.transformz.AffineMN;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.ArrayVector;
import mikera.vectorz.IOp;
import mikera.vectorz.Op;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.VectorzException;

/**
 * General abstract 2D matrix class.
 * 
 * Implements generic version of most key matrix operations.
 * 
 * @author Mike
 */
public abstract class AMatrix extends ALinearTransform implements IMatrix, Iterable<AVector> {
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
	public final double get(int row) {
		throw new VectorzException("1D get not supported on matrix!");
	}
	
	@Override 
	public final double get() {
		throw new VectorzException("0D get not supported on matrix!");
	}
	
	@Override 
	public void set(int row, double value) {
		throw new VectorzException("1D get not supported on matrix!");
	}
	
	@Override 
	public void set(double value) {
		throw new VectorzException("0D set not supported on matrix!");
	}
	
	@Override 
	public void fill(double value) {
		asVector().fill(value);
	}
	
	@Override
	public void clamp(double min, double max) {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			getRow(i).clamp(min, max);
		}
	}
	
	@Override
	public void pow(double exponent) {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			getRow(i).pow(exponent);
		}
	}
	
	@Override
	public void square() {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			getRow(i).square();
		}
	}
	
	@Override 
	public void set(int[] indexes, double value) {
		if (indexes.length==2) {
			set(indexes[0],indexes[1],value);
		} else {
			throw new VectorzException(""+indexes.length+"D set not supported on AMatrix");
		}
	}
	
	@Override
	public int dimensionality() {
		return 2;
	}
	
	@Override
	public long elementCount() {
		return rowCount()*columnCount();
	}
	
	@Override
	public AVector slice (int rowNumber) {
		return getRow(rowNumber);
	}
	
	@Override
	public INDArray slice(int dimension, int index) {
		if ((dimension<0)||(dimension>=2)) throw new IllegalArgumentException("Dimension out of range!");
		return (dimension==0)?getRow(index):getColumn(index);	
	}	
	
	@Override
	public int sliceCount() {
		return rowCount();
	}
	
	@Override
	public List<AVector> getSlices() {
		ArrayList<AVector> al=new ArrayList<AVector>();
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			al.add(getRow(i));
		}
		return al;
	}
	
	@Override
	public List<INDArray> getSliceViews() {	
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			al.add(getRow(i));
		}
		return al;
	}
	
	@Override
	public int[] getShape() {
		return new int[] {rowCount(),columnCount()};
	}
	
	@Override
	public int getShape(int dim) {
		if (dim==0) {
			return rowCount();
		} else if (dim==1) {
			return columnCount();
		} else {
			throw new IndexOutOfBoundsException("Matrix does not have dimension: "+dim);
		}
	}	
	
	@Override
	public long[] getLongShape() {
		return new long[] {rowCount(),columnCount()};
	}
	
	@Override
	public double get(int... indexes) {
		assert(indexes.length==2);
		return get(indexes[0],indexes[1]);
	}
	
	/**
	 * Returns a new vector that contains the leading diagonal values of the matrix
	 * @return
	 */
	public AVector getLeadingDiagonal() {
		if (!isSquare()) throw new UnsupportedOperationException("Not a square matrix!");
		int dims=rowCount();
		AVector v=Vectorz.newVector(dims);
		for (int i=0; i<dims; i++) {
			v.set(i,this.get(i,i));
		}
		return v;
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return getRow(i).dotProduct(v);
	}
	
	@Override
	public AAffineTransform toAffineTransform() {
		return new AffineMN(new VectorMatrixMN(this),getTranslationComponent());
	}

	@Override
	public AMatrix getMatrixComponent() {
		return this;
	}
	
	@Override
	public boolean isIdentity() {
		int rc=this.rowCount();
		int cc=this.columnCount();
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				double expected=(i==j)?1.0:0.0;
				if (!(this.get(i,j)==expected)) return false;
			}
		}
		return true;
	}

	@Override
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
	public INDArray reshape(int... dimensions) {
		int ndims=dimensions.length;
		if (ndims==1) {
			return toVector().subVector(0, dimensions[0]);
		} else if (ndims==2) {
			return Matrixx.createFromVector(asVector(), dimensions[0], dimensions[1]);
		} else {
			return Arrayz.createFromVector(toVector(), dimensions);
		}
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
		if (v instanceof ArrayVector) {
			transformInPlace((ArrayVector)v);
			return;
		}
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
		v.setElements(temp);
	}
	
	public void transformInPlace(ArrayVector v) {
		double[] temp = new double[v.length()];
		int rc = rowCount();
		int cc = columnCount();
		if (rc != cc)
			throw new UnsupportedOperationException(
					"Cannot transform in place with a non-square transformation");
		double[] data=v.getArray();
		int offset=v.getArrayOffset();
		for (int row = 0; row < rc; row++) {
			double total = 0.0;
			for (int column = 0; column < cc; column++) {
				total += get(row, column) * data[offset+column];
			}
			temp[row] = total;
		}
		v.setElements(temp);
	}


	@SuppressWarnings("serial")
	private class MatrixRowView extends MatrixSubVector {
		private final int row;

		private MatrixRowView(int row) {
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
		
		@Override 
		public boolean isFullyMutable() {
			return AMatrix.this.isFullyMutable();
		}

		
		@Override
		public MatrixRowView exactClone() {
			return AMatrix.this.exactClone().new MatrixRowView(row);
		}
	}

	@SuppressWarnings("serial")
	private class MatrixColumnView extends MatrixSubVector {
		private final int column;

		private MatrixColumnView(int column) {
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
		public boolean isFullyMutable() {
			return AMatrix.this.isFullyMutable();
		}

		@Override
		public void set(int i, double value) {
			AMatrix.this.set(i, column, value);
		}
		
		@Override
		public MatrixColumnView exactClone() {
			return AMatrix.this.exactClone().new MatrixColumnView(column);
		}
	}

	/**
	 * Returns a row of the matrix as a vector view
	 */
	public AVector getRow(int row) {
		return new MatrixRowView(row);
	}

	/**
	 * Returns a column of the matrix as a vector view
	 */
	public AVector getColumn(int column) {
		return new MatrixColumnView(column);
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
	
	public void set(INDArray a) {
		if (a instanceof AMatrix) {set((AMatrix) a); return;}	
		if (a instanceof AVector) {for (AVector r:this) {r.set((AVector) a);} return;}
		if (a instanceof AScalar) {set(a.get()); return;}
		
		throw new UnsupportedOperationException("Can't set matrix to array: "+a.getClass() +" with shape: "+a.getShape());
	}
	
	public void set(Object o) {
		if (o instanceof INDArray) {set((INDArray)o); return;}
		if (o instanceof Number) {
			set(((Number)o).doubleValue()); return;
		}
		throw new UnsupportedOperationException("Can't set to value for "+o.getClass().toString());		
	}
	
	@Override
	public void setElements(double[] values, int offset, int length) {
		if (length!=elementCount()) {
			throw new IllegalArgumentException("Incorrect element count: "+length);
		}
		int rc = rowCount();
		int cc = columnCount();
		int di=offset;
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				set(i,j,values[di++]);
			}
		}	
	} 
	
	@Override
	public void getElements(double[] dest, int offset) {
		asVector().getElements(dest, offset);
	}
	
	@Override
	public void copyTo(double[] arr) {
		getElements(arr,0);
	}
	
	@Override
	public void setElements(double[] values) {
		setElements(values,0,values.length);
	}

	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return isFullyMutable();
	}
	
	@Override
	public boolean isElementConstrained() {
		return false;
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

	/**
	 * Creates a fully mutable deep copy of this matrix
	 * @return A new matrix
	 */
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
	 * Returns a transposed version of this matrix. May or may not be a view.
	 * @return
	 */
	@Override
	public AMatrix getTranspose() {
		return TransposedMatrix.wrap(this);
	}
	
	@Override
	public AMatrix getTransposeView() {
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
	
	public void add(AVector v) {
		int rc=rowCount();
		int cc=columnCount();
		assert(cc==v.length());

		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				set(i,j,get(i,j)+v.get(j));
			}
		}		
	}
	
	public void sub(AVector v) {
		int rc=rowCount();
		int cc=columnCount();
		assert(cc==v.length());

		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				addAt(i,j,-v.get(j));
			}
		}		
	}
	
	@Override
	public void sub(double d) {
		add(-d);
	}
	
	/**
	 * Scales a matrix by a constant scalar factor.
	 * @param m
	 */
	public final void scale(double factor) {
		multiply(factor);
	}
	
	@Override
	public final void scaleAdd(double factor, double constant) {
		multiply(factor);
		add(constant);
	}
	
	public void multiply(double factor) {
		int rc=rowCount();
		int cc=columnCount();

		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				set(i,j,get(i,j)*factor);
			}
		}
	}	

	/**
	 * Returns the sum of all elements in the matrix
	 * @param m
	 * @return 
	 */
	public double elementSum() {
		int rc=rowCount();
		int cc=columnCount();
		
		double result=0.0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				result+=get(i,j);
			}
		}
		return result;
	}
	
	/**
	 * Returns the squared sum of all elements in the matrix
	 * @param m
	 * @return 
	 */
	public double elementSquaredSum() {
		int rc=rowCount();
		int cc=columnCount();
		
		double result=0.0;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				double value=get(i,j);
				result+=value*value;
			}
		}
		return result;
	}
	
	@Override
	public long nonZeroCount() {
		long result=0;
		int rc=rowCount();
		int cc=columnCount();
		
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				if (get(i,j)!=0.0) result++;
			}
		}
		return result;	
	}
	
	/**
	 * Subtracts another matrix from this one
	 * @param m
	 */
	public void sub(AMatrix m) {
		addMultiple(m,-1.0);
	}
	
	@Override
	public void negate() {
		multiply(-1.0);
	}
	
	@Override
	public void reciprocal() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRow(i).reciprocal();
		}
	}
	
	@Override
	public void abs() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRow(i).abs();
		}
	}
	
	@Override
	public void sqrt() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRow(i).sqrt();
		}
	}
	
	@Override
	public void log() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRow(i).log();
		}
	}
	
	@Override
	public void exp() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRow(i).exp();
		}
	}
	
	@Override
	public void signum() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRow(i).signum();
		}
	}
	
	/**
	 * Multiplies this matrix in-place by another in an entrywise manner (Hadamard product).
	 * @param m
	 */
	public void elementMul(AMatrix m) {
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
	
	public void multiplyRow(int i, double factor) {
		getRow(i).multiply(factor);
	}
	
	public void addRowMultiple(int src, int dst, double factor) {
		getRow(dst).addMultiple(getRow(src), factor);
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
	public boolean isView() {
		return false;
	}
	
	@Override
	public AMatrix copyOfMatrix() {
		return this.clone();
	}
	
	@Override
	public AVector copyOfTranslationVector() {
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
	
	/**
	 * Returns an iterator over rows in this Matrix
	 */
	public Iterator<AVector> iterator() {
		return new MatrixIterator(this);
	}
	
	@Override
	public boolean epsilonEquals(INDArray a) {
		return epsilonEquals(a,Vectorz.TEST_EPSILON);
	}
	
	@Override
	public boolean epsilonEquals(INDArray a, double epsilon) {
		if (a.dimensionality()!=2) {
			return false;
		} else {
			int sc=rowCount();
			if (a.sliceCount()!=sc) return false;
			for (int i=0; i<sc; i++) {
				INDArray s=getRow(i);
				if (!s.epsilonEquals(a.slice(i),epsilon)) return false;
			}			
			return true;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AMatrix) return equals((AMatrix) o);
		if (o instanceof INDArray) return equals((INDArray) o);
		return false;
		
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
	
	@Override
	public boolean equals(INDArray v) {
		if (v instanceof AMatrix) return equals((AMatrix) v);
		if (v.dimensionality()!=2) return false;
		int[] vs=v.getShape();
		int rc=rowCount();
		if (rc != vs[0]) return false;
		int cc=columnCount();
		if (cc != vs[1]) return false;
		
		int[] ind = new int[2];
		for (int i = 0; i < rc; i++) {
			ind[0]=i;
			for (int j=0; j<cc; j++) {
				ind[1]=j;
				if (get(i,j) != v.get(ind)) return false;
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
			if (i>0) sb.append(',');
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
				hashCode = 31 * hashCode + (Hash.hashCode(get(i, j)));
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
	@Override
	public AVector asVector() {
		int rc = rowCount();
		if (rc == 0) {
			return Vector0.INSTANCE;
		}

		AVector v = getRow(0);
		for (int i = 1; i < rc; i++) {
			v = Vectorz.join(v, getRow(i));
		}
		return v;
	}
	
	@Override
	public List<Double> asElementList() {
		return asVector().asElementList();
	}
	
	@Override
	public ATransform compose(ATransform a) {
		if (!(a instanceof AMatrix)) return super.compose(a);
		return compose((AMatrix)a);
	}
	
	/**
	 * Composes this matrix with another matrix (matrix multiplication)
	 * Returns a new matrix that represents the compose transformation.
	 * @param a
	 * @return
	 */
	public final AMatrix compose(AMatrix a) {
		return innerProduct(a);
	}
	
	public AMatrix innerProduct(AMatrix a) {
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
	
	public AVector innerProduct(AVector v) {
		return transform(v);
	}
	
	public AMatrix innerProduct(AScalar s) {
		AMatrix r= clone();
		r.scale(s.get());
		return r;
	}
	
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AVector) {
			return innerProduct((AVector)a);
		} else if (a instanceof AMatrix) {
			return compose((AMatrix) a);
		} else if (a instanceof AScalar) {
			return innerProduct((AScalar)a);
		}
		throw new UnsupportedOperationException("Can't take inner product with: "+a.getClass());
	}

	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (Object s:this) {
			if (s instanceof INDArray) {
				al.add(((INDArray)s).outerProduct(a));
			} else {
				double x=Tools.toDouble(s);
				INDArray sa=a.clone();
				sa.scale(x);
				al.add(sa);
			}
		}
		return Arrayz.create(al);
	}

	@Override
	public AMatrix inverse() {
		AMatrix result = Matrixx.createInverse(this);
		return result;
	}
	
	public double trace() {
		int rc=rowCount();
		assert(rc==columnCount());
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=get(i,i);
		}
		return result;
	}
	
	@Override
	public boolean isInvertible() {
		return isSquare()&&(determinant()!=0.0);
	}


	/**
	 * Converts the matrix to a single flattened vector
	 * in row major order.
	 */
	public Vector toVector() {
		int rc = rowCount();
		int cc = columnCount();
		Vector v = Vector.createLength(rc * cc);
		this.getElements(v.data,0);
		return v;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		int n=rowCount();
		for (int i=0; i<n; i++) {
			getRow(i).toDoubleBuffer(dest);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		int rc = rowCount();
		int cc = columnCount();
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				set(i,j,op.apply(get(i,j)));
			}
		}
	}
	
	@Override
	public void applyOp(IOp op) {
		if (op instanceof Op) {applyOp((Op)op); return;}
		int rc = rowCount();
		int cc = columnCount();
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				set(i,j,op.apply(get(i,j)));
			}
		}
	}
	
	public void add(INDArray a) {
		if (a instanceof AMatrix) {
			add((AMatrix)a);
		} else if (a instanceof AVector) {
			add((AVector)a);
		} else if (a instanceof AScalar) {
			add(a.get());
		} else {
			int dims=a.dimensionality();
			int rc=rowCount();
			if (dims==0) {
				add(a.get());
			} else if (dims==1) {
				for (int i=0; i<rc; i++) {
					slice(i).add(a);
				}
			} else if (dims==2) {
				for (int i=0; i<rc; i++) {
					slice(i).add(a.slice(i));
				}		
			}
		}
	}
	
	@Override
	public void multiply(INDArray a) {
		if (a instanceof AMatrix) {
			elementMul((AMatrix)a);
		} else if (a instanceof AScalar) {
			multiply(a.get());
		} else {
			int dims=a.dimensionality();
			int rc=rowCount();
			if (dims==0) {
				multiply(a.get());
			} else if (dims==1) {
				for (int i=0; i<rc; i++) {
					slice(i).multiply(a);
				}
			} else if (dims==2) {
				for (int i=0; i<rc; i++) {
					slice(i).multiply(a.slice(i));
				}		
			} else {
				throw new VectorzException("Can't multiply matrix with array of dimensionality: "+dims);
			}
		}
	}
	
	public void sub(INDArray a) {
		if (a instanceof AMatrix) {
			sub((AMatrix)a);
		} else if (a instanceof AVector) {
			sub((AVector)a);
		} else if (a instanceof AScalar) {
			sub(a.get());
		} else {
			int dims=a.dimensionality();
			int rc=rowCount();
			if (dims==0) {
				sub(a.get());
			} else if (dims==1) {
				for (int i=0; i<rc; i++) {
					slice(i).sub(a);
				}
			} else if (dims==2) {
				for (int i=0; i<rc; i++) {
					slice(i).sub(a.slice(i));
				}		
			}
		}
	}

	@Override
	public void add(double d) {
		int rc = rowCount();
		int cc = columnCount();
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				addAt(i,j,d);
			}
		}
	}

	public void addAt(int i, int j, double d) {
		set(i,j,get(i,j)+d);
	}
	
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims<2) {
			throw new VectorzException("Can't broadcast to a smaller shape!");
		} else if (2==tdims) {
			return this;
		} else {
			int n=targetShape[0];
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,n);
		}
	}

	/**
	 * Returns true if the matrix is the zero matrix (all components zero)
	 */
	public boolean isZeroMatrix() {
		int rc = rowCount();
		int cc = columnCount();
		for (int i = 0; i < rc; i++) {
			for (int j = 0; j < cc; j++) {
				if (get(i,j)!=0.0) return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is positive definite
	 */
	public void isPositiveDefinite() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns true if a matrix is diagonal
	 */
	public boolean isDiagonal() {
		int rc=rowCount();
		int cc=columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				if ((i!=j)&&(get(i,j)!=0.0)) return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is symmetric
	 */
	public boolean isSymmetric() {
		int rc=rowCount();
		int cc=columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			for (int j=i+1; j<cc; j++) {
				if (get(i,j)!=get(j,i)) return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is Hermitian
	 * 
	 * This is equivalent to isSymmetric(), since all Vectorz matrices have real values.
	 */
	public final boolean isHermitian() {
		return isSymmetric();
	}
	
	/**
	 * Returns true if a matrix is upper triangular
	 */
	public boolean isUpperTriangular() {
		int rc=rowCount();
		int cc=columnCount();
		for (int j=0; j<cc; j++) {
			for (int i=j+1; i<rc; i++) {
				if (get(i,j)!=0.0) return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is lower triangular
	 */
	public boolean isLowerTriangular() {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			for (int j=i+1; j<cc; j++) {
				if (get(i,j)!=0.0) return false;
			}
		}
		return true;
	}

	public abstract AMatrix exactClone();
	
	@Override 
	public void validate() {
		// TODO: any generic validation?
	}
}
