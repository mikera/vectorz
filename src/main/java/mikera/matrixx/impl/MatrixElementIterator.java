package mikera.matrixx.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import mikera.matrixx.AMatrix;

/**
 * Iterator over matrix elements for an arbitrary matrix
 * 
 * @author Mike
 */
public class MatrixElementIterator implements Iterator<Double> {
	private final AMatrix source;
	private int col=0;
	private int row=0;
	
	public MatrixElementIterator(AMatrix source) {
		this.source=source;
		
		// hack for matrices with zero elements
		if (source.elementCount()==0) {
			row=source.rowCount();
		}
	}
	
	@Override
	public boolean hasNext() {
		return row<source.rowCount();
	}

	@Override
	public Double next() {
		if (row>=source.rowCount()) throw new NoSuchElementException();
		int ox=col++;
		int oy=row;
		if (col>=source.columnCount()) {
			col=0;
			row++;
		}
		return source.unsafeGet(oy,ox);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from MatrixElementIterator");
	}
}
