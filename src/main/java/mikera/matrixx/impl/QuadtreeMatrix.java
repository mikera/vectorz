package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * A matrix implemented as a quadtree of submatrices. 
 * 
 * Useful for large matrices with heirarchical structure where large regions are either fully sparse
 * or of a specialised subtype (e.g. a diagonal matrix).
 * 
 * @author Mike
 *
 */
public class QuadtreeMatrix extends ABlockMatrix implements ISparse {
	private static final long serialVersionUID = -7267626771473908891L;

	// Quadtree subcompoents
	private final AMatrix c00, c01, c10, c11;
	
	private final int rowSplit;
	private final int columnSplit;
	
	private QuadtreeMatrix(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		super(c00.rowCount()+c10.rowCount(),c00.columnCount()+c01.columnCount());
		this.c00=c00;
		this.c01=c01;
		this.c10=c10;
		this.c11=c11;
		this.rowSplit= c00.rowCount();
		this.columnSplit=c00.columnCount();
	}
	
	public static QuadtreeMatrix wrap(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		if (c00.rowCount()!=c01.rowCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c10.rowCount()!=c11.rowCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c00.columnCount()!=c10.columnCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		if (c01.columnCount()!=c11.columnCount()) throw new IllegalArgumentException("Mismtached submatrix size");
		return new QuadtreeMatrix(c00,c01,c10,c11);
	}
	
	public static QuadtreeMatrix create(AMatrix c00, AMatrix c01, AMatrix c10, AMatrix c11) {
		return wrap(c00.copy(),c01.copy(),c10.copy(),c11.copy());
	}
	
	@Override
	public boolean isFullyMutable() {
		return c00.isFullyMutable()&&c01.isFullyMutable()&&c10.isFullyMutable()&&(c11.isFullyMutable());
	}
	
	@Override
	public boolean isMutable() {
		return (c00.isMutable())||(c01.isMutable())||(c10.isMutable())||(c11.isMutable());
	}
	
	@Override
	public boolean isZero() {
		return c00.isZero()&&c01.isZero()&&c10.isZero()&&(c11.isZero());
	}
	
	@Override
	public boolean isDiagonal() {
		if (!isSquare()) return false;
		if (columnSplit==rowSplit) {
			return (c01.isZero())&&(c10.isZero())&&(c00.isDiagonal())&&(c11.isDiagonal());
		} else {
			return super.isDiagonal();
		}
	}

	@Override
	public double get(int row, int column) {
		checkIndex(row,column);
		return unsafeGet(row,column);
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		unsafeSet(row,column,value);
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
	public void addAt(int row, int column, double value) {
		if (row<rowSplit) {
			if (column<columnSplit) {
				c00.addAt(row,column, value);
			} else {
				c01.addAt(row,column-columnSplit, value);		
			}
		} else {
			if (column<columnSplit) {
				c10.addAt(row-rowSplit,column, value);
			} else {
				c11.addAt(row-rowSplit,column-columnSplit, value);		
			}	
		}	
	}
	
	@Override
	public void copyRowTo(int row, double[] data, int offset) {
		if (row<rowSplit) {
			c00.copyRowTo(row, data, offset);
			c01.copyRowTo(row, data, offset+columnSplit);
		} else {
			c10.copyRowTo(row-rowSplit, data, offset);
			c11.copyRowTo(row-rowSplit, data, offset+columnSplit);
		}
	}
	
	@Override
	public void copyColumnTo(int col, double[] data, int offset) {
		if (col<columnSplit) {
			c00.copyColumnTo(col, data, offset);
			c10.copyColumnTo(col, data, offset+rowSplit);
		} else {
			c01.copyColumnTo(col-columnSplit, data, offset);
			c11.copyColumnTo(col-columnSplit, data, offset+rowSplit);
		}
	}
	
	@Override
	public long nonZeroCount() {
		return c00.nonZeroCount()+c01.nonZeroCount()+c10.nonZeroCount()+c11.nonZeroCount();
	}
	
	@Override
	public double elementSum() {
		return c00.elementSum()+c01.elementSum()+c10.elementSum()+c11.elementSum();
	}
	
	@Override
	public double elementMin() {
		return Math.min(
				Math.min(c00.elementMin(), c01.elementMin()), 
				Math.min(c10.elementMin(), c11.elementMin()));
	}
	
	@Override
	public double elementMax() {
		return Math.max(
				Math.max(c00.elementMax(), c01.elementMax()), 
				Math.max(c10.elementMax(), c11.elementMax()));
	}
	
	@Override
	public void fill(double v) {
		c00.fill(v);
		c01.fill(v);
		c10.fill(v);
		c11.fill(v);
	}
	
	
	@Override
	public void add(double v) {
		c00.add(v);
		c01.add(v);
		c10.add(v);
		c11.add(v);
	}
	
	@Override
	public void add(AVector v) {
		AVector v0=v.subVector(0, columnSplit);
		AVector v1=v.subVector(columnSplit,cols-columnSplit);
		c00.add(v0);
		c01.add(v1);
		c10.add(v0);
		c11.add(v1);
	}
	
	@Override
	public AVector getRowView(int row) {
		if (row<rowSplit) {
			return c00.getRowView(row).join(c01.getRowView(row));
		} else {
			row-=rowSplit;
			return c10.getRowView(row).join(c11.getRowView(row));
		}
	}
	
	@Override
	public AVector getColumnView(int col) {
		if (col<columnSplit) {
			return c00.getColumnView(col).join(c10.getColumnView(col));
		} else {
			col-=columnSplit;
			return c01.getColumnView(col).join(c11.getColumnView(col));
		}
	}

	@Override
	public AMatrix exactClone() {
		return new QuadtreeMatrix(c00.exactClone(),c01.exactClone(),c10.exactClone(),c11.exactClone());
	}

	@Override
	public double density() {
		return ((double)nonZeroCount())/((long)rows*(long)cols);
	}

	@Override
	public AMatrix getBlock(int rowBlock, int colBlock) {
		switch (rowBlock) {
		case 0:
			switch (colBlock) {
			case 0: return c00;
			case 1: return c01;
			default: throw new IndexOutOfBoundsException("Column Block: "+colBlock);			
			}
		case 1:
			switch (colBlock) {
			case 0: return c10;
			case 1: return c11;
			default: throw new IndexOutOfBoundsException("Column Block: "+colBlock);			
			}
		
		default: throw new IndexOutOfBoundsException("Row Block: "+rowBlock);
		}
	}
	
	@Override
	public int getBlockColumnStart(int colBlock) {
		switch (colBlock) {
		case 0: return 0;
		case 1: return columnSplit;
		default: throw new IndexOutOfBoundsException("Column Block: "+colBlock);			
		}
	}
	
	@Override
	public int getBlockRowStart(int rowBlock) {
		switch (rowBlock) {
		case 0: return 0;
		case 1: return rowSplit;
		default: throw new IndexOutOfBoundsException("Row Block: "+rowBlock);			
		}
	}


	@Override
	public int getBlockColumnCount(int colBlock) {
		return (colBlock==0)?columnSplit:(cols-columnSplit);
	}

	@Override
	public int getBlockRowCount(int rowBlock) {
		return (rowBlock==0)?rowSplit:(rows-rowSplit);
	}

	@Override
	public int getColumnBlockIndex(int col) {
		return col<columnSplit?0:1;
	}

	@Override
	public int getRowBlockIndex(int row) {
		return row<rowSplit?0:1;
	}

	@Override
	public int columnBlockCount() {
		return 2;
	}

	@Override
	public int rowBlockCount() {
		return 2;
	}
}
