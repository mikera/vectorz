package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Specialised class representing the broadcasting of a vector to a matrix shape
 * @author Mike
 *
 */
public final class BroadcastVectorMatrix extends AVectorMatrix<AVector> {
	private final int rows;
	private final int cols;
	private final AVector vector;
	
	private BroadcastVectorMatrix(AVector v, int rows) {
		this.rows=rows;
		this.cols=v.length();
		this.vector=v;
	}
	
	public static BroadcastVectorMatrix wrap(AVector v, int rows) {
		return new BroadcastVectorMatrix(v,rows);
	}
	
	@Override
	public void appendRow(AVector row) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AVector getRow(int row) {
		if (row<0 ||(row>=rows)) throw new IndexOutOfBoundsException("Row: "+row);
		return vector;
	}
	
	@Override
	public final void copyColumnTo(int col, double[] dest, int destOffset) {
		double v=vector.get(col);
		for (int i=0;i<rows; i++) {
			dest[destOffset+i]=v;
		}
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
	public AMatrix exactClone() {
		return BroadcastVectorMatrix.wrap(vector.exactClone(),rows);
	}

}
