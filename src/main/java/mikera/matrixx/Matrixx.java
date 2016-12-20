package mikera.matrixx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mikera.arrayz.INDArray;
import mikera.indexz.Index;
import mikera.matrixx.impl.ADiagonalMatrix;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.ColumnMatrix;
import mikera.matrixx.impl.DenseColumnMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ScalarMatrix;
import mikera.matrixx.impl.SparseColumnMatrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.StridedRowMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

/**
 * Static method class for matrices
 * 
 * @author Mike
 */
public class Matrixx {

	private static final long SPARSE_ELEMENT_THRESHOLD = 100000;
	
	private final static Random rand=new Random();

	/**
	 * Creates an identity matrix
	 */
	public static AMatrix createIdentityMatrix(int dimensions) {
		return createImmutableIdentityMatrix(dimensions);
	}

	/**
	 * Creates a sparse, immutable identity matrix. This is the most efficient format for identity matrices
	 */
	public static IdentityMatrix createImmutableIdentityMatrix(int dimensions) {
		return IdentityMatrix.create(dimensions);
	}
	
	/**
	 * Creates a fully mutable identity matrix
	 */
	public static AMatrix createMutableIdentityMatrix(int dimensions) {
		AMatrix m = newMatrix(dimensions, dimensions);
		for (int i = 0; i < dimensions; i++) {
			m.unsafeSet(i, i, 1.0);
		}
		return m;
	}
	
	/**
	 * Coerce an object to a matrix format, on a best effort basis.
	 * 
	 * Can handle:
	 * - Existing matrices
	 * - Vectors (will be broadcast to a n x 1 column matrix)
	 * - Scalars (will be broadcast to a 1 x 1 matrix)
	 */
	public static AMatrix toMatrix(INDArray a) {
		if (a instanceof AMatrix) return (AMatrix)a;
		int dims=a.dimensionality();
		if (dims==0) return Matrix.wrap(1, 1, new double[]{a.get()});
		
		if (dims==1) {
			if (a instanceof AVector) return ColumnMatrix.wrap(((AVector)a).clone());
			return ColumnMatrix.wrap(a.toVector());
		}
		if (dims==2) return Matrix.create(a);
		throw new UnsupportedOperationException("Can't convert to matrix: "
				+ a.getClass() + " with shape " +a.getShape());
	}

	/**
	 * Coerce an object to a matrix format, on a best effort basis.
	 * 
	 * Can handle:
	 * - INDArray instances of suitable shape (will be broadcast to a matrix if needed)
	 * - Iterable objects representing a list of rows
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static AMatrix toMatrix(Object o) {
		if (o instanceof INDArray) {
			return toMatrix((INDArray)o);
		} else if (o instanceof Iterable<?>) {
			List<INDArray> al = Tools.toList((Iterable<INDArray>) o);
			return createFromVectors(al);
		}
		throw new UnsupportedOperationException("Can't convert to matrix: "
				+ o.getClass());
	}

	/**
	 * Creates a sparse matrix from the given matrix, ignoring zeros
	 */
	public static AMatrix createSparse(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		if ((rc==0)||(cc==0)) return ZeroMatrix.create(rc, cc);
		return SparseRowMatrix.create(m);
	}
	
	/**
	 * Creates a sparse matrix of the given size, initially zero-filled. Uses row-based storage by default
	 */
	public static SparseRowMatrix createSparse(int rowCount, int columnCount) {
		return SparseRowMatrix.create(rowCount,columnCount);
	}
	
	/**
	 * Creates a sparse matrix from the given matrix, ignoring zeros. Uses row-based storage by default
	 */
	public static SparseRowMatrix createSparseRows(Iterable<AVector> rows) {
		Iterator<AVector> rowIterator=rows.iterator();
		return createSparseRows(rowIterator);
	}
	
	/**
	 * Creates a sparse matrix from the given iterator. Each vector in the iterator will be copied to
	 * a row in the new sparse matrix 
	 */
	public static ArrayList<AVector> createSparseArray(Iterator<AVector> vecIterator) {
		AVector v0=vecIterator.next();
		int len = v0.length();
		ArrayList<AVector> vecList = new ArrayList<AVector>();
		vecList.add(v0);
		while (vecIterator.hasNext()) {
			AVector v = vecIterator.next();
            if ((v == null) || (v.isZero()))
                v = Vectorz.createZeroVector(len);
            else
			    v = v.sparseClone();
			vecList.add(v.sparseClone());
		}
        return vecList;
	}

	public static SparseRowMatrix createSparseRows(Iterator<AVector> rowIterator) {
		return SparseRowMatrix.wrap(createSparseArray(rowIterator));
    }
	
	public static SparseColumnMatrix createSparseColumns(Iterator<AVector> colIterator) {
		return SparseColumnMatrix.wrap(createSparseArray(colIterator));
    }
	
	/**
	 * Create a sparse array, given an Index of column positions and AVector of corresponding values for each row in the sparse array
	 * Performs a defensive copy of underlying data.
	 */
	public static AMatrix createSparse(int columnCount, Index[] indexes,
			AVector[] weights) {
		int rowCount = indexes.length;
		if (rowCount != weights.length)
			throw new IllegalArgumentException("Length of indexes array must match length of weights array");
		SparseRowMatrix sm=SparseRowMatrix.create(rowCount, columnCount);
		for (int i = 0; i < rowCount; i++) {
			sm.replaceRow(i, SparseIndexedVector.wrap(columnCount, indexes[i].clone(), weights[i].toDoubleArray()));
		}
		return sm;
	}

	/**
	 * Creates a SparseColumnMatrix from the given matrix, ignoring zeros
	 */
	public static SparseColumnMatrix createSparseColumns(AMatrix m) {
		int cc = m.columnCount();
		AVector[] cols = new AVector[cc];
		for (int i = 0; i < cc; i++) {
			cols[i] = Vectorz.createSparse(m.getColumn(i));
		}
		return SparseColumnMatrix.wrap(cols);
	}
	
	/**
	 * Creates a SparseRowMatrix matrix from the given matrix, ignoring zeros
	 */
	public static AMatrix createSparseRows(AMatrix m) {
		if (m.rowCount()==0) return ZeroMatrix.create(0, m.columnCount());
		return SparseRowMatrix.create(m);
	}
	
	/**
	 * Creates a SparseRowMatrix matrix from the given INDArray, ignoring zeros
	 */
	public static SparseRowMatrix createSparseRows(INDArray a) {
		if (!(a.dimensionality()==2)) throw new IllegalArgumentException(ErrorMessages.incompatibleShape(a));
		int rc=a.getShape(0);
		int cc=a.getShape(1);
		SparseRowMatrix m=SparseRowMatrix.create(rc,cc);
		for (int i=0; i<rc; i++) {
			AVector v=a.slice(i).sparseClone().asVector();
			if (!v.isZero()) {
				m.replaceRow(i, v);
			}
		}
		return m;
	}

	/**
	 * Creates an immutable zero-filled matrix
	 */
	public static ZeroMatrix createImmutableZeroMatrix(int rows, int columns) {
		return ZeroMatrix.create(rows, columns);
	}

	/**
	 * Creates a diagonal scaling matrix (same factor on every element of main diagonal, 0.0 elsewhere)
	 * 
	 * Mutable only on main diagonal.
	 * 
	 * @param dimensions
	 * @param factor
	 * @return
	 */
	public static ADiagonalMatrix createScaleMatrix(int dimensions,
			double factor) {
		DiagonalMatrix im = new DiagonalMatrix(dimensions);
		for (int i = 0; i < dimensions; i++) {
			im.unsafeSet(i, i, factor);
		}
		return im;
	}
	
	/**
	 * Creates a scalar matrix with the given scale factor. Scalar matrices are efficient,
	 * lightweight and immutable.
	 */
	public static ADiagonalMatrix createScalarMatrix(int dimensions,
			double factor) {
		if (factor==1.0) return IdentityMatrix.create(dimensions);
		return ScalarMatrix.create(dimensions, factor);
	}

	/**
	 * Creates a diagonal scaling matrix with the given scale factors for each dimension
	 */
	public static DiagonalMatrix createScaleMatrix(double... scalingFactors) {
		int dimensions = scalingFactors.length;
		DiagonalMatrix im = new DiagonalMatrix(dimensions);
		for (int i = 0; i < dimensions; i++) {
			im.unsafeSet(i, i, scalingFactors[i]);
		}
		return im;
	}

	/**
	 * Creates a diagonal matrix, using the given diagonal values. Performs a defensive copy of the data.
	 */
	public static DiagonalMatrix createDiagonalMatrix(double... diagonalValues) {
		int dimensions = diagonalValues.length;
		DiagonalMatrix im = new DiagonalMatrix(dimensions);
		im.getLeadingDiagonal().setElements(diagonalValues);
		return im;
	}
	
	/**
	 * Creates a diagonal matrix using the given vector of values on the main diagonal.
	 */
	public static DiagonalMatrix createDiagonalMatrix(AVector diagonalValues) {
		return DiagonalMatrix.wrap(diagonalValues.toDoubleArray());
	}

	/**
	 * Creates a 3D rotation matrix for a given angle or rotation in radians around an axis vector.
	 * The axis vector need not be normalised.
	 */
	public static Matrix33 createRotationMatrix(Vector3 axis, double angle) {
		return createRotationMatrix(axis.x, axis.y, axis.z, angle);
	}

	/**
	 * Create a 3x3 rotation matrix using the given rotation axis and angle in radians
	 * @param angle Rotation angle in radians
	 * @param x x-element of rotation vector
	 * @param y y-element of rotation vector
	 * @param z z-element of rotation vector
	 */
	public static Matrix33 createRotationMatrix(double x, double y, double z,
			double angle) {
		double d = Math.sqrt(x * x + y * y + z * z);
		if (d==0.0) return Matrix33.createIdentityMatrix();
		double ca = Math.cos(angle);
		double u=x, v=y, w=z;
		if (d!=1.0) {
			double s=1.0/d;
			u = x *s;
			v = y *s;
			w = z *s;
		}
		double sa = Math.sin(angle);
		return new Matrix33(u * u + (1 - u * u) * ca,
				u * v * (1 - ca) - w * sa, u * w * (1 - ca) + v * sa, u * v
						* (1 - ca) + w * sa, v * v + (1 - v * v) * ca, v * w
						* (1 - ca) - u * sa, u * w * (1 - ca) - v * sa, v * w
						* (1 - ca) + u * sa, w * w + (1 - w * w) * ca);
	}

	/**
	 * Create a 3x3 rotation matrix using the given rotation axis and angle in radians
	 * @param angle Rotation angle in radians
	 * @param v Rotation axis vector
	 */
	public static Matrix33 createRotationMatrix(AVector v, double angle) {
		if (!(v.length() == 3))
			throw new VectorzException(
					"Rotation matrix requires a 3d axis vector");
		return createRotationMatrix(v.unsafeGet(0), v.unsafeGet(1),
				v.unsafeGet(2), angle);
	}

	/**
	 * Create a 3x3 rotation matrix using the given rotation angle in radians around the x-axis
	 * @param angle Rotation angle in radians
	 */
	public static Matrix33 createXAxisRotationMatrix(double angle) {
		return createRotationMatrix(1, 0, 0, angle);
	}

	/**
	 * Create a 3x3 rotation matrix using the given rotation angle in radians around the y-axis
	 * @param angle Rotation angle in radians
	 */
	public static Matrix33 createYAxisRotationMatrix(double angle) {
		return createRotationMatrix(0, 1, 0, angle);
	}

	/**
	 * Create a 3x3 rotation matrix using the given rotation angle in radians around the z-axis
	 * @param angle Rotation angle in radians
	 */
	public static Matrix33 createZAxisRotationMatrix(double angle) {
		return createRotationMatrix(0, 0, 1, angle);
	}

	public static Matrix22 create2DRotationMatrix(double angle) {
		return Matrix22.createRotationMatrix(angle);
	}

	public static Matrix createRandomSquareMatrix(int dimensions) {
		Matrix m = createSquareMatrix(dimensions);
		fillRandomValues(m);
		return m;
	}
	
	public static Matrix createRandomSquareMatrix(int dimensions, Random rand) {
		Matrix m = createSquareMatrix(dimensions);
		fillRandomValues(m,rand);
		return m;
	}

	/**
	 * Creates a mutable matrix containing random double values in the range [0.0 , 1.0)
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static AMatrix createRandomMatrix(int rows, int columns) {
		AMatrix m = newMatrix(rows, columns);
		fillRandomValues(m);
		return m;
	}
	
	/**
	 * Creates a mutable matrix containing random double values in the range [0.0 , 1.0)
	 * @param rows
	 * @param columns
	 * @param rand A random number generator to use as the seed
	 * @return
	 */
	public static AMatrix createRandomMatrix(int rows, int columns, Random rand) {
		AMatrix m = newMatrix(rows, columns);
		fillRandomValues(m,rand);
		return m;
	}

	/**
	 * Creates an empty (zero-filled) mutable matrix of the specified size. Uses mutable primitive
	 * matrices for small sizes (1x1, 2x2, 3x3)
	 * 
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static AMatrix newMatrix(int rows, int columns) {
		if (rows==columns) {
			if (rows == 1) return new Matrix11();
			if (rows == 2) return new Matrix22();
			if (rows == 3) return new Matrix33();
		}
		if ((rows==0)||(columns==0)) return ZeroMatrix.create(rows, columns);
		if (rows*((long)columns)>SPARSE_ELEMENT_THRESHOLD) return createSparse(rows,columns);
		return Matrix.create(rows, columns);
	}

	/**
	 * Creates a new matrix using the elements in the specified vector.
	 * Truncates or zero-pads the data as required to fill the new matrix
	 * @param data
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static Matrix createFromVector(AVector source, int rows, int columns) {
		int length=source.length();
		Matrix m = Matrix.create(rows, columns);
		int n=Math.min(rows*columns, length);
		source.copyTo(0, m.data, 0, n);
		return m;
	}
	
	/**
	 * Computes the inverse of a matrix. Returns null if the matrix is singular.
	 * 
	 * Throws an Exception is the matrix is not square
	 * @param m
	 * @return
	 */
	public AMatrix createInverse(AMatrix m) {
		return m.inverse();
	}

	/**
	 * Creates a zero-filled matrix with the specified number of dimensions for both rows and columns
	 * @param dimensions
	 * @return
	 */
	private static Matrix createSquareMatrix(int dimensions) {
		return Matrix.create(dimensions, dimensions);
	}
	
	/**
	 * Extracts a lower triangular matrix from a matrix 
	 */
	public static AMatrix extractLowerTriangular(AMatrix a) {
		int rc=a.rowCount();
		if (rc>a.columnCount()) throw new IllegalArgumentException("Too few columns in matrix");
		AMatrix r=Matrixx.newMatrix(rc,rc);
		for (int i=0; i<rc; i++) {
			for (int j=0; j<=i; j++) {
				r.unsafeSet(i, j, a.unsafeGet(i, j));
			}
		}
		return r;
	}
	
	/**
	 * Extracts an upper triangular matrix from a matrix 
	 */
	public static AMatrix extractUpperTriangular(AMatrix a) {
		int cc=a.rowCount();
		if (cc>a.rowCount()) throw new IllegalArgumentException("Too few rows in matrix");
		AMatrix r=Matrixx.newMatrix(cc,cc);
		for (int i=0; i<cc; i++) {
			for (int j=i; j<cc; j++) {
				r.unsafeSet(i, j, a.unsafeGet(i, j));
			}
		}
		return r;
	}

	/**
	 * Creates a mutable deep copy of a matrix
	 */
	public static Matrix create(AMatrix m) {
		return new Matrix(m);
	}

	/**
	 * Create a matrix from a list of rows
	 * 
	 * @param rows
	 * @return
	 */
	public static Matrix create(List<Object> rows) {
		int rc = rows.size();
		AVector firstRow = Vectorz.create(rows.get(0));
		int cc = firstRow.length();

		Matrix m = Matrix.create(rc, cc);
		m.setRow(0, firstRow);

		for (int i = 1; i < rc; i++) {
			m.setRow(i, Vectorz.create(rows.get(i)));
		}
		return m;
	}

	/**
	 * Creates a mutable copy of a matrix
	 */
	public static AMatrix create(IMatrix m) {
		int rows = m.rowCount();
		int columns = m.columnCount();
		AMatrix result = newMatrix(rows, columns);
		result.set(m);
		return result;
	}

	/**
	 * Fills a matrix with uniform random numbers
	 * @param m
	 */
	public static void fillRandomValues(AMatrix m) {
		int rows = m.rowCount();
		int columns = m.columnCount();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				m.unsafeSet(i, j, rand.nextDouble());
			}
		}
	}
	
	/**
	 * Fills a matrix with uniform random numbers, using the specified Random instance
	 * @param m
	 */
	public static void fillRandomValues(AMatrix m, Random rand) {
		int rows = m.rowCount();
		int columns = m.columnCount();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				m.unsafeSet(i, j, rand.nextDouble());
			}
		}
	}

	/** 
	 * Create a matrix using as array of vectors which represent the data for each row
	 * @param data
	 * @return
	 */
	public static AMatrix createFromVectors(INDArray... data) {
		int rc = data.length;
		int cc = (rc == 0) ? 0 : data[0].sliceCount();
		AMatrix m = newMatrix(rc, cc);
		for (int i = 0; i < rc; i++) {
			m.setRow(i, data[i].asVector());
		}
		return m;
	}

	/** 
	 * Create a matrix using a list of vectors as the data for each row
	 * @param data
	 * @return
	 */
	public static AMatrix createFromVectors(List<INDArray> data) {
		int rc = data.size();
		int cc = (rc == 0) ? 0 : data.get(0).sliceCount();
		AMatrix m = newMatrix(rc, cc);
		for (int i = 0; i < rc; i++) {
			m.setRow(i,data.get(i).asVector());
		}
		return m;
	}

	// ====================================
	// Edn formatting and parsing functions

	private static Parser.Config getMatrixParserConfig() {
		return Parsers.defaultConfiguration();
	}

	/**
	 * Parse a matrix in edn format
	 * 
	 * @param ednString
	 * @return
	 */
	public static AMatrix parse(String ednString) {
		Parser p = Parsers.newParser(getMatrixParserConfig());
		Parseable ps = Parsers.newParseable(ednString);
		@SuppressWarnings("unchecked")
		List<List<Object>> data = (List<List<Object>>) p.nextValue(ps);
		int rc = data.size();
		int cc = (rc == 0) ? 0 : data.get(0).size();
		AMatrix m = newMatrix(rc, cc);
		for (int i = 0; i < rc; i++) {
			List<Object> row=data.get(i);
			for (int j = 0; j < cc; j++) {
				m.unsafeSet(i, j, Tools.toDouble(row.get(j)));
			}
		}
		return m;
	}

	/**
	 * Creates a matrix using the given objects to generate individual rows
	 * @param vs
	 * @return
	 */
	public static AMatrix create(Object... vs) {
		return create(Arrays.asList(vs));
	}

	/**
	 * Creates a matrix using the given double[][] data
	 * @param data
	 * @return
	 */
	public static Matrix create(double[][] data) {
		return Matrix.create(data);
	}

	/**
	 * Wraps double[] data in a strided matrix of the most efficient available type.
	 * 
	 * @param array
	 * @param arrayOffset
	 * @param reverse
	 * @param reverse2
	 * @return
	 */
	public static AStridedMatrix wrapStrided(double[] data, int rows, int cols, int offset, int rowStride, int colStride) {
		if ((offset==0)&&(data.length==rows*cols)) {
			if ((rows<=1)||(cols<=1)||((cols==rowStride)&&(colStride==1))) {
				return Matrix.wrap(rows, cols, data);
			} 
			if ((rows==colStride)&&(rowStride==1)) {
				return DenseColumnMatrix.wrap(rows, cols, data);
			} 
		}
		if (colStride==1) {
			return StridedRowMatrix.wrap(data, rows, cols, offset, rowStride);			
		}
		return StridedMatrix.wrap(data, rows, cols, offset, rowStride, colStride);
	}

	/**
	 * Creates a sparse matrix using the given arrays as slices
	 * @param slices An iterable object containing n length m vectors to use as matrix rows 
	 * @return
	 */
	public static AMatrix createSparse(Iterable<INDArray> slices) {
		INDArray slice1=slices.iterator().next();
		int cc=slice1.sliceCount();
		ArrayList<AVector> al=new ArrayList<AVector>();
		for (INDArray a:slices) {
			if ((a.dimensionality()!=1)||(a.sliceCount()!=cc)) throw new IllegalArgumentException(ErrorMessages.incompatibleShape(a)); 
			al.add(a.sparse().asVector());
		}
		return SparseRowMatrix.create(al);
	}

	/**
	 * Creates a sparse matrix using the given objects as slices
	 * @param slices
	 * @return
	 */
	public static AMatrix createSparse(Object... slices) {
		int rc=slices.length;
		AVector[] vs=new AVector[rc];
		AVector s0=Vectorz.createSparse(slices[0]);
		int cc=s0.length();
		vs[0]=s0;
		for (int i=1; i<rc; i++) {
			vs[i]=Vectorz.createSparse(slices[i]);
		}
		return SparseRowMatrix.create(vs,rc,cc);
	}

}
