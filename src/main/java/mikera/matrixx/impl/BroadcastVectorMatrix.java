package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised class representing the broadcasting of a vector to a matrix shape.
 * All rows of this matrix are identical views of the source vector.
 * 
 * Matrices of this type should be considered immutable.
 * @author Mike
 *
 */
public final class BroadcastVectorMatrix extends ARectangularMatrix implements IFastRows, IFastColumns {
	private static final long serialVersionUID = 8586152718389477791L;

	private final AVector source;
	
	private BroadcastVectorMatrix(AVector v, int rows) {
		super(rows,v.length());
		this.source=v;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	public static BroadcastVectorMatrix wrap(AVector v, int rows) {
		return new BroadcastVectorMatrix(v,rows);
	}

	@Override
	public AVector getRowView(int row) {
		checkRow(row);
		return source;
	}
	
	@Override
	public AVector getRow(int row) {
		checkRow(row);
		return source;
	}
	
	@Override
 	public AVector getColumn(int col) {
		return RepeatedElementVector.create(rows, source.get(col));
	}
	
	@Override
	public final void copyColumnTo(int col, double[] dest, int destOffset) {
		double v=source.get(col);
		Arrays.fill(dest, destOffset, destOffset+rows, v);
	}
	
	@Override
	public final void copyRowTo(int row, double[] dest, int destOffset) {
		source.getElements(dest,destOffset);
	}
	
	@Override
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		return BroadcastVectorMatrix.wrap(source.subVector(colStart, cols), rows);
	}

	@Override
	public AMatrix exactClone() {
		return BroadcastVectorMatrix.wrap(source.exactClone(),rows);
	}

	@Override
	public double get(int row, int column) {
		checkRow(row);
		return source.get(column);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return source.unsafeGet(column);
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public boolean isZero() {
		return source.isZero();
	}

	@Override
	public void setSparse(double v) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}
}
