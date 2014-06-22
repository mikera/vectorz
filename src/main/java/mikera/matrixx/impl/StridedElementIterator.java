package mikera.matrixx.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over matrix rows
 * 
 * @author Mike
 */
public class StridedElementIterator implements Iterator<Double> {
	private final double[] source;
	private final int rows;
	private final int cols;
	private final int offset;
	private final int rowStride;
	private final int colStride;
	private int col=0;
	private int row=0;
	
	public StridedElementIterator(AStridedMatrix a) {
		this(a.getArray(),a.rows,a.cols,a.getArrayOffset(),a.rowStride(),a.columnStride());
	}
	
	public StridedElementIterator(double[] array, int rows, int cols,
			int arrayOffset, int rowStride, int colStride) {
		this.source=array;
		this.rows=rows;
		this.cols=cols;
		this.rowStride=rowStride;
		this.colStride=colStride;
		this.offset=arrayOffset;
	}

	@Override
	public boolean hasNext() {
		return row<rows;
	}

	@Override
	public Double next() {
		if (row>=rows) throw new NoSuchElementException();
		int ox=col++;
		int oy=row;
		if (col>=cols) {
			col=0;
			row++;
		}
		return source[offset+(ox*colStride)+(oy*rowStride)];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from MatrixElementIterator");
	}
}
