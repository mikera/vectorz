package mikera.matrixx.impl;

import java.nio.DoubleBuffer;

import mikera.arrayz.impl.IDenseArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ImmutableVector;
import mikera.vectorz.util.DoubleArrays;

/**
 * Immutable dense matrix class
 * 
 * This is the immutable equivalent of mikera.matrixx.Matrix
 * 
 * @author Mike
 *
 */
public final class ImmutableMatrix extends ARectangularMatrix implements IDenseArray, IFastRows {
	private static final long serialVersionUID = 2848013010449128820L;

	private final double[] data;
	
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
	 * WARNING: does not take a defensive copy. Should be used only if you can guarantee that
	 * the underlying data array will not be mutated elsewhere.
	 */
	public static ImmutableMatrix wrap(Matrix source) {
		double[] data=source.data;
		return new ImmutableMatrix(source.rowCount(), source.columnCount(), data);
	}
	
	/**
	 * Creates a new ImmutableMatrix wrapping the specified source array
	 * 
	 * WARNING: does not take a defensive copy. Should be used only if you can guarantee that
	 * the data array will not be mutated elsewhere.
	 */
	public static ImmutableMatrix wrap(int rows, int cols, double[] data) {
		return new ImmutableMatrix(rows,cols, data);
	}
	
	private Matrix asMatrix() {
		return Matrix.wrap(rows, cols, data);
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
	public double get(int i, int j) {
		checkIndex(i,j);
		return unsafeGet(i,j);
	}
	
	@Override
	public ImmutableVector getRowView(int row) {
		checkRow(row);
		return ImmutableVector.wrap(data,row*cols,cols);
	}
	
	@Override
	public double unsafeGet(int i, int j) {
		return data[i*cols+j];
	}
	
	@Override
	public final void copyRowTo(int row, double[] dest, int destOffset) {
		int srcOffset=row*cols;
		System.arraycopy(data, srcOffset, dest, destOffset, cols);
	}
	
	@Override
	public Vector innerProduct(AVector a) {
		return asMatrix().innerProduct(a);
	}
	
	@Override
	public Matrix innerProduct(AMatrix a) {
		return asMatrix().innerProduct(a);
	}
	
	@Override
	public void transform(Vector source, Vector dest) {
		asMatrix().transform(source, dest);
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
	public ImmutableVector asVector() {
		return ImmutableVector.wrap(data, getArrayOffset(), rows*cols);
	}

	@Override
	public Matrix toMatrix() {
		return Matrix.wrap(rows, cols, DoubleArrays.copyOf(data));
	}
	
	@Override
	public Matrix toMatrixTranspose() {
		Matrix m = Matrix.create(cols, rows);
		for (int j=0; j<cols; j++) {
			copyColumnTo(j,m.data,rows*j);
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
	
	@Override
	public boolean equals(AMatrix a) {
		if (!isSameShape(a)) return false;
		return a.equalsArray(data, 0);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return DoubleArrays.equals(this.data, 0, data, offset, rows*cols);
	}

	/**
	 * Unsafe method that returns the internal data array
	 * @return
	 */
	public double[] getInternalData() {
		return data;
	}

	public static ImmutableMatrix create(AMatrix a) {
		int rows=a.rowCount();
		int cols=a.columnCount();
		return ImmutableMatrix.wrap(rows,cols,a.getElements());
	}

	@Override
	public double[] getArray() {
		return data;
	}

	@Override
	public int getArrayOffset() {
		return 0;
	}
}
