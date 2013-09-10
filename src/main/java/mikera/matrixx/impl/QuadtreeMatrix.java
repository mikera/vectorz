package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/**
 * A matrix implemented as a quadtree of submatrices. 
 * 
 * @author Mike
 *
 */
public class QuadtreeMatrix extends AMatrix {
	
	// Quadtree subcompoents
	private final AMatrix c00, c01, c10, c11;
	
	private final int rowSplit;
	private final int columnSplit;
	private final int rows;
	private final int columns;
	
	private QuadtreeMatrix(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		this.c00=c00;
		this.c01=c01;
		this.c10=c10;
		this.c11=c11;
		this.rowSplit= c00.rowCount();
		this.columnSplit=c00.columnCount();
		this.rows=rowSplit+c10.rowCount();
		this.columns=rowSplit+c01.columnCount();
	}
	
	public static QuadtreeMatrix create(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		if (c00.rowCount()!=c01.rowCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c10.rowCount()!=c11.rowCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c00.columnCount()!=c10.columnCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c01.columnCount()!=c11.columnCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		return new QuadtreeMatrix(c00,c01,c10,c11);
	}
	
	@Override
	public boolean isFullyMutable() {
		return c00.isFullyMutable()&&c01.isFullyMutable()&&c10.isFullyMutable()&&(c11.isFullyMutable());
	}

	@Override
	public int rowCount() {
		return rows;
	}

	@Override
	public int columnCount() {
		return columns;
	}

	@Override
	public double get(int row, int column) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				return c00.get(row,column);
			} else {
				return c01.get(row,column-columnSplit);		
			}
		} else {
			if (column<columnSplit) {
				return c10.get(row-rowSplit,column);
			} else {
				return c11.get(row-rowSplit,column-columnSplit);		
			}	
		}
	}

	@Override
	public void set(int row, int column, double value) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				c00.set(row,column, value);
			} else {
				c01.set(row,column-columnSplit, value);		
			}
		} else {
			if (column<columnSplit) {
				c10.set(row-rowSplit,column, value);
			} else {
				c11.set(row-rowSplit,column-columnSplit, value);		
			}	
		}	
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				return c00.unsafeGet(row,column);
			} else {
				return c01.unsafeGet(row,column-columnSplit);		
			}
		} else {
			if (column<columnSplit) {
				return c10.unsafeGet(row-rowSplit,column);
			} else {
				return c11.unsafeGet(row-rowSplit,column-columnSplit);		
			}	
		}
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				c00.unsafeSet(row,column, value);
			} else {
				c01.unsafeSet(row,column-columnSplit, value);		
			}
		} else {
			if (column<columnSplit) {
				c10.unsafeSet(row-rowSplit,column, value);
			} else {
				c11.unsafeSet(row-rowSplit,column-columnSplit, value);		
			}	
		}	
	}

	@Override
	public AMatrix exactClone() {
		return new QuadtreeMatrix(c00.exactClone(),c01.exactClone(),c10.exactClone(),c11.exactClone());
	}
}
