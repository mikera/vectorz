package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Specialised class representing the broadcasting of a vector to a matrix shape
 * @author Mike
 *
 */
public final class BroadcastVectorMatrix extends ARectangularMatrix implements IFastRows {
	private static final long serialVersionUID = 8586152718389477791L;

	private final AVector vector;
	
	private BroadcastVectorMatrix(AVector v, int rows) {
		super(rows,v.length());
		this.vector=v;
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
		return vector;
	}
	
	@Override
	public final void copyColumnTo(int col, double[] dest, int destOffset) {
		double v=vector.get(col);
		Arrays.fill(dest, destOffset, destOffset+rows, v);
	}
	
	@Override
	public final void copyRowTo(int row, double[] dest, int destOffset) {
		vector.getElements(dest,destOffset);
	}
	
	@Override
	public AMatrix subMatrix(int rowStart, int rows, int colStart, int cols) {
		return BroadcastVectorMatrix.wrap(vector.subVector(colStart, cols), rows);
	}

	@Override
	public AMatrix exactClone() {
		return BroadcastVectorMatrix.wrap(vector.exactClone(),rows);
	}

	@Override
	public double get(int row, int column) {
		checkRow(row);
		return vector.get(column);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return vector.unsafeGet(column);
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

	@Override
	public boolean isZero() {
		return vector.isZero();
	}
}
