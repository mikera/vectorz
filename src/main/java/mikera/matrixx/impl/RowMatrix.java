package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix class that wraps a vector as a 1-row matrix
 * @author Mike
 */
public class RowMatrix extends AMatrix {
	private final AVector vector;
	
	public RowMatrix(AVector v) {
		vector=v;
	}
	
	public static RowMatrix wrap(AVector v) {
		return new RowMatrix(v);
	}

	@Override
	public int rowCount() {
		return 1;
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
	public int columnCount() {
		return vector.length();
	}
	
	@Override 
	public double elementSum() {
		return vector.elementSum();
	}
	
	@Override 
	public long nonZeroCount() {
		return vector.nonZeroCount();
	}

	@Override
	public double get(int row, int column) {
		if (row!=0) throw new IndexOutOfBoundsException("Row: "+row);
		return vector.get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		if (row!=0) throw new IndexOutOfBoundsException("Row: "+row);
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
	public void addAt(int i, int j, double d) {
		assert(i==0);
		vector.addAt(j,d);
	}
	
	@Override
	public ColumnMatrix getTranspose() {
		return new ColumnMatrix(vector);
	}

	@Override
	public RowMatrix exactClone() {
		return new RowMatrix(vector.exactClone());
	}
	
	@Override
	public void copyRowTo(int row, double[] dest, int destOffset) {
		if (row==0) {
			vector.getElements(dest, destOffset);
		} else {
			throw new IndexOutOfBoundsException("Row out of range: "+row);
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
