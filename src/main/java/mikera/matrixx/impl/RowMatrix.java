package mikera.matrixx.impl;

import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix class that wraps a vector as a single-row matrix
 * @author Mike
 */
public class RowMatrix extends ARectangularMatrix implements IFastColumns, IFastRows {
	private static final long serialVersionUID = 2636365975400418264L;

	private final AVector vector;
	
	public RowMatrix(AVector v) {
		super(1,v.length());
		vector=v;
	}
	
	public static RowMatrix wrap(AVector v) {
		return new RowMatrix(v);
	}
	
	@Override
	public boolean isFullyMutable() {
		return vector.isFullyMutable();
	}
	
	@Override
	public boolean isMutable() {
		return vector.isMutable();
	}
	
	@Override
	public boolean isZero() {
		return vector.isZero();
	}
	
	@Override
	public Vector toVector() {
		return vector.toVector();
	}
	
	@Override
	public AVector asVector() {
		return vector;
	}
	
	@Override
	public void multiply(double factor) {
		vector.scale(factor);
	}
	
	@Override
	public void applyOp(Op op) {
		vector.applyOp(op);
	}
	
	@Override 
	public double elementSum() {
		return vector.elementSum();
	}
	
	@Override 
	public double elementSquaredSum() {
		return vector.elementSquaredSum();
	}
	
	@Override 
	public double elementMin() {
		return vector.elementMin();
	}
	
	@Override 
	public double elementMax() {
		return vector.elementMax();
	}
	
	@Override 
	public long nonZeroCount() {
		return vector.nonZeroCount();
	}

	@Override
	public double get(int row, int column) {
		if (row!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		return vector.get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		if (row!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, row,column));
		vector.set(column,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return vector.unsafeGet(column);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		vector.unsafeSet(column,value);
	}
	
	@Override
	public AVector getRowView(int i) {
		if (i!=0) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, i));
		return vector;
	}
	
	@Override
	public AVector getColumnView(int i) {
		return vector.subVector(i, 1);
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		assert(i==0);
		vector.addAt(j,d);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		vector.addToArray(data, offset);
	}
	
	@Override
	public ColumnMatrix getTranspose() {
		return new ColumnMatrix(vector);
	}
	
	@Override
	public ColumnMatrix getTransposeView() {
		return new ColumnMatrix(vector);
	}

	@Override
	public RowMatrix exactClone() {
		return new RowMatrix(vector.exactClone());
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return vector.equalsArray(data, offset);
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		if (row==0) {
			vector.getElements(dest, destOffset);
		} else {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(this, row));
		}
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		dest[destOffset]=vector.get(col);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		vector.getElements(data, offset);
	}

	@Override
	public Matrix transposeInnerProduct(Matrix s) {
		if (s.rowCount()!=1) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, s));
		int rc=this.columnCount();
		int cc=s.columnCount();
		Matrix m=Matrix.create(rc,cc);
		for (int i=0; i<rc; i++) {
			double ti=vector.unsafeGet(i);
			DoubleArrays.addMultiple(m.data, i*cc, s.data, 0, cc, ti);
		}
		return m;
	}
}
