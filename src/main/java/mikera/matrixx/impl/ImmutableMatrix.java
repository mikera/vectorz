package mikera.matrixx.impl;

import java.nio.DoubleBuffer;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

public final class ImmutableMatrix extends AMatrix {
	private double[] data;
	private int rows;
	private int cols;
	
	public ImmutableMatrix(AMatrix m) {
		rows=m.rowCount();
		cols=m.columnCount();
		data=m.toDoubleArray();
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return cols;
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
		for (int i=0; i<rows; i++) {
			v.data[i]=a.dotProduct(data, i*cols);
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
		for (int row = 0; row < rc; row++) {
			double total = 0.0;
			for (int column = 0; column < cc; column++) {
				total += data[di+column] * source.data[column];
			}
			di+=cc;
			dest.data[row]=total;
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
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		dest.put(data);
	}
	
	@Override
	public void getElements(double[] dest, int offset) {
		System.arraycopy(data, 0, dest, offset, data.length);
	}
	
	@Override
	public Matrix clone() {
		return Matrix.create(this);
	}

	@Override
	public AMatrix exactClone() {
		return new ImmutableMatrix(this);
	}

}
