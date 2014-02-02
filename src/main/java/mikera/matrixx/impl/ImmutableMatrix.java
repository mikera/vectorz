package mikera.matrixx.impl;

import java.nio.DoubleBuffer;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ImmutableVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Immutable dense matrix class
 * 
 * This is the immutable equivalent of mikera.matrixx.Matrix
 * 
 * @author Mike
 *
 */
public final class ImmutableMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = 2848013010449128820L;

	private double[] data;
	
	private ImmutableMatrix(int rows, int cols, double[] data) {
		super(rows,cols);
		this.data=data;
	}
	
	public ImmutableMatrix(AMatrix m) {
		super(m.rowCount(),m.columnCount());
		data=m.toDoubleArray();
	}
	
	/**
	 * Creates a new ImmutableMatrix wrapping the data array of a given Matrix
	 * 
	 * WARNING: does not take a defensive copy.
	 */
	public static ImmutableMatrix wrap(Matrix source) {
		double[] data=source.data;
		return new ImmutableMatrix(source.rowCount(), source.columnCount(), data);
	}
	
	public static ImmutableMatrix wrap(int rows, int cols, double[] data) {
		return new ImmutableMatrix(rows,cols, data);
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data);
	}
	
	@Override
	public boolean isBoolean() {
		return DoubleArrays.isBoolean(data,0,data.length);
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data,0,data.length);
	}
	
	@Override
	public double get(int row, int column) {
		if ((row<0)||(row>=rows)||(column<0)||(column>=cols)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		}
		return unsafeGet(row,column);
	}
	
	@Override
	public ImmutableVector getRowView(int row) {
		if ((row<0)||(row>=rows)) throw new IllegalArgumentException(ErrorMessages.invalidSlice(this, row));
		return ImmutableVector.wrap(data,row*cols,cols);
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return data[i*cols+j];
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
	
	@Override
	public final void copyRowTo(int row, double[] dest, int destOffset) {
		int srcOffset=row*cols;
		System.arraycopy(data, srcOffset, dest, destOffset, cols);
	}
	
	@Override
	public Vector innerProduct(AVector a) {
		if (a instanceof Vector) return innerProduct((Vector)a);
		return transform(a);
	}
	
	@Override
	public Vector transform (AVector a) {
		Vector v=Vector.createLength(rows);
		double[] vdata=v.getArray();
		for (int i=0; i<rows; i++) {
			vdata[i]=a.dotProduct(data, i*cols);
		}
		return v;
	}
	
	@Override
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = columnCount();
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		int di=0;
		double[] sdata=source.getArray();
		double[] ddata=source.getArray();
		for (int row = 0; row < rc; row++) {
			double total = 0.0;
			for (int column = 0; column < cc; column++) {
				total += data[di+column] * sdata[column];
			}
			di+=cc;
			ddata[row]=total;
		}
	}
	
	@Override
	public final void copyColumnTo(int col, double[] dest, int destOffset) {
		int colOffset=col;
		for (int i=0;i<rows; i++) {
			dest[destOffset+i]=data[colOffset+i*cols];
		}
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data);
	}
	
	@Override
	public double elementSquaredSum() {
		return DoubleArrays.elementSquaredSum(data);
	}
	
	@Override
	public Vector toVector() {
		return Vector.create(data);
	}
	
	@Override
	public Matrix toMatrix() {
		return Matrix.wrap(rows, cols, DoubleArrays.copyOf(data));
	}
	
	public Matrix toMatrixTranspose() {
		int di=0;
		Matrix m = Matrix.create(cols, rows);
		for (int j=0; j<rows; j++) {
			for (int i=0; i<cols; i++) {
				m.unsafeSet(i, j, data[di++]);
			}
		}
		return m;
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, data.length);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		DoubleArrays.add(this.data, 0, data,offset,rows*cols);
	}
	
	@Override
	public Matrix clone() {
		return Matrix.create(this);
	}

	@Override
	public AMatrix exactClone() {
		return new ImmutableMatrix(this);
	}

	/**
	 * Unsafe method that returns the internal data array
	 * @return
	 */
	public double[] getInternalData() {
		return data;
	}

	public static INDArray create(AMatrix a) {
		int rows=a.rowCount();
		int cols=a.columnCount();
		int n=rows*cols;
		double[] data = new double[n];
		a.getElements(data,0);
		return ImmutableMatrix.wrap(rows,cols,data);
	}
}
