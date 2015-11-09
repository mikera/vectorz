package mikera.arrayz.impl;

import java.util.Iterator;

import mikera.arrayz.INDArray;
import mikera.vectorz.util.ErrorMessages;

/**
 * General purpose iterator for slices of arbitrary arrays.
 * 
 * @author Mike
 */
public class SliceIterator<T> implements Iterator<T> {
	private final INDArray source;
	private final int maxPos;
	private int pos;
	
	public SliceIterator(INDArray source) {
		this.pos=0;
		this.source=source;
		this.maxPos=source.sliceCount();
	}
	
	public SliceIterator(INDArray source, int start, int length) {
		this.pos=start;
		this.source=source;
		this.maxPos=start+length;
	}
	
	@Override
	public boolean hasNext() {
		return pos<maxPos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		assert(pos<maxPos);
		return (T)source.slice(pos++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(ErrorMessages.immutable(this));
	}

}
