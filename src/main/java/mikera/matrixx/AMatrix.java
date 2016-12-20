package mikera.matrixx;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.ISparse;
import mikera.arrayz.impl.AbstractArray;
import mikera.arrayz.impl.IDense;
import mikera.arrayz.impl.JoinedArray;
import mikera.arrayz.impl.SliceArray;
import mikera.indexz.AIndex;
import mikera.indexz.Index;
import mikera.matrixx.algo.Definite;
import mikera.matrixx.algo.Determinant;
import mikera.matrixx.algo.Inverse;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.algo.Rank;
import mikera.matrixx.impl.ADenseArrayMatrix;
import mikera.matrixx.impl.ARectangularMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ImmutableMatrix;
import mikera.matrixx.impl.MatrixBandView;
import mikera.matrixx.impl.MatrixColumnList;
import mikera.matrixx.impl.MatrixColumnView;
import mikera.matrixx.impl.MatrixElementIterator;
import mikera.matrixx.impl.MatrixRowIterator;
import mikera.matrixx.impl.MatrixRowList;
import mikera.matrixx.impl.MatrixRowView;
import mikera.matrixx.impl.SparseColumnMatrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.matrixx.impl.SubMatrixView;
import mikera.matrixx.impl.TransposedMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.randomz.Hash;
import mikera.transformz.AAffineTransform;
import mikera.transformz.AffineMN;
import mikera.transformz.impl.IdentityTranslation;
import mikera.util.Maths;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.MatrixViewVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.Constants;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract 2D matrix class. All Vectorz 2D matrices inherit from this class.
 * 
 * Implements generic version of most key matrix operations.
 * 
 * @author Mike
 */
public abstract class AMatrix extends AbstractArray<AVector> implements IMatrix {
	// ==============================================
	// Abstract interface
	private static final long serialVersionUID = 4854869374064155441L;
	
	@Override
	public abstract double get(int i, int j);

	@Override
	public abstract void set(int i, int j, double value);

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
	public final void set(double value) {
		fill(value);
	}
	
	@Override 
	public void fill(double value) {
		int len=rowCount();
		for (int i = 0; i < len; i++) {
			getRowView(i).fill(value);
		}
	}
	
	/**
	 * Sets an element value in the matrix in an unsafe fashion, without performing bound checks
	 * The result is undefined if the row and column are out of bounds.
	 * @param i
	 * @param j
	 * @return
	 */
	public void unsafeSet(int i, int j, double value) {
		set(i,j,value);
	}
	
	/**
	 * Gets an element in the matrix in an unsafe fashion, without performing bound checks
	 * The result is undefined if the row and column are out of bounds.
	 * @param i
	 * @param j
	 * @return
	 */
	public double unsafeGet(int i, int j) {
		return get(i,j);
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
	public void set(long[] indexes, double value) {
		if (indexes.length==2) {
			set(Tools.toInt(indexes[0]),Tools.toInt(indexes[1]),value);
		} else {
			throw new VectorzException(""+indexes.length+"D set not supported on AMatrix");
		}
	}
	
	@Override
	public final int dimensionality() {
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
		checkDimension(dimension);
		return (dimension==0)?getRowView(index):getColumnView(index);	
	}	
	
	@Override
	public int sliceCount() {
		return rowCount();
	}
	
	@Override
	public final List<AVector> getSlices() {
		return getRows();
	}
	
    @Override
	public List<AVector> getRows() {    
		return new MatrixRowList(this);
	}
    
    @Override
	public List<AVector> getColumns() {    
		return new MatrixColumnList(this);
	}
	
	@Override
	public final List<AVector> getSlices(int dimension) {
		checkDimension(dimension);
		return (dimension==0)?getRows():getColumns();
	}
	
	@Override
	public List<INDArray> getSliceViews() {	
		int rc=rowCount();
		ArrayList<INDArray> al=new ArrayList<INDArray>(rc);
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
	
	@Override
	public final double get(AIndex ix) {
		if (ix.length()!=2) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, ix));
		return get(ix.get(0),ix.get(1));
	}
	
	@Override
	public double getElement(long i) { 
		int rc=rowCount();
		int cc=columnCount();
		if ((i<0)||(i>=(rc*cc))) throw new IndexOutOfBoundsException(ErrorMessages.invalidElementIndex(this,i));
		return unsafeGet((int)(i/cc),(int)(i%cc));
	}
	
	/**
	 * Returns a vector view of the leading diagonal values of the matrix
	 * @return
	 */
	public AVector getLeadingDiagonal() {
		return getBand(0);
	}
	
	public AAffineTransform toAffineTransform() {
		return new AffineMN(this,IdentityTranslation.create(rowCount()));
	}
	
	@Override
	public boolean isIdentity() {
		int rc=this.rowCount();
		int cc=this.columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			AVector v=getRow(i);
			if (v.unsafeGet(i)!=1.0) return false;
			if (!v.isRangeZero(0, i)) return false;
			if (!v.isRangeZero(i+1, cc-i-1)) return false;
		}
		return true;
	}

	@Override
	public boolean isSquare() {
		return rowCount() == columnCount();
	}
	
	/**
	 * Check to see if the matrix is orthogonal
	 * (default tolerance: 1e-8)
	 * @return
	 */
	public boolean isOrthogonal() {
	    return isOrthogonal(Vectorz.TEST_EPSILON);
	}
	
	/**
	 * Check to see if the matrix is orthogonal
	 * @param tolerance inner product of a column with all of the next columns should be less than tolerance
	 * @return
	 */
	public boolean isOrthogonal(double tolerance) {
	    if(!isSquare())
	        return false;
	    int n=rowCount();
	    
        List<AVector> cols=getColumns();
	    for( int i = 0; i < n; i++ ) {
            AVector a = cols.get(i);
            if (!a.isUnitLengthVector(tolerance)) return false;
            for( int j = i+1; j < n; j++ ) {
                double val = a.dotProduct(cols.get(j));
                if ((Math.abs(val) > tolerance)) return false;
            }
        }
        
        return true;
	}

	/**
	 * Tests whether all columns in the matrix are orthonormal vectors
	 * @return
	 */
	public boolean hasOrthonormalColumns() {
		return getTranspose().innerProduct(this).epsilonEquals(IdentityMatrix.create(columnCount()));
	}
	
	/**
	 * Tests whether all rows in the matrix are orthonormal vectors
	 * @return
	 */
	public boolean hasOrthonormalRows() {
		return innerProduct(getTranspose()).epsilonEquals(IdentityMatrix.create(rowCount()));
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
	
	@Override
	public AMatrix reorder(int[] order) {
		return reorder(0,order);
	}	
	
	@Override
	public AMatrix reorder(int dim, int[] order) {
		int n=order.length;
		switch (dim) {
			case 0: {
				if (n==0) return ZeroMatrix.create(0, columnCount());
				if (IntArrays.isRange(order)) {
					return subMatrix(order[0],n,0,columnCount());
				}
				
				ArrayList<AVector> al=new ArrayList<AVector>(n);
				for (int si: order) {
					al.add(slice(si));
				}
				return SparseRowMatrix.wrap(al);
			}
			case 1: {
				if (n==0) return ZeroMatrix.create(rowCount(),0);
				if (IntArrays.isRange(order)) {
					return subMatrix(0,rowCount(),order[0],n);
				}
				
				ArrayList<AVector> al=new ArrayList<AVector>(n);
				for (int si: order) {
					al.add(slice(1,si));
				}
				return SparseColumnMatrix.wrap(al);
			}
			default: throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
		}
	}	
	
	@Override
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		if ((rows==0)||(cols==0)) return ZeroMatrix.create(rows, cols);
		return new SubMatrixView(this, rowStart, colStart, rows, cols);	
	}
	
	@Override
	public final AMatrix subArray(int[] offsets, int[] shape) {
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
			dest.unsafeSet(row, rowDotProduct(row,source));
		}
	}
	
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = columnCount();
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int row = 0; row < rc; row++) {
			dest.unsafeSet(row, rowDotProduct(row,source));
		}
	}

	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof ADenseArrayVector) {
			transformInPlace((ADenseArrayVector)v);
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
	
	public void transformInPlace(ADenseArrayVector v) {
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

	@Override
	public AVector getRow(int row) {
		return getRowView(row);
	}

	@Override
	public AVector getColumn(int column) {
		return getColumnView(column);
	}
	
	@Override
	public AVector getRowView(int row) {
		return new MatrixRowView(this, row);
	}

	@Override
	public AVector getColumnView(int column) {
		return new MatrixColumnView(this, column);
	}
	
	@Override
	public AVector getRowClone(int row) {
		int cc=columnCount();
		Vector result=Vector.createLength(cc);
		this.copyRowTo(row,result.getArray(),0);
		return result;
	}

	@Override
	public AVector getColumnClone(int column) {
		int rc=rowCount();
		Vector result=Vector.createLength(rc);
		this.copyColumnTo(column,result.getArray(),0);
		return result;
	}

	/**
	 * Sets this matrix with the element values from another matrix. Source matrix must have the same shape.
	 * @param a
	 */
	public void set(AMatrix a) {
		int rc = rowCount();
		int cc = columnCount();
		a.checkShape(rc,cc);
		for (int i = 0; i < rc; i++) {
			setRow(i,a.getRow(i));
		}
	}
	
	@Override
	public void set(INDArray a) {
		if (a instanceof AMatrix) {set((AMatrix) a); return;}	
		if (a instanceof AVector) {set((AVector)a); return;}
		if (a instanceof AScalar) {set(a.get()); return;}
		// fall back to default impl
		super.set(a);
	}
	
	/**
	 * Sets every row of this matrix with the element values from a vector.
	 * @param a
	 */
	public void set(AVector v) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).set(v);
		}
	}
	
	@Override
	public void set(Object o) {
		if (o instanceof INDArray) {
			set((INDArray)o);
		} else if (o instanceof Number) {
			set(((Number) o).doubleValue());
		} else {
			set(Matrixx.toMatrix(o));		
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
	public final void setElements(double... values) {
		int vl=values.length;
		if (vl!=elementCount()) throw new IllegalArgumentException("Incorrect number of elements in array: "+vl);
		setElements(values,0);
	}
	
	@Override
	public final void setMultiple(INDArray a, double b) {
		if (a instanceof AMatrix) {
			setMultiple((AMatrix)a,b);
		} else switch (a.dimensionality()) {
			case 1: setMultiple(a.asVector(),b); return;
			case 0: fill(a.get()*b); return;
		}
		super.setMultiple(a, b);
	}
	
	@Override
	public void setMultiple(INDArray a, INDArray b) {
		if (a instanceof AMatrix) {
			if (b instanceof AMatrix) {
				setMultiple((AMatrix)a,(AMatrix)b);
				return;
			} else if (b.dimensionality()==0) {
				setMultiple((AMatrix)a,b.get());
			} else {
				set((AMatrix)a);
				multiply(b);
			}
		} else {
			set(a);
			multiply(b);
		}
	}

	/**
	 * Sets this matrix to the elementwise product of two other matrices
	 * @param a
	 * @param b
	 */
	public void setMultiple(AMatrix a, AMatrix b) {
		set(a);
		multiply(b);
	}
	
	/**
	 * Sets this matrix to a multiple of another matrix
	 * @param a
	 * @param b
	 */
	public final void setMultiple(AMatrix a, double b) {
		set(a);
		scale(b);
	}
	
	/**
	 * Sets every row of this matrix to a multiple of a vector
	 * @param a
	 * @param b
	 */
	public final void setMultiple(AVector a, double b) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).setMultiple(a, b);
		}
	}
	
	@Override
	public void setElements(double[] values, int offset) {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			slice(i).setElements(values,offset+i*cc);
		}
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
		return Matrix.create(this);
	}
	
	@Override
	public AMatrix copy() {
		if (isMutable()) return clone();
		return this;
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
		return Determinant.calculate(this);
	}

	/**
	 * Calculate the rank of a matrix.
	 * 
	 * This is equivalent to the maximum number of linearly independent rows or columns.
	 */
	public int rank() {
		return Rank.compute(this);
	}

	/**
	 * Creates a fully mutable deep copy of this matrix
	 * @return A new matrix
	 */
	public AMatrix toMutableMatrix() {
		return Matrixx.create(this);
	}

	@Override
	public void transposeInPlace() {
		int dims = checkSquare();
		for (int i = 0; i < dims; i++) {
			AVector row=getRowView(i);
			AVector col=getColumnView(i);
			for (int j = i + 1; j < dims; j++) {
				double vr = row.unsafeGet(j);
				double vc = col.unsafeGet(j);
				if (vr!=vc) {
					row.unsafeSet(j,vc);
					col.unsafeSet(j,vr);
				}
			}
		}
	}

	@Override
	public AMatrix getTranspose() {
		return getTransposeView();
	}
	
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
		m.checkShape(rc, cc);

		for (int i=0; i<rc; i++) {
			getRowView(i).add(m.getRow(i));
		}		
	}
	
	/**
	 * Adds a vector to every row of this matrix.
	 */
	public void add(AVector v) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).add(v);
		}		
	}
	
	/**
	 * Subtracts a vector from every row of this matrix.
	 */
	public void sub(AVector v) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).sub(v);
		}		
	}
	
	@Override
	public final void scaleAdd(double factor, double constant) {
		multiply(factor);
		add(constant);
	}
	
	@Override
	public void scaleAdd(double factor, INDArray b, double bfactor, double constant) {
		scaleAdd(factor,constant);
		addMultiple(b,bfactor);
	}
	
	@Override
	public void multiply(double factor) {
		int rc=rowCount();

		for (int i=0; i<rc; i++) {
			getRowView(i).multiply(factor);
		}
	}	
	
	@Override
	public AMatrix multiplyCopy(double factor) {
		AMatrix r=clone();
		r.multiply(factor);
		return r;
	}	
	
	@Override
	public AMatrix divideCopy(double d) {
		AMatrix r = clone();
		r.multiply(1.0/d);
		return r;
	}

	/**
	 * Returns the sum of all elements in this matrix
	 * @param m
	 * @return 
	 */
	@Override
	public double elementSum() {
		int rc=rowCount();
		
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=getRow(i).elementSum();
		}
		return result;
	}
	
	/**
	 * Returns the squared sum of all elements in this matrix
	 * @param m
	 * @return 
	 */
	@Override
	public double elementSquaredSum() {
		int rc=rowCount();
		
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=getRow(i).elementSquaredSum();
		}
		return result;
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		return new MatrixElementIterator(this);
	}
	
	@Override
	public boolean isBoolean() {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).isBoolean()) return false;
		}
		return true;
	}	
	
	@Override
	public long nonZeroCount() {
		long result=0;
		int rc=rowCount();
		
		for (int i=0; i<rc; i++) {
			result+=getRow(i).nonZeroCount();
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
	
	/**
	 * Subtracts another matrix from this one, returning a new matrix
	 * @param m
	 */
	public AMatrix subCopy(AMatrix m) {
		AMatrix r=clone();
		r.addMultiple(m,-1.0);
		return r;
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
	public void multiply(AMatrix m) {
		int rc=rowCount();
		checkSameShape(m);
		
		for (int i=0; i<rc; i++) {
			getRowView(i).multiply(m.getRow(i));
		}
	}
	
	/**
	 * Divides this matrix in-place by another in an entrywise manner.
	 * @param m
	 */
	public void divide(AMatrix m) {
		int rc=rowCount();
		int cc=columnCount();
		m.checkShape(rc,cc);
		
		for (int i=0; i<rc; i++) {
			getRowView(i).divide(m.getRow(i));
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
		if (src==dst) {
			getRowView(dst).scale(1.0+factor);
		} else {
			getRowView(dst).addMultiple(getRow(src), factor);			
		}
	}
	
	@Override
	public void addInnerProduct(INDArray a, INDArray b) {
		if (a instanceof AMatrix) {
			addInnerProduct((AMatrix)a,b);
		} else {
			super.addInnerProduct(a, b);
		}
	}
	
	@Override
	public final void addInnerProduct(AMatrix a, INDArray b) {
		if (b instanceof AMatrix) {
			addInnerProduct(a,(AMatrix)b);
		} else {
			super.addInnerProduct(a, b);
		}
	}
	
	/**
	 * Adds the inner product (matrix multiply) of two matrices to this matrix
	 */
	public void addInnerProduct(AMatrix a, AMatrix b) {
		add(a.innerProduct(b));
	}

	@Override
	public void addOuterProduct(INDArray a, INDArray b) {
		if (a instanceof AVector && b instanceof AVector) {
			addOuterProduct((AVector)a,(AVector)b);
		} else {
			super.addOuterProduct(a, b);
		}		
	}
	
	@Override
	public void addOuterProductSparse(INDArray a, INDArray b) {
		if (a instanceof AVector && b instanceof AVector) {
			addOuterProductSparse((AVector)a,(AVector)b);
		} else {
			super.addOuterProductSparse(a, b);
		}		
	}
	
	/**
	 * Adds the outer product of two vectors to this matrix.
	 * Mutates only the non-sparse elements in this matrix
	 * @param a
	 * @param b
	 */
	public void addOuterProductSparse(AVector a, AVector b) {
		int rc=a.length();
		int cc=b.length();
		checkShape(rc, cc);
		for (int i=0; i<rc; i++) {
			getRowView(i).addMultipleSparse(b,a.get(i));
		}
	}

	/**
	 * Adds the outer product of two vectors to this matrix
	 * @param a
	 * @param b
	 */
	public void addOuterProduct(AVector a, AVector b) {
		int rc=rowCount();
		a.checkLength(rc);
		for (int i=0; i<rc; i++) {
			getRowView(i).addMultiple(b, a.unsafeGet(i));;
		}	
	}
	
	@Override
	public void addSparse(INDArray a) {
		if (a instanceof AMatrix) {
			addSparse((AMatrix)a);
			return;
		} 
		int adims=a.dimensionality();
		if (adims==0) {
			addSparse(a.get());
		} else if (adims==1){
			addSparse(a.asVector());
		} else {
			super.addSparse(a);
		}
	}
	
	/**
	 * Adds the corresponding elements of the source vector to the non-sparse elements in rows of this matrix
	 * @param a
	 */
	public void addSparse(AVector a) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).addSparse(a);
		}
	}
	
	
	@Override
	public final void setSparse(INDArray a) {
		int dims=a.dimensionality();
		if (dims==0) {
			setSparse(a.get());
		} else if (dims==1) {
			setSparse(a.asVector());
		} else if (dims==2) {
			setSparse(Matrixx.toMatrix(a));
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(a, this));
		}
	}
	
	/**
	 * Sets the non-sparse elements in this matrix to the corresponding values in the source matrix
	 * @param a
	 */
	public void setSparse(AMatrix a) {
		set(a);
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
		Vectorz.swap(a, b);
	}

	/**
	 * Swaps two columns of the matrix in place
	 */
	public void swapColumns(int i, int j) {
		if (i == j)
			return;
		AVector a = getColumnView(i);
		AVector b = getColumnView(j);
		Vectorz.swap(a, b);
	}
	
	public void composeWith(AMatrix a) {
		AMatrix t=innerProduct(a);
		this.set(t);
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public void addMultiple(INDArray src, double factor) {
		int srcDims=src.dimensionality();
		if (srcDims==0) {
			add(factor*src.get());
		} else if (srcDims==1) {
			addMultiple(src.asVector(),factor);
		} else {
			addMultiple(src.broadcastLike(this),factor);
		}
	}
	
	/**
	 * Adds a scalar multiple of a vector to every row of this matrix
	 * @param m
	 * @param factor
	 */
	public void addMultiple(AVector v, double factor) {
		if (factor==0.0) return;
		
		int rc=rowCount();
		int cc=columnCount();
		v.checkLength(cc);
		
		for (int i=0; i<rc; i++) {
			getRowView(i).addMultiple(v, factor);
		}
	}
	
	/**
	 * Adds a scalar multiple of another matrix to this matrix
	 * @param m
	 * @param factor
	 */
	public void addMultiple(AMatrix m, double factor) {
		if (factor==0.0) return;
		
		int rc=rowCount();
		int cc=columnCount();
		m.checkShape(rc, cc);
		
		for (int i=0; i<rc; i++) {
			getRowView(i).addMultiple(m.getRow(i), factor);
		}
	}
	
	/**
	 * Returns an iterator over rows in this Matrix
	 */
	@Override
	public Iterator<AVector> iterator() {
		return new MatrixRowIterator(this);
	}
		
	@Override
	public final boolean epsilonEquals(INDArray a, double epsilon) {
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
		if (a==this) return true;
		if (a instanceof ADenseArrayMatrix) return a.equals(this);	
		if (!a.isSameShape(this)) return false;
		
		return equalsByRows(a);		
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		int rc = rowCount();
		int cc = columnCount();
		for (int i = 0; i < rc; i++) {
			if (!getRow(i).equalsArray(data,offset+i*cc)) return false;
		}
		return true;
	}
	
	@Override
	public boolean elementsEqual(double value) {
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			if (!getRow(i).elementsEqual(value)) return false;
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
		if (!isSameShape(v)) return false;
		int rc=rowCount();
		for (int i = 0; i < rc; i++) {
			if (!getRow(i).equals(v.slice(i))) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this matrix is approximately equal to 
	 * a second matrix, up to a default tolerance level
	 */
	public final boolean epsilonEquals(AMatrix a) {
		return epsilonEquals(a,Vectorz.TEST_EPSILON);
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

	/**
	 * Internal method to test for equality in a row-wise basis. Assumes row counts are already proven equal.
	 */
	protected boolean equalsByRows(AMatrix m) {
		int rc = rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).equals(m.getRow(i))) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		if (elementCount()>Constants.PRINT_THRESHOLD) {
			Index shape=Index.create(getShape());
			return "Large matrix with shape: "+shape.toString();
		}
		
		return toStringFull();
	}

	@Override
	public String toStringFull() {
		StringBuilder sb = new StringBuilder();
		int rc = rowCount();
		sb.append("[");
		for (int i = 0; i < rc; i++) {
			if (i>0) sb.append(",\n");
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

		return new MatrixViewVector(this);
	}
	
	@Override
	public List<Double> asElementList() {
		return asVector().asElementList();
	}
	
	@Override
	public AMatrix innerProduct(AMatrix a) {
		return Multiplications.multiply(this, a);
	}
	
	@Override
	public Vector innerProduct(Vector v) {
		int cc=this.columnCount();
		int rc=this.rowCount();
		v.checkLength(cc);
		Vector r=Vector.createLength(rc);
		for (int i=0; i<rc; i++) {
			r.unsafeSet(i,rowDotProduct(i,v));
		}
		return r;
	}
	
	@Override
	public AVector innerProduct(AVector v) {
		int cc=this.columnCount();
		int rc=this.rowCount();
		v.checkLength(cc);
		Vector r=Vector.createLength(rc);
		for (int i=0; i<rc; i++) {
			r.unsafeSet(i,rowDotProduct(i,v));
		}
		return r;
	}
	
	@Override
	public final AMatrix innerProduct(AScalar s) {
		return innerProduct(s.get());
	}
	
	@Override
	public final AMatrix innerProduct(double d) {
		return multiplyCopy(d);
	}	
	
	public AMatrix transposeInnerProduct(AMatrix s) {
		if (s instanceof Matrix) return transposeInnerProduct((Matrix)s);
		if (isSparse()) {
			// TODO: should revisit: is there a better way?
			AMatrix t= getTranspose();
			if (t instanceof TransposedMatrix) t=t.sparseClone();
			return t.innerProduct(s);
		} else {
			Matrix t= toMatrixTranspose();
			return t.innerProduct(s);			
		}
	}
	
	public AMatrix transposeInnerProduct(Matrix s) {
		Matrix r= toMatrixTranspose();
		return Multiplications.multiply(r, s);
	}
	
	@Override
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AVector) {
			return innerProduct((AVector)a);
		} else if (a instanceof AMatrix) {
			return innerProduct((AMatrix) a);
		} else if (a instanceof AScalar) {
			return innerProduct((AScalar)a);
		} else if (a.dimensionality()<=2) {
			return innerProduct(Arrayz.create(a)); // convert to most efficient format
		}
		int rc=rowCount();
		List<AVector> al=getRows();
		List<INDArray> rl=new ArrayList<INDArray>(rc);
		for (AVector v: al ) {
			rl.add(v.innerProduct(a));
		}
		return SliceArray.create(rl);
	}

	@Override
	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>(sliceCount());
		for (AVector s:this) {
			al.add(s.outerProduct(a));
		}
		return Arrayz.create(al);
	}
	
	/**
	 * Returns the dot product of a specific row with a vector.
	 * 
	 * Unsafe operation: performs no bounds checking
	 * @param i
	 * @param a
	 * @return
	 */
	public double rowDotProduct(int i, AVector a) {
		return getRow(i).dotProduct(a);
	}

	/**
	 * Computes the inverse of a matrix. Returns null if the matrix is singular.
	 * 
	 * Throws an Exception is the matrix is not square
	 * @param m
	 * @return
	 */
	@Override
	public AMatrix inverse() {
		return Inverse.calculate(this);
	}
	
	@Override
	public double trace() {
		int rc=Math.min(rowCount(), columnCount());
		double result=0.0;
		for (int i=0; i<rc; i++) {
			result+=unsafeGet(i,i);
		}
		return result;
	}
	
	/**
	 * Computes the product of entries on the main diagonal of a matrix
	 * 
	 * @return
	 */
	@Override
	public double diagonalProduct() {
		int rc=Math.min(rowCount(), columnCount());
		double result=1.0;
		for (int i=0; i<rc; i++) {
			result*=unsafeGet(i,i);
		}
		return result;
	}
	
	@Override
	public boolean isInvertible() {
		return isSquare()&&(determinant()!=0.0);
	}

	@Override
	public Vector toVector() {
		int rc = rowCount();
		int cc = columnCount();
		Vector v = Vector.createLength(rc * cc);
		this.getElements(v.getArray(),0);
		return v;
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
		double[] result=DoubleArrays.createStorage(rowCount(),columnCount());
		getElements(result,0);
		return result;
	}
	
	@Override
	public double[][] toNestedDoubleArrays() {
		int rc=rowCount();
		double[][] result=new double[rc][];
		int i=0;
		for (AVector row: getSlices()) {
			result[i++]=row.toDoubleArray();
		}
		return result;
	}
	
	@Override
	public AVector[] toSliceArray() {
		int n=sliceCount();
		AVector[] result=new AVector[n];
		for (int i=0; i<n; i++) {
			result[i]=slice(i);
		}
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
	public final void setApplyOp(Op op, INDArray a) {
		if (a instanceof AMatrix) {
			setApplyOp(op,(AMatrix)a);
		} else {
			super.setApplyOp(op, a);
		}
	}
	
	/**
	 * Sets this matrix to the result of applying an operator to another matrix
	 * @param op
	 * @param a
	 */
	public void setApplyOp(Op op, AMatrix a) {
		checkSameShape(a);
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).setApplyOp(op,a.getRow(i));
		}
	}
	
	@Override
	public AMatrix applyOpCopy(Op op) {
		AMatrix r=clone();
		r.applyOp(op);
		return r;
	}
	
	@Override
	public void applyOp(Op2 op, INDArray b) {
		int dims=b.dimensionality();
		if (dims==0) {
			applyOp(op,b.get());
		} else if (dims==1) {
			applyOp(op,b.asVector());
		} else {
			applyOp(op,b.broadcastLike(this));			
		}
	}
	
	public void applyOp(Op2 op, AMatrix b) {
		checkSameShape(b);
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).applyOp(op,b.getRow(i));
		}
	}
	
	public void applyOp(Op2 op, AVector b) {
		b.checkLength(columnCount());
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).applyOp(op,b);
		}
	}

	@Override
	public void applyOp(Op2 op, double b) {
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).applyOp(op,b);
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
	
	@Override
	public double reduce(Op2 op, double init) {
		int rc=rowCount();
		double result=init;
		for (int i=0; i<rc; i++) {
			result=getRow(i).reduce(op, result);
		}
		return result;
	}
	
	@Override
	public double reduce(Op2 op) {
		int rc=rowCount();
		double result=getRow(0).reduce(op);
		for (int i=1; i<rc; i++) {
			result=getRow(i).reduce(op, result);
		}
		return result;
	}
	
	@Override
	public AVector reduceSlices(Op2 op) {
		int rc=rowCount();
		AVector result=Vector.createLength(rc);
		for (int i=0; i<rc; i++) {
			result.unsafeSet(i, getRow(i).reduce(op));
		}
		return result;
	}
	
	@Override
	public AVector reduceSlices(Op2 op, double init) {
		int rc=rowCount();
		AVector result=Vector.createLength(rc);
		for (int i=0; i<rc; i++) {
			result.unsafeSet(i, getRow(i).reduce(op,init));
		}
		return result;
	}
	
	@Override
	public void add(INDArray a) {
		if (a instanceof AMatrix) {
			add((AMatrix)a);
		} else if (a instanceof AVector) {
			add((AVector)a);
		} else {
			int dims=a.dimensionality();
			if (dims>2) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
			if (dims==0) {
				add(a.get());
			} else if (dims==1) {
				add(Vectorz.toVector(a));
			} else if (dims==2) {
				add(Matrixx.toMatrix(a));
			}
		}
	}
	
	/**
	 * Multiply each row in this matrix by a vector. Mutates this matrix.
	 * @param v
	 */
	public void multiply(AVector v) {
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).multiply(v);
		}
	}
	
	@Override
	public void multiply(INDArray a) {
		if (a instanceof AMatrix) {
			multiply((AMatrix)a);
		} else if (a instanceof AVector) {
			multiply((AVector)a);
		} else{
			int dims=a.dimensionality();
			if (dims==0) {
				multiply(a.get());
			} else if (dims==1) {
				multiply(Vectorz.toVector(a));
			} else if (dims==2) {
				multiply(Matrixx.toMatrix(a));
			} else {
				throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this,a));
			}
		}
	}
	
	/**
	 * Divides every row of this matrix by the given vector
	 * @param v
	 */
	public void divide(AVector v) {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			getRowView(i).divide(v);
		}
	}
	
	@Override
	public void divide(INDArray a) {
		if (a instanceof AMatrix) {
			divide((AMatrix)a);
		} else if (a instanceof AVector) {
			divide((AVector)a);
		} else {
			int dims=a.dimensionality();
			int rc=rowCount();
			if (dims==0) {
				divide(a.get());
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
		} else {
			int dims=a.dimensionality();
			if (dims==0) {
				sub(a.get());
			} else if (dims==1) {
				sub(Vectorz.toVector(a));
			} else if (dims==2) {
				sub(Matrixx.toMatrix(a));
			}
		}
	}

	@Override
	public void add(double d) {
		if (d==0.0) return;
		int rc = rowCount();
		for (int i = 0; i < rc; i++) {
			getRowView(i).add(d);
		}
	}

	@Override
	public void addAt(int i, int j, double d) {
		unsafeSet(i,j,unsafeGet(i,j)+d);
	}
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int tdims=targetShape.length;
		if (tdims<2) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));				
		} else if (2==tdims) {
			checkShape(targetShape[0],targetShape[1]);
			return this;			
		} else {
			checkShape(targetShape[tdims-2],targetShape[tdims-1]);
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,targetShape[0]);
		}
	}
	
	@Override
	public INDArray broadcastLike(INDArray target) {
		if (target instanceof AMatrix) {
			return broadcastLike((AMatrix)target);
		}
		return broadcast(target.getShape());
	}
	
	@Override
	public AMatrix broadcastLike(AMatrix target) {
		checkSameShape(target);
		return this;
	}
	
	@Override
	public INDArray broadcastCloneLike(INDArray target) {
		INDArray r=this;
		if (target.dimensionality()>2) r=r.broadcastLike(target);
		return r.clone();
	}
	
	@Override
	public INDArray broadcastCopyLike(INDArray target) {
		INDArray r=this.copy();
		if (target.dimensionality()>2) r=r.broadcastLike(target);
		return r;
	}

	/**
	 * Returns true if the matrix is the zero matrix (all components zero)
	 */
	@Override
	public boolean isZero() {
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			if (!getRow(i).isZero()) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is positive definite
	 */
	public boolean isPositiveDefinite() {
		return Definite.isPositiveDefinite(this);
	}
	
	@Override
	public boolean isDiagonal() {
		int rc=rowCount();
		int cc=columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			AVector r=getRow(i);
			if (!r.isRangeZero(0, i-1)) return false;
			if (!r.isRangeZero(i+1, cc-i-1)) return false;
		}
		return true;
	}
	
	@Override
	public boolean isSameShape(INDArray a) {
		if (a instanceof AMatrix) return isSameShape((AMatrix)a);
		if (a.dimensionality()!=2) return false;
		if (getShape(0)!=a.getShape(0)) return false;
		if (getShape(1)!=a.getShape(1)) return false;
		return true;
	}
	
	/**
	 * Returns true iff this matrix is exactly the same shape as another matrix
	 * @param a
	 * @return
	 */
	public boolean isSameShape(AMatrix a) {
		return (this.rowCount()==a.rowCount())&&(this.columnCount()==a.columnCount());
	}
	
	@Override
	public boolean isRectangularDiagonal() {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			AVector r=getRow(i);
			if (i<cc) {
				if (!r.isRangeZero(0, i-1)) return false;
				if (!r.isRangeZero(i+1, cc-i-1)) return false;
			} else {
				if (!r.isZero()) return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is symmetric
	 */
	@Override
	public boolean isSymmetric() {
		int rc=rowCount();
		int cc=columnCount();
		if (rc!=cc) return false;
		for (int i=0; i<rc; i++) {
			if (!getRow(i).equals(getColumn(i))) return false;
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
	 * 
	 * An upper triangular matrix is defined as having all elements equal to 0.0 where i > j
	 */
	public boolean isUpperTriangular() {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=1; i<rc; i++) {
			if (!getRow(i).isRangeZero(0, Math.min(i,cc))) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if a matrix is lower triangular.
	 * 
	 * A lower triangular matrix is defined as having all elements equal to 0.0 where i < j
	 */
	public boolean isLowerTriangular() {
		int rc=rowCount();
		int cc=columnCount();
		for (int i=0; i<rc; i++) {
			int start=Math.min(cc, i+1);
			if (!getRow(i).isRangeZero(start,cc-start)) return false;
		}
		return true;
	}
	
	/**
	 * A limit on the upper bandwidth of the banded matrix. Actual upper bandwidth is guaranteed
	 * to be less than or equal to this value. Implementation should be O(1).
	 * @return
	 */
	public int upperBandwidthLimit() {
		return columnCount()-1;
	}
	
	/**
	 * A limit on the lower bandwidth of the banded matrix. Actual lower bandwidth is guaranteed
	 * to be less than or equal to this value. Implementation should be O(1).
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
	public final int bandIndex(int i, int j) {
		return j-i;
	}
	
	/**
	 * Returns the band position for a specified (i,j) index in the matrix.
	 * @param i
	 * @param j
	 * @return
	 */
	public final int bandPosition(int i, int j) {
		return Math.min(i, j);
	}
	
	/**
	 * Computes the upper bandwidth of a matrix, i.e. the number of bands above the leading diagonal
	 * that cover all non-zero values
	 * @return
	 */
	public int upperBandwidth() {
		for (int w=upperBandwidthLimit(); w>0; w--) {
			if (!getBand(w).isZero()) return w;
		}
		return 0;
	}
	
	/**
	 * Computes the lower bandwidth of a matrix, i.e. the number of bands below the leading diagonal
	 * that cover all non-zero values
	 * @return
	 */
	public int lowerBandwidth() {
		for (int w=lowerBandwidthLimit(); w>0; w--) {
			if (!getBand(-w).isZero()) return w;
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
	 * Sets a row in a matrix to the value specified by the given vector
	 * 
	 * @param i
	 * @param row
	 */
	public void setRow(int i, AVector row) {
		getRowView(i).set(row);
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
		getColumnView(i).set(col);
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
	public AMatrix dense() {
		if (this instanceof IDense) return this;
		return Matrix.create(this);
	}
	
	@Override
	public final Matrix denseClone() {
		return Matrix.create(this);
	}
	
	@Override 
	public void validate() {
		if (((long)rowCount())*columnCount()!=elementCount()) throw new VectorzException("Invalid Array shape?");
		super.validate();
	}

	/**
	 * Copies the elements in a selected row of this matrix to a double array
	 * @param i The index of the selected row
	 * @param dest Destination double[] array
	 * @param destOffset Offset into destination array
	 */
	public void copyRowTo(int i, double[] dest, int destOffset) {
		// note: using getRow() may be faster when overriding
		int cc=columnCount();
		for (int j=0; j<cc; j++) {
			dest[destOffset+j]=unsafeGet(i,j);
		}
	}
	
	/**
	 * Copies the elements in a selected row of this matrix to a double array
	 * @param i The index of the selected column
	 * @param dest Destination double[] array
	 * @param destOffset
	 */
	public void copyColumnTo(int j, double[] dest, int destOffset) {
		// note: using getColumn() may be faster when overriding
		int rc=rowCount();
		for (int i=0; i<rc; i++) {
			dest[destOffset+i]=unsafeGet(i,j);
		}
	}
	
	@Override
	public final INDArray addCopy(INDArray a) {
		if (a instanceof AMatrix) {
			return addCopy((AMatrix)a);
		} 
		switch(a.dimensionality()) {
			case 0: return addCopy(a.get());
			case 1: return addCopy(a.asVector());
			default: {
				INDArray r=this.broadcastCloneLike(a);
				r.add(a);
				return r;
			}
		}
	}

	@Override
	public AMatrix addCopy(AMatrix a) {
		AMatrix result=a.clone();
		result.add(this);
		return result;
	}
	
	@Override
	public AMatrix addCopy(AVector v) {
		int rc=this.rowCount();
		AMatrix result=this.clone();
		for (int i=0; i<rc; i++) {
			result.getRowView(i).add(v);
		}
		return result;
	}
	
	@Override
	public AMatrix addCopy(double d) {
		AMatrix result=clone();
		result.add(d);
		return result;
	}
	
	/**
     * Adds a dense matrix to this matrix, returning a new dense Matrix
     *
     * @param m A matrix. Not modified.
     * @return True if any element in the matrix is NaN of Infinite.
     */
	public Matrix addCopy(AStridedMatrix a) {
		checkSameShape(a);
		Matrix result=a.clone();
		this.addToArray(result.data,0);
		return result;
	}

	/**
     * Checks to see if any element in the matrix is NaN of Infinite.
     *
     * @param m A matrix. Not modified.
     * @return True if any element in the matrix is NaN of Infinite.
     */
	@Override
	public boolean hasUncountable() {
		int rc = rowCount();
		for(int i=0; i<rc; i++) {
			if (getRow(i).hasUncountable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a matrix is square. Returns the size if true, throws an exception otherwise;
	 * @return
	 */
	public int checkSquare() {
		int rc=rowCount();
		if (rc!=columnCount()) throw new UnsupportedOperationException(ErrorMessages.nonSquareMatrix(this));
		return rc;
	}
	
	/**
	 * Checks if this matrix has the expected number of rows.
	 * Throws an exception if not.
	 * @param expected
	 * @return The actual number of rows in this matrix
	 */
	protected int checkRowCount(int expected) {
		int rc=rowCount();
		if (rc!=expected) throw new IllegalArgumentException("Unexpected row count: "+rc+" expected: "+expected);
		return rc;
	}
	
	/**
	 * Checks if this matrix has the expected number of columns.
	 * Throws an exception if not.
	 * @param expected
	 * @return The actual number of columns in this matrix
	 */
	protected int checkColumnCount(int expected) {
		int cc=columnCount();
		if (cc!=expected) throw new IllegalArgumentException("Unexpected column count: "+cc+" expected: "+expected);
		return cc;
	}
	
	@Override
	protected final void checkDimension(int dimension) {
		if ((dimension < 0) || (dimension >= 2))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this,dimension));
	}
	
	/**
	 * Checks if this matrix has the same shape as another matrix. Throws an exception if not.
	 * @param m
	 */
	protected void checkSameShape(AMatrix m) {
		if((rowCount()!=m.rowCount())||(columnCount()!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	/**
	 * Checks if this matrix has the same shape as another matrix. Throws an exception if not.
	 * @param m
	 */
	protected void checkSameShape(ARectangularMatrix m) {
		if((rowCount()!=m.rowCount())||(columnCount()!=m.columnCount())) {
			throw new IndexOutOfBoundsException(ErrorMessages.mismatch(this, m));
		}
	}
	
	/**
	 * Checks if this matrix has the specified shape. Throws an exception if not.
	 */
	protected void checkShape(int rows, int cols) {
		int rc=rowCount();
		int cc=columnCount();
		if((rc!=rows)||(cc!=cols)) {
			throw new IllegalArgumentException("Unexpected shape: ["+cc+","+rc+"] expected: ["+rows+","+cols+"]");
		}
	}
	
	/**
	 * Checks if the given index is valid for this matrix. Throws an exception if not.
	 */
	protected void checkIndex(int i, int j) {
		int rc=rowCount();
		int cc=columnCount();
		if ((i<0)||(i>=rc)||(j<0)||(j>=cc)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i,j));
		}
	}
	
	/**
	 * Checks if the given column index is valid for this matrix. Throws an exception if not.
	 *
	 * @return the number of columns in this matrix
	 */
	public int checkColumn(int column) {
		int cc=columnCount();
		if ((column<0)||(column>=cc)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 1, column));
		return cc;
	}
	
	/**
	 * Checks if the given rows index is valid for this matrix. Throws an exception if not.
	 * 
	 * @return the number of rows in this matrix
	 */
	public int checkRow(int row) {
		int rc=rowCount();
		if ((row<0)||(row>=rc)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, 0, row));
		return rc;
	}

	@Override
	public void add2(AMatrix a, AMatrix b) {
		add(a);
		add(b);
	}


}
