package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.arrayz.impl.IDense;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.util.DoubleArrays;

/**
 * A densely packed matrix organised in column-major format.
 * 
 * Transposes to/from a regular dense Matrix
 * 
 * This class is useful if your main performance requirement is fast access to columns of a matrix. 
 * This is frequently true when the matrix is used as the *second* argument in matrix/matrix operations.
 * 
 * @author Mike
 *
 */
public final class DenseColumnMatrix extends AStridedMatrix implements IFastColumns, IDense {
	private static final long serialVersionUID = 5459617932072332096L;

	private DenseColumnMatrix(int rowCount, int columnCount, double[] data) {
		super(data, rowCount, columnCount);
	}
	
	private DenseColumnMatrix(int rowCount, int columnCount) {
		this(rowCount, columnCount, DoubleArrays.createStorage(rowCount, columnCount));
	}
	
	public static DenseColumnMatrix wrap(int rows, int cols, double[] data) {
		return new DenseColumnMatrix(rows, cols, data);
	}
	
	public static DenseColumnMatrix create(AMatrix m) {
		DenseColumnMatrix dm= new DenseColumnMatrix(m.rowCount(), m.columnCount());
		dm.set(m);
		return dm;
	}
	
	@Override
	public int getArrayOffset() {
		return 0;
	}

	@Override
	public int rowStride() {
		return 1;
	}

	@Override
	public int columnStride() {
		return rows;
	}
	
	@Override
	public ArraySubVector getColumn(int j) {
		return ArraySubVector.wrap(data, j*rows, rows);
	}

	@Override
	public void copyRowTo(int i, double[] dest, int destOffset) {
		for (int j=0; j<cols; j++) {
			dest[destOffset+j]=data[i+j*rows];
		}
	}

	@Override
	public void copyColumnTo(int j, double[] dest, int destOffset) {
		System.arraycopy(data, j*rows, dest, destOffset, rows);
	}
	
	@Override
	public void setRow(int i, AVector row) {
		int cc = checkColumnCount(row.length());
		for (int j = 0; j < cc; i++) {
			data[index(i, j)] = row.unsafeGet(i);
		}
	}

	@Override
	public void setColumn(int j, AVector col) {
		int rc = checkRowCount(col.length());
		col.getElements(data, j * rc);
	}
	
	@Override
	public void addMultiple(AMatrix m, double factor) {
		checkRowCount(m.rowCount());
		int cc=checkColumnCount(m.columnCount());
		
		for (int i=0; i<cc; i++) {
			getColumnView(i).addMultiple(m.getColumn(i), factor);
		}
	}
	
	@Override
	public void addOuterProduct(AVector a, AVector b) {
		a.checkLength(rows);
		int cc=b.checkLength(cols);
		double[] data=getArray();
		for (int i=0; i<cc; i++) {
			a.addMultipleToArray(b.unsafeGet(i), data, rows*i);
		}	
	}
	
	@Override 
	public void set(AMatrix m) {
		checkSameShape(m);
		for (int i=0; i<cols; i++) {
			m.copyColumnTo(i, data, index(0,i));
		}
	}

	@Override
	protected int index(int i, int j) {
		return i+j*rows;
	}
	
	@Override
	public double get(int i, int j) {
		checkRow(i); // we only need to check i is in range: out of range j will trigger exception anyway
		return data[(j * rows) + i];
	}
	
	@Override
	public void set(int i, int j, double value) {
		checkRow(i); // we only need to check i is in range: out of range j will trigger exception anyway
		data[(j * rows) + i] = value;
	}

	@Override
	public void unsafeSet(int i, int j, double value) {
		data[(j * rows) + i] = value;
	}

	@Override
	public double unsafeGet(int i, int j) {
		return data[(j * rows) + i];
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		data[(j * rows) + i] += d;
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isPackedArray() {
		return (cols<=1);
	}
	
	@Override
	public boolean isBoolean() {
		return DoubleArrays.isBoolean(data, 0, data.length);
	}

	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data, 0, data.length);
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
	public double elementMax() {
		return DoubleArrays.elementMax(data);
	}

	@Override
	public double elementMin() {
		return DoubleArrays.elementMin(data);
	}

	@Override
	public void abs() {
		DoubleArrays.abs(data);
	}

	@Override
	public void signum() {
		DoubleArrays.signum(data);
	}

	@Override
	public void square() {
		DoubleArrays.square(data);
	}

	@Override
	public void exp() {
		DoubleArrays.exp(data);
	}

	@Override
	public void log() {
		DoubleArrays.log(data);
	}
	
	@Override
	public void applyOp(Op op) {
		op.applyTo(data);
	}

	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data);
	}
	
	@Override
	public void add(double d) {
		DoubleArrays.add(data, d);
	}

	@Override
	public void multiply(double factor) {
		DoubleArrays.multiply(data, factor);
	}
	
	@Override
	public void fill(double value) {
		Arrays.fill(data, value);
	}
	
	@Override
	public void reciprocal() {
		DoubleArrays.reciprocal(data, 0, data.length);
	}

	@Override
	public void clamp(double min, double max) {
		DoubleArrays.clamp(data, 0, data.length, min, max);
	}

	
	@Override
	public Matrix getTranspose() {
		return getTransposeView();
	}
	
	@Override
	public Matrix getTransposeView() {
		return Matrix.wrap(cols, rows, data);
	}

	@Override
	public DenseColumnMatrix exactClone() {
		return new DenseColumnMatrix(rows,cols,data.clone());
	}
	
	@Override
	public DenseColumnMatrix dense() {
		return this;
	}
	
	@Override
	public DenseColumnMatrix copy() {
		return exactClone();
	}
		
	@Override
	public Matrix toMatrixTranspose() {
		return Matrix.wrap(cols, rows, data);
	}

}
