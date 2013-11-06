package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Matrix class that wraps a vector as a 1-columns matrix
 * @author Mike
 */
public class ColumnMatrix extends AMatrix {
	private final AVector vector;
	
	public ColumnMatrix(AVector v) {
		vector=v;
	}
	
	public static ColumnMatrix wrap(AVector v) {
		return new ColumnMatrix(v);
	}


	@Override
	public int rowCount() {
		return vector.length();
	}

	@Override
	public int columnCount() {
		return 1;
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		if (col==0) {
			vector.getElements(dest, destOffset);
		} else {
			throw new IndexOutOfBoundsException("Column out of range: "+col);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		vector.applyOp(op);
	}
	
	@Override
	public void multiply(double factor) {
		vector.scale(factor);
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
		if(column!=0) throw new IndexOutOfBoundsException(ErrorMessages.position(row,column));
		return vector.get(row);
	}

	@Override
	public void set(int row, int column, double value) {
		if(column!=0) throw new IndexOutOfBoundsException(ErrorMessages.position(row,column));
		vector.set(row,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return vector.unsafeGet(row);
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		vector.unsafeSet(row,value);
	}
	
	@Override
	public RowMatrix getTranspose() {
		return new RowMatrix(vector);
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
	public ColumnMatrix exactClone() {
		return new ColumnMatrix(vector.exactClone());
	}

	@Override
	public Matrix transposeInnerProduct(Matrix s) {
		Vector v=vector.innerProduct(s).toVector();
		return Matrix.wrap(1, s.columnCount(), v.asDoubleArray());
	}

}
