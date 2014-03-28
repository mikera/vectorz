package mikera.vectorz.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for elements stored in strided arrays.
 * 
 * @author Mike
 */
public final class StridedElementIterator implements Iterator<Double> {
	private final double[] source;
	private final int offset;
	private final int maxPos;
	private final int stride;
	private int pos;

	public StridedElementIterator(double[] source, int offset, int length,int stride) {
		this.pos=0;
		this.offset=offset;
		this.source=source;
		this.maxPos=length;
		this.stride=stride;
	}
	
	@Override
	public boolean hasNext() {
		return pos<maxPos;
	}

	@Override
	public Double next() {
		if(pos>=maxPos) throw new NoSuchElementException();
		return source[offset+(pos++)*stride];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from StridedElementIterator");
	}
}
