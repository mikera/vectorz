package mikera.arrayz.impl;

import java.util.Iterator;

import mikera.arrayz.INDArray;

/**
 * General purpose iterator for elements of arbitrary arrays.
 * 
 * @author Mike
 */
public class SliceElementIterator implements Iterator<Double> {
	private final INDArray source;
	private final int maxPos;
	private int pos;
	private Iterator<Double> inner;
	
	public SliceElementIterator(INDArray source) {
		this.pos=0;
		this.source=source;
		this.maxPos=source.sliceCount();
		inner=source.slice(pos).elementIterator();
		if (!inner.hasNext()) {
			pos=maxPos;
		}
	}
	
	public SliceElementIterator(INDArray source, int start, int length) {
		this.pos=start;
		this.source=source;
		this.maxPos=start+length;
	}
	
	@Override
	public boolean hasNext() {
		return (pos<maxPos)&&(inner.hasNext());
	}

	@Override
	public Double next() {
		Double d=inner.next();
		if (!inner.hasNext()) {
			pos++;
			if (pos<maxPos) inner=source.slice(pos).elementIterator();
		}
		return d;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
