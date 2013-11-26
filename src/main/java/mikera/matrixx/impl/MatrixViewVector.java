package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

@SuppressWarnings("serial")
public class MatrixViewVector extends AMatrixSubVector {
	final AMatrix source;
	final int rows;
	final int columns;
	final int length;
	
	public MatrixViewVector(AMatrix a) {
		source=a;
		rows=a.rowCount();
		columns=a.columnCount();
		length=rows*columns;
	}

	@Override
	public int length() {
		return length;
	}
	
	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		return source.unsafeGet(i/columns, i%columns);
	}
	
	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this, i));
		source.unsafeSet(i/columns, i%columns,value);	
	}
	
	@Override
	public AVector exactClone() {
		return new MatrixViewVector(source.exactClone());
	}
	
	@Override
	public void fill(double v) {
		source.fill(v);
	}
}
