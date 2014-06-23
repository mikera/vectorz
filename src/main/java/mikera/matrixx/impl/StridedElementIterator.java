package mikera.matrixx.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over strided matrix array elements.
 * 
 * This is an optimised / specialised element iterator for strided matrices that otherwise
 * performs the same function as MatrixElementIterator.
 * 
 * Performance tricks used:
 * - Caches the data array reference
 * - Caches the underlying stride data to avoid re-checking the strided matrix
 * - Hopefully fits the full iterator data in a 64-byte cache line
 * 
 * @author Mike
 */
public class StridedElementIterator implements Iterator<Double> {
	private int col=0;
	private int row=0;
	private final int rows;
	private final int cols;
	private final int offset;
	private final int rowStride;
	private final int colStride;
	private final double[] source;
	
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
		throw new UnsupportedOperationException("Cannot remove from StridedElementIterator");
	}
}
