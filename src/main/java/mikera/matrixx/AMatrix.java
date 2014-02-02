package mikera.matrixx;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Array;
import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.ISparse;
import mikera.arrayz.impl.AbstractArray;
import mikera.arrayz.impl.IDense;
import mikera.arrayz.impl.JoinedArray;
import mikera.arrayz.impl.SliceArray;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.impl.AArrayMatrix;
import mikera.matrixx.impl.IFastRows;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ImmutableMatrix;
import mikera.matrixx.impl.MatrixBandView;
import mikera.matrixx.impl.MatrixColumnView;
import mikera.matrixx.impl.MatrixElementIterator;
import mikera.matrixx.impl.MatrixIterator;
import mikera.matrixx.impl.MatrixRowView;
import mikera.matrixx.impl.MatrixAsVector;
import mikera.matrixx.impl.TransposedMatrix;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.randomz.Hash;
import mikera.transformz.AAffineTransform;
import mikera.transformz.AffineMN;
import mikera.transformz.impl.IdentityTranslation;
import mikera.util.Maths;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Scalar;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * General abstract 2D matrix class.
 * 
 * Implements generic version of most key matrix operations.
 * 
 * @author Mike
 */
public abstract class AMatrix extends AbstractArray<AVector> implements IMatrix {
	// ==============================================
	// Abstract interface
	private static final long serialVersionUID = 4854869374064155441L;

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
		int rc = rowCount();
		int cc = columnCount();
		for (int row = 0; row < rc; row++) {
			for (int column = 0; column < cc; column++) {
				unsafeSet(row,column,value);
			}
		}	
	}
	
	/**
	 * Sets an element value in the matrix in an unsafe fashion, without performing bound checks
	 * The result is undefined if the row and column are out of bounds.
	 * @param row
	 * @param column
	 * @return
	 */
	public void unsafeSet(int row, int column, double value) {
		set(row,column,value);
	}
	
	/**
	 * Gets an element in the matrix in an unsafe fashion, without performing bound checks
	 * The result is undefined if the row and column are out of bounds.
	 * @param row
	 * @param column
	 * @return
	 */
	public double unsafeGet(int row, int column) {
		return get(row,column);
	}
	
	@Override
	public void clamp(double min, double max) {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			getRowView(i).clamp(min, max);
		}
	}
	
	@Override
	public void pow(double exponent) {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			AVector v=getRowView(i);
			v.pow(exponent);
		}
	}
	
	@Override
	public void square() {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			getRowView(i).square();
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
	
	/**
	 * Returns the number of dimensions required for input vectors
	 * @return
	 */
	public final int inputDimensions() {
		return columnCount();
	}
	
	/**
	 * Returns the number of dimensions required for output vectors
	 * @return
	 */
	public final int outputDimensions() {
		return rowCount();
	}
	
	@Override
	public long elementCount() {
		return ((long)rowCount())*columnCount();
	}
	
	@Override
	public final AVector slice(int row) {
		return getRowView(row);
	}
	
	@Override
	public AVector slice(int dimension, int index) {
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
	public List<INDArray> getSlices(int dimension) {
		if ((dimension<0)||(dimension>=2)) throw new IllegalArgumentException(ErrorMessages.invalidDimension(this, dimension));
		int l=getShape(dimension);
		ArrayList<INDArray> al=new ArrayList<INDArray>(l);
		for (int i=0; i<l; i++) {
			al.add(slice(dimension,i));
		}
		return al;	
	}
	
	@Override
	public List<INDArray> getSliceViews() {	
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			al.add(getRowView(i));
		}
		return al;
	}
	
	@Override
	public INDArray join(INDArray a, int dimension) {
		if (a instanceof AMatrix) {
			// TODO: JoinedMatrix implementation
		}
		return JoinedArray.join(this,a,dimension);
	}
	
	@Override
	public int[] getShape() {
		return new int[] {rowCount(),columnCount()};
	}
	
	@Override
	public int[] getShapeClone() {
		return new int[] {rowCount(),columnCount()};
	}
	
	@Override
	public int getShape(int dim) {
		if (dim==0) {
			return rowCount();
		} else if (dim==1) {
			return columnCount();
		} else {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
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
	 * Returns a vector view of the leading diagonal values of the matrix
	 * @return
	 */
	public AVector getLeadingDiagonal() {
		return getBand(0);
	}
	
	public double calculateElement(int i, AVector v) {
		return getRow(i).dotProduct(v);
	}
	
	public double calculateElement(int i, Vector v) {
		return getRow(i).dotProduct(v);
	}
	
	public AAffineTransform toAffineTransform() {
		return new AffineMN(this,IdentityTranslation.create(rowCount()));
	}
	
	public boolean isIdentity() {
		int rc=this.rowCount();
		int cc=this.columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				double expected=(i==j)?1.0:0.0;
				if (!(this.unsafeGet(i,j)==expected)) return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSquare() {
		return rowCount() == columnCount();
	}
	
	public boolean isOrthogonal() {
		return isSquare()
				&&getTranspose().innerProduct(this).epsilonEquals(IdentityMatrix.create(columnCount()));
	}

	
	public boolean hasOrthonormalColumns() {
		return getTranspose().innerProduct(this).epsilonEquals(IdentityMatrix.create(columnCount()));
	}
	
	public boolean hasOrthonormalRows() {
		return innerProduct(getTranspose()).epsilonEquals(IdentityMatrix.create(columnCount()));
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
	
	public Matrix reshape(int rows, int cols) {
		return Matrixx.createFromVector(asVector(), rows, cols);
	}
	
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		VectorMatrixMN vm=new VectorMatrixMN(0,cols);
		for (int i=0; i<rows; i++) {
			vm.appendRow(this.getRowView(rowStart+i).subVector(colStart, cols));
		}
		return vm;	
	}
	
	@Override
	public AMatrix subArray(int[] offsets, int[] shape) {
		if (offsets.length!=2) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		if (shape.length!=2) throw new IllegalArgumentException(ErrorMessages.illegalSize(shape));
		return subMatrix(offsets[0],shape[0],offsets[1],shape[1]);
	}
	
	@Override
	public INDArray rotateView(int dimension, int shift) {
		int n=getShape(dimension);
		
		if (n==0) return this;
		shift = Maths.mod(shift,n);
		if (shift==0) return this;
		
		int[] off=new int[2];
		int[] shp=getShapeClone();
		
		shp[dimension]=shift;
		INDArray right=subArray(off,shp);
		shp[dimension]=n-shift;
		off[dimension]=shift;
		INDArray left=subArray(off,shp);
		return left.join(right,dimension);
	}
	
	@Override
	public AVector transform(AVector source) {
		Vector v=Vector.createLength(rowCount());
		if (source instanceof Vector) {
			transform((Vector)source,v);
		} else {
			transform(source,v);
		}
		return v;
	}
	
	public Vector transform(Vector source) {
		Vector v=Vector.createLength(rowCount());
		transform(source,v);
		return v;
	}

	@Override
	public void transform(AVector source, AVector dest) {
		if ((source instanceof Vector )&&(dest instanceof Vector)) {
			transform ((Vector)source, (Vector)dest);
			return;
		}
		int rc = rowCount();
		int cc = columnCount();
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int row = 0; row < rc; row++) {
			dest.unsafeSet(row, getRow(row).dotProduct(source));
		}
	}
	
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = columnCount();
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int row = 0; row < rc; row++) {
			dest.unsafeSet(row, getRow(row).dotProduct(source));
		}
	}

	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof AArrayVector) {
			transformInPlace((AArrayVector)v);
			return;
		}
		double[] temp = new double[v.length()];
		int rc = rowCount();
		int cc = columnCount();
		if (v.length()!=rc) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,v));
		if (rc != cc)
			throw new UnsupportedOperationException(
					"Cannot transform in place with a non-square transformation");
		for (int row = 0; row < rc; row++) {
			temp[row] = getRow(row).dotProduct(v);
		}
		v.setElements(temp);
	}
	
	public void transformInPlace(AArrayVector v) {
		double[] temp = new double[v.length()];
		int rc = rowCount();
		int cc = columnCount();
		if (v.length()!=rc) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,v));
		if (rc != cc)
			throw new UnsupportedOperationException(
					"Cannot transform in place with a non-square transformation");
		double[] data=v.getArray();
		int offset=v.getArrayOffset();
		for (int row = 0; row < rc; row++) {
			temp[row] = getRow(row).dotProduct(data,offset);
		}
		v.setElements(temp);
	}


	/**
	 * Returns a row of the matrix. May or may not be a view, depending on matrix type.
	 */
	public AVector getRow(int row) {
		return getRowView(row);
	}

	/**
	 * Returns a column of the matrix. May or may not be a view, depending on matrix type.
	 */
	public AVector getColumn(int column) {
		return getColumnView(column);
	}
	
	/**
	 * Returns a row of the matrix as a vector view
	 */
	public AVector getRowView(int row) {
		return new MatrixRowView(this, row);
	}

	/**
	 * Returns a column of the matrix as a vector view
	 */
	public AVector getColumnView(int column) {
		return new MatrixColumnView(this, column);
	}
	
	/**
	 * Returns a row of the matrix as a new cloned vector
	 */
	public AVector getRowClone(int row) {
		int cc=this.columnCount();
		Vector v=Vector.createLength(cc);
		copyRowTo(row,v.data,0);
		return v;
	}

	/**
	 * Returns a column of the matrix as a new cloned vector
	 */
	public AVector getColumnClone(int column) {
		int rc=this.rowCount();
		Vector v=Vector.createLength(rc);
		copyColumnTo(column,v.data,0);
		return v;
	}

	public void set(AMatrix a) {
		int rc = rowCount();
		int cc = columnCount();
		if ((a.rowCount() != rc)||(a.columnCount()!=cc)) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));			
		}
		for (int row = 0; row < rc; row++) {
			for (int column = 0; column < cc; column++) {
				unsafeSet(row, column, a.unsafeGet(row, column));
			}
		}
	}
	
	public void set(INDArray a) {
		if (a instanceof AMatrix) {set((AMatrix) a); return;}	
		if (a instanceof AVector) {for (AVector r:this) {r.set((AVector) a);} return;}
		if (a instanceof AScalar) {set(a.get()); return;}
		
		throw new UnsupportedOperationException("Can't set matrix to array: "+a.getClass() +" with shape: "+Arrays.toString(a.getShape()));
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
		for (int i = 0; i < rc; i++) {
			int iOffset=offset+i*cc;
			for (int j = 0; j < cc; j++) {
				unsafeSet(i,j,values[iOffset+j]);
			}
		}	
	} 
	
	@Override
	public void getElements(double[] dest, int offset) {
		int rc=this.rowCount();
		int cc=this.columnCount();
		for (int i=0; i<rc; i++) {
			copyRowTo(i,dest,offset+i*cc);
		}
	}
	
	@Override
	public void copyTo(double[] arr) {
		getElements(arr,0);
	}
	
	@Override
	public void setElements(double[] values) {
		setElements(values,0,values.length);
	}

	@Override
	public abstract boolean isFullyMutable();
	
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
	
	@Override
	public AMatrix copy() {
		if (isMutable()) return clone();
		return this;
	}
	
	public final AVector cloneRow(int row) {
		return getRowClone(row);
	}
	
	public final AVector cloneColumn(int column) {
		return getColumnClone(column);
	}
	
	@Override
	public AMatrix sparseClone() {
		return Matrixx.createSparse(this);
	}
	
	@Override
	public AMatrix ensureMutable() {
		if (isFullyMutable()&&!isView()) return this;
		return clone();
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

	private double calcDeterminant(int[] inds, int offset) {
		int rc = rowCount();
		if (offset == (rc - 1))
			return unsafeGet(offset, inds[offset]);

		double det = unsafeGet(offset, inds[offset])
				* calcDeterminant(inds, offset + 1);
		for (int i = 1; i < (rc - offset); i++) {
			IntArrays.swap(inds, offset, offset + i);
			det -= unsafeGet(offset, inds[offset])
					* calcDeterminant(inds, offset + 1);
			IntArrays.swap(inds, offset, offset + i);
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
			throw new UnsupportedOperationException(ErrorMessages.squareMatrixRequired(this));
		int dims = rowCount();
		for (int i = 0; i < dims; i++) {
			for (int j = i + 1; j < dims; j++) {
				double temp = unsafeGet(i, j);
				unsafeSet(i, j, unsafeGet(j, i));
				unsafeSet(j, i, temp);
			}
		}
	}

	/**
	 * Returns the transpose of this matrix. 
	 * 
	 * Will be a transposed view by default, but specialised matrix type may override this if they are able to provide
	 * a better implementation.
	 * 
	 * @return
	 */
	@Override
	public AMatrix getTranspose() {
		return getTransposeView();
	}
	
	/**
	 * Returns a transposed view of the matrix. 
	 */
	@Override
	public AMatrix getTransposeView() {
		return TransposedMatrix.wrap(this);
	}
	
	/**
	 * Gets a mutable transposed clone of the matrix
	 */
	@Override
	public AMatrix getTransposeCopy() {
		return copy().getTranspose();
	}
	
	/**
	 * Adds another matrix to this matrix. Matrices must be the same size.
	 */
	public void add(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=m.rowCount())||(cc!=m.columnCount())) throw new IllegalArgumentException(ErrorMessages.mismatch(this, m));

		if ((cc>5)||(this instanceof IFastRows)) {
			for (int i=0; i<rc; i++) {
				getRowView(i).add(m.getRow(i));
			}			
		} else {
			for (int i=0; i<rc; i++) {
				for (int j=0; j<cc; j++) {
					unsafeSet(i,j,unsafeGet(i,j)+m.unsafeGet(i, j));
				}
			}
		}
	}
	
	/**
	 * Adds a vector to every row of this matrix.
	 */
	public void add(AVector v) {
		int rc=rowCount();
		int cc=columnCount();
		if(cc!=v.length()) throw new IllegalArgumentException(ErrorMessages.mismatch(this, v));

		for (int i=0; i<rc; i++) {
			getRowView(i).add(v);
		}		
	}
	
	public void sub(AVector v) {
		int rc=rowCount();
		int cc=columnCount();
		if(cc!=v.length()) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, v));

		for (int i=0; i<rc; i++) {
			getRowView(i).sub(v);
		}		
	}
	
	@Override
	public void sub(double d) {
		add(-d);
	}
	
	@Override
	public final void scaleAdd(double factor, double constant) {
		multiply(factor);
		add(constant);
	}
	
	public void multiply(double factor) {
		int rc=rowCount();

		for (int i=0; i<rc; i++) {
			getRowView(i).multiply(factor);
		}
	}	

	/**
	 * Returns the sum of all elements in the matrix
	 * @param m
	 * @return 
	 */
	public double elementSum() {
		int rc=rowCount();
		
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=getRowView(i).elementSum();
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
		
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=getRowView(i).elementSquaredSum();
		}
		return result;
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new MatrixElementIterator(this);
	}
	
	@Override
	public boolean isBoolean() {
		double[] data=Tools.getElements(this);
		return DoubleArrays.isBoolean(data,0,data.length);
	}
	
	@Override
	public long nonZeroCount() {
		long result=0;
		int rc=rowCount();
		int cc=columnCount();
		
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				if (unsafeGet(i,j)!=0.0) result++;
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
	
	public void sub(AScalar a) {
		add(-a.get());
	}
	
	public void add(AScalar a) {
		add(a.get());
	}
	
	@Override
	public void negate() {
		multiply(-1.0);
	}
	
	@Override
	public void reciprocal() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRowView(i).reciprocal();
		}
	}
	
	@Override
	public void abs() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRowView(i).abs();
		}
	}
	
	@Override
	public void sqrt() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRowView(i).sqrt();
		}
	}
	
	@Override
	public void log() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRowView(i).log();
		}
	}
	
	@Override
	public void exp() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRowView(i).exp();
		}
	}
	
	@Override
	public void signum() {
		int sc=rowCount();
		for (int i=0; i<sc; i++) {
			getRowView(i).signum();
		}
	}
	
	/**
	 * Multiplies this matrix in-place by another in an entrywise manner (Hadamard product).
	 * @param m
	 */
	public void elementMul(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=m.rowCount())||(cc!=m.columnCount())) throw new IllegalArgumentException(ErrorMessages.mismatch(this, m));

		for (int i=0; i<rc; i++) {
			getRowView(i).multiply(m.getRow(i));
		}
	}
	
	/**
	 * Divides this matrix in-place by another in an entrywise manner.
	 * @param m
	 */
	public void elementDiv(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=m.rowCount())||(cc!=m.columnCount())) throw new IllegalArgumentException(ErrorMessages.mismatch(this, m));

		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				unsafeSet(i,j,unsafeGet(i,j)/m.unsafeGet(i, j));
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
	
	/**
	 * Multiplies a row by a constant factor
	 * This is an elementary row operation
	 */
	public void multiplyRow(int i, double factor) {
		getRowView(i).multiply(factor);
	}
	
	/**
	 * Adds a multiple of a source row to a destination row
	 * This is an elementary row operation
	 */
	public void addRowMultiple(int src, int dst, double factor) {
		getRowView(dst).addMultiple(getRow(src), factor);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		int cc=columnCount();
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRow(i).addToArray(data, offset+i*cc);
		}
	}
	
	/**
	 * Swaps two rows of the matrix in place
	 * This is an elementary row operation
	 */
	public void swapRows(int i, int j) {
		if (i == j)
			return;
		AVector a = getRowView(i);
		AVector b = getRowView(j);
		int cc = columnCount();
		for (int k = 0; k < cc; k++) {
			double t = a.unsafeGet(k);
			a.unsafeSet(k, b.unsafeGet(k));
			b.unsafeSet(k, t);
		}
	}

	/**
	 * Swaps two columns of the matrix in place
	 */
	public void swapColumns(int i, int j) {
		if (i == j)
			return;
		AVector a = getColumnView(i);
		AVector b = getColumnView(j);
		int rc = rowCount();
		for (int k = 0; k < rc; k++) {
			double t = a.unsafeGet(k);
			a.unsafeSet(k, b.unsafeGet(k));
			b.unsafeSet(k, t);
		}
	}
	
	public void composeWith(AMatrix a) {
		AMatrix t=compose(a);
		this.set(t);
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	public void addMultiple(AMatrix m, double factor) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=m.rowCount())||(cc!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
		
		for (int i=0; i<rc; i++) {
			getRowView(i).addMultiple(m.getRow(i), factor);
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
		if (a instanceof AMatrix) {
			return epsilonEquals((AMatrix) a,epsilon);
		} if (a.dimensionality()!=2) {
			return false;
		} else {
			int sc=rowCount();
			if (a.sliceCount()!=sc) return false;
			for (int i=0; i<sc; i++) {
				AVector s=getRow(i);
				if (!s.epsilonEquals(a.slice(i),epsilon)) return false;
			}			
			return true;
		}
	}
	
	public boolean epsilonEquals(AMatrix a, double epsilon) {
		if (a==this) return true;
		int sc=rowCount();
		if (a.rowCount()!=sc) return false;
		for (int i=0; i<sc; i++) {
			AVector s=getRow(i);
			if (!s.epsilonEquals(a.getRow(i),epsilon)) return false;
		}			
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AMatrix) return equals((AMatrix) o);
		if (o instanceof INDArray) return equals((INDArray) o);
		return false;
	}

	/**
	 * Returns true if this matrix is exactly equal to another matrix
	 */
	public boolean equals(AMatrix a) {
		if (a instanceof AArrayMatrix) return a.equals(this);
		
		if (a==this) return true;
		int rc = rowCount();
		if (rc != a.rowCount()) return false;
		int cc = columnCount();
		if (cc != a.columnCount()) return false;
		
		if ((cc>10)||(this instanceof IFastRows)) {
			return equalsByRows(a);		
		} else {
			for (int i = 0; i < rc; i++) {
				for (int j = 0; j < cc; j++) {
					if (unsafeGet(i, j) != a.unsafeGet(i, j))
						return false;
				}
			}			
		}

		return true;
	}
	
	@Override
	public boolean elementsEqual(double value) {
		int rc = rowCount();
		
		if (this instanceof IFastRows) {
			for (int i = 0; i < rc; i++) {
				if (!getRow(i).elementsEqual(value)) return false;
			}
		} else {
			int cc = columnCount();
			for (int i = 0; i < rc; i++) {
				for (int j = 0; j < cc; j++) {
					if (unsafeGet(i, j) != value) return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Tests if this matrix is exactly equal to the transpose of another matrix
	 * @param a
	 * @return
	 */
	public boolean equalsTranspose(AMatrix a) {
		int rc = rowCount();
		if (rc != a.columnCount())
			return false;
		int cc = columnCount();
		if (cc != a.rowCount())
			return false;
		for (int i = 0; i < rc; i++) {
			if (!getRow(i).equals(a.getColumn(i))) return false;
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
		
		for (int i = 0; i < rc; i++) {
			if (!getRow(i).equals(v.slice(i))) return false;
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
			throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		
		if ((this instanceof IFastRows)&&(a instanceof IFastRows)) {
			for (int i = 0; i < rc; i++) {
				if (!getRow(i).epsilonEquals(a.getRow(i))) return false;	
			}
		} else {
			for (int i = 0; i < rc; i++) {
				for (int j = 0; j < cc; j++) {
					if (!Tools.epsilonEquals(unsafeGet(i, j), a.unsafeGet(i, j)))
						return false;
				}
			}
		}
		return true;
	}

	public boolean equals(AAffineTransform a) {

		return a.getTranslation().isIdentity()
				&& this.equals(a.getMatrix());
	}
	
	protected boolean equalsByRows(AMatrix m) {
		int rc = rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).equals(m.getRow(i))) return false;
		}
		return true;
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
				hashCode = 31 * hashCode + (Hash.hashCode(unsafeGet(i, j)));
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
		if (rc == 0) return Vector0.INSTANCE;
		if (rc == 1) return getRowView(0);
		
		int cc= columnCount();
		if (cc==1) return getColumnView(0);

		return new MatrixAsVector(this);
	}
	
	@Override
	public List<Double> asElementList() {
		return asVector().asElementList();
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
		return Multiplications.multiply(this, a);
	}
	
	public final Vector innerProduct(Vector v) {
		return transform(v);
	}
	
	public Matrix innerProduct(Matrix a) {
		return Multiplications.multiply(this, a);
	}
	
	public AVector innerProduct(AVector v) {
		if (v instanceof Vector) {
			return transform((Vector)v);
		} else {
			return transform(v);
		}
	}
	
	@Override
	public AMatrix innerProduct(AScalar s) {
		return innerProduct(s.get());
	}
	
	@Override
	public AMatrix innerProduct(double d) {
		AMatrix r= clone();
		r.scale(d);
		return r;
	}	
	
	public AMatrix transposeInnerProduct(AMatrix s) {
		if (s instanceof Matrix) return transposeInnerProduct((Matrix)s);
		if (isSparse()) {
			// TODO: any better approach?
			Matrix r= toMatrixTranspose();
			return Multiplications.multiply(r, s);
		} else {
			Matrix r= toMatrixTranspose();
			return Multiplications.multiply(r, s);			
		}
	}
	
	public Matrix transposeInnerProduct(Matrix s) {
		Matrix r= toMatrixTranspose();
		return Multiplications.multiply(r, s);
	}
	
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AVector) {
			return innerProduct((AVector)a);
		} else if (a instanceof AMatrix) {
			return compose((AMatrix) a);
		} else if (a instanceof AScalar) {
			return innerProduct((AScalar)a);
		} else if (a.dimensionality()<=2) {
			return innerProduct(Arrayz.create(a)); // convert to most efficient format
		}
		return Array.create(this).innerProduct(a);
	}

	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (AVector s:this) {
			al.add(s.outerProduct(a));
		}
		return Arrayz.create(al);
	}

	@Override
	public AMatrix inverse() {
		AMatrix result = Matrixx.createInverse(this);
		return result;
	}
	
	/**
	 * Computes the trace of a matrix
	 * 
	 * @return
	 */
	public double trace() {
		int rc=Math.min(rowCount(), columnCount());
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=unsafeGet(i,i);
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
	@Override
	public Vector toVector() {
		int rc = rowCount();
		int cc = columnCount();
		Vector v = Vector.createLength(rc * cc);
		this.getElements(v.data,0);
		return v;
	}
	
	@Override
	public Array toArray() {
		return Array.create(this);
	}
	
	/**
	 * Coerces the matrix to the standard mutable Matrix type
	 * in row major order. Performs a copy if necessary.
	 */
	public Matrix toMatrix() {
		int rc = rowCount();
		int cc = columnCount();
		return Matrix.wrap(rc, cc, this.toDoubleArray());
	}
	
	/**
	 * Coerces the transpose of a matrix to the standard mutable Matrix type
	 * in row major order. Performs a copy if necessary.
	 */
	public Matrix toMatrixTranspose() {
		int rc = rowCount();
		int cc = columnCount();
		return Matrix.wrap(cc, rc,this.getTranspose().toDoubleArray());
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		int n=rowCount();
		for (int i=0; i<n; i++) {
			getRow(i).toDoubleBuffer(dest);
		}
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] result=Matrix.createStorage(rowCount(),columnCount());
		getElements(result,0);
		return result;
	}
	
	@Override
	public double[] asDoubleArray() {
		return null;
	}
	
	@Override
	public void applyOp(Op op) {
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).applyOp(op);
		}
	}
	
	@Override
	public void applyOp(IOperator op) {
		if (op instanceof Op) {applyOp((Op)op); return;}
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).applyOp(op);
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
				throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
			}
		}
	}
	
	@Override
	public void divide(INDArray a) {
		if (a instanceof AMatrix) {
			elementDiv((AMatrix)a);
		} else if (a instanceof AScalar) {
			multiply(1.0/a.get());
		} else {
			int dims=a.dimensionality();
			int rc=rowCount();
			if (dims==0) {
				multiply(1.0/a.get());
			} else if (dims==1) {
				for (int i=0; i<rc; i++) {
					slice(i).divide(a);
				}
			} else if (dims==2) {
				for (int i=0; i<rc; i++) {
					slice(i).divide(a.slice(i));
				}		
			} else {
				throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
			}
		}	
	}
	
	@Override
	public void divide(double factor) {
		multiply(1.0/factor);
	}
	
	@Override
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
		unsafeSet(i,j,unsafeGet(i,j)+d);
	}
	
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims<2) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));				
		} else if (2==tdims) {
			if (rowCount()==targetShape[0]&&columnCount()==targetShape[1]) return this;
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));				
		} else {
			if (rowCount()!=targetShape[tdims-2]||(columnCount()!=targetShape[tdims-1])) {
				throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));				
			}
			int n=targetShape[0];
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,n);
		}
	}
	
	@Override
	public INDArray broadcastLike(INDArray target) {
		if (target instanceof AMatrix) {
			return broadcastLike((AMatrix)target);
		}
		return broadcast(target.getShape());
	}
	
	public INDArray broadcastLike(AMatrix target) {
		if (rowCount()==target.rowCount()&&(columnCount()==target.columnCount())) {
			return this;
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, target));
		}
	}
	
	@Override
	public INDArray broadcastCloneLike(INDArray target) {
		INDArray r=this;
		if (target.dimensionality()>2) r=r.broadcastLike(target);
		return r.clone();
	}

	/**
	 * Returns true if the matrix is the zero matrix (all components zero)
	 */
	public boolean isZero() {
		return elementsEqual(0.0);
	}
	
	/**
	 * Returns true if a matrix is positive definite
	 */
	public void isPositiveDefinite() {
		throw new UnsupportedOperationException(ErrorMessages.notYetImplemented());
	}
	
	/**
	 * Returns true iff a matrix is a square diagonal matrix
	 */
	public boolean isDiagonal() {
		int rc=rowCount();
		int cc=columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				if ((i!=j)&&(unsafeGet(i,j)!=0.0)) return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isSameShape(INDArray a) {
		if (a instanceof AMatrix) return isSameShape((AMatrix)a);
		if (a.dimensionality()!=2) return false;
		for (int i=0; i<2; i++) {
			if (getShape(i)!=a.getShape(i)) return false;
		}
		return true;
	}
	
	public boolean isSameShape(AMatrix a) {
		return (this.rowCount()==a.rowCount())&&(this.columnCount()==a.columnCount());
	}
	
	public boolean isRectangularDiagonal() {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				if ((i!=j)&&(unsafeGet(i,j)!=0.0)) return false;
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
				if (unsafeGet(i,j)!=unsafeGet(j,i)) return false;
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
				if (unsafeGet(i,j)!=0.0) return false;
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
				if (unsafeGet(i,j)!=0.0) return false;
			}
		}
		return true;
	}
	
	/**
	 * A limit on the upper bandwidth of the banded matrix. Actual upper bandwidth is guaranteed
	 * to be less than or equal to this value
	 * @return
	 */
	public int upperBandwidthLimit() {
		return columnCount()-1;
	}
	
	/**
	 * A limit on the lower bandwidth of the banded matrix. Actual lower bandwidth is guaranteed
	 * to be less than or equal to this value
	 * @return
	 */
	public int lowerBandwidthLimit() {
		return rowCount()-1;
	}
	
	/**
	 * Returns the length of a band of the matrix. Returns 0 if the band is outside the matrix.
	 * @param band
	 * @return
	 */
	public int bandLength(int band) {
		return bandLength(rowCount(),columnCount(),band);
	}
	
	/**
	 * Returns the start row of a given band.
	 * @param band
	 * @return
	 */
	public final int bandStartRow(int band) {
		return (band<0)?-band:0;
	}
	
	/**
	 * Returns the start column of a given band.
	 * @param band
	 * @return
	 */
	public final int bandStartColumn(int band) {
		return (band>0)?band:0;
	}
	
	protected final static int bandLength(int rc, int cc, int band) {
		if (band>0) {
			return (band<cc)?Math.min(rc, cc-band):0;
		} else {
			band=-band;
			return (band<rc)?Math.min(cc, rc-band):0;			
		}
	}
	
	/**
	 * Returns the band index number for a specified position in the matrix.
	 * @param i
	 * @param j
	 * @return
	 */
	public int bandIndex(int i, int j) {
		return j-i;
	}
	
	/**
	 * Returns the band position for a specified (i,j) index in the matrix.
	 * @param i
	 * @param j
	 * @return
	 */
	public int bandPosition(int i, int j) {
		return Math.min(i, j);
	}
	
	/**
	 * Computes the upper bandwidth of a matrix
	 * @return
	 */
	public int upperBandwidth() {
		for (int band=upperBandwidthLimit(); band>0; band--) {
			int bandLen=bandLength(band);
			for (int i=0; i<bandLen; i++) {
				if (unsafeGet(band+i,i)!=0.0) return band;
			}
		}
		return 0;
	}
	
	/**
	 * Computes the lower bandwidth of a matrix
	 * @return
	 */
	public int lowerBandwidth() {
		for (int band=lowerBandwidthLimit(); band>0; band--) {
			int bandLen=bandLength(-band);
			for (int i=0; i<bandLen; i++) {
				if (unsafeGet(i,band+i)!=0.0) return band;
			}
		}
		return 0;
	}
	
	/**
	 * Gets a specific band of the matrix, as a view vector. The band is truncated at the edges of the
	 * matrix, i.e. it does not wrap around the matrix.
	 * 
	 * @param band
	 * @return
	 */
	@Override
	public AVector getBand(int band) {
		return MatrixBandView.create(this,band);
	}
	
	public AVector getBandWrapped(int band) {
		AVector result=Vector0.INSTANCE;
		int rc=rowCount();
		int cc=columnCount();
		if (rc<cc) {
			int si=band%rc;
			if (si>0) si-=rc;
			for (;si<cc; si+=rc) {
				result=result.join(getBand(si));
			}
		} else {
			if (cc==0) return result;
			int si=band%cc;
			if (si<0) si+=cc;
			for (;si>-rc; si-=cc) {
				result=result.join(getBand(si));
			}
		}
		return result;
	}
	
	/**
	 * Sets a row in a matrix. 
	 * 
	 * @param i
	 * @param row
	 */
	public void setRow(int i, AVector row) {
		getRow(i).set(row);
	}
	
	/**
	 * Replaces a row in a matrix, adding the row to the internal structure of the matrix.
	 * 
	 * Will throw UnsupportedOperationException if not possible for the given matrix type.
	 * 
	 * @param i
	 * @param row
	 */
	public void replaceRow(int i, AVector row) {
		throw new UnsupportedOperationException("replaceRow not supported for "+this.getClass()+". Consider using an AVectorMatrix or SparseRowMatrix instance instead.");
	}
	
	/**
	 * Replaces a column in a matrix, adding the column to the internal structure of the matrix.
	 * 
	 * Will throw UnsupportedOperationException if not possible for the given matrix type.
	 * 
	 * @param i
	 * @param row
	 */
	public void replaceColumn(int i, AVector row) {
		throw new UnsupportedOperationException("replaceColumn not supported for "+this.getClass()+". Consider using a SparseColumnMatrix instance instead.");
	}
	
	/**
	 * Sets a column in a matrix. 
	 * 
	 * @param i
	 * @param row
	 */
	public void setColumn(int i, AVector col) {
		getColumn(i).set(col);
	}

	@Override
	public abstract AMatrix exactClone();
	
	@Override
	public INDArray immutable() {
		if (!isMutable()) return this;
		return ImmutableMatrix.create(this);
	}
	
	@Override
	public AMatrix mutable() {
		if (isFullyMutable()) return this;
		return clone();
	}
	
	@Override
	public AMatrix sparse() {
		if (this instanceof ISparse) return this;
		return Matrixx.createSparse(this);
	}
	
	@Override
	public INDArray dense() {
		if (this instanceof IDense) return this;
		return Matrix.create(this);
	}
	
	@Override 
	public void validate() {
		// nothing to do since we have no data to validate
	}

	public void copyRowTo(int row, double[] dest, int destOffset) {
		int cc=columnCount();
		for (int i=0; i<cc; i++) {
			dest[i+destOffset]=unsafeGet(row,i);
		}
	}
	
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			dest[i+destOffset]=unsafeGet(i,col);
		}
	}
}
