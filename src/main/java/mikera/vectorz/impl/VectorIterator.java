package mikera.vectorz.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import mikera.vectorz.AVector;

/**
 * General purpose iterator for arbitrary vectors.
 * 
 * @author Mike
 */
public final class VectorIterator implements Iterator<Double> {
	private final AVector source;
	private final int maxPos;
	private int pos;
	
	public VectorIterator(AVector source) {
		this.pos=0;
		this.source=source;
		this.maxPos=source.length();
	}
	
	public VectorIterator(AVector source, int start, int length) {
		this.pos=start;
		this.source=source;
		this.maxPos=start+length;
	}
	
	@Override
	public boolean hasNext() {
		return pos<maxPos;
	}

	@Override
	public Double next() {
		if(pos>=maxPos) throw new NoSuchElementException();
		return source.unsafeGet(pos++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from VectorIterator");
	}

}
