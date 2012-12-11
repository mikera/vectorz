package mikera.indexz.impl;

import java.util.Iterator;

import mikera.indexz.AIndex;

/**
 * General purpose iterator for arbitrary indexes.
 * 
 * @author Mike
 */
public class IndexIterator implements Iterator<Integer> {
	private final AIndex source;
	private final int length;
	private int pos=0;
	
	public IndexIterator(AIndex source) {
		this.source=source;
		this.length=source.length();
	}
	
	@Override
	public boolean hasNext() {
		return pos<length;
	}

	@Override
	public Integer next() {
		assert(pos<length);
		return source.get(pos++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from IndexIterator");
	}

}
