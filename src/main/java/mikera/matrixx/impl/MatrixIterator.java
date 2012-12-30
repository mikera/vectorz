package mikera.matrixx.impl;

import java.util.Iterator;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

public class MatrixIterator implements Iterator<AVector> {
	private final AMatrix source;
	private final int maxPos;
	private int pos;

	public MatrixIterator(AMatrix source) {
		this.pos=0;
		this.source=source;
		this.maxPos=source.outputDimensions();
	}
	
	@Override
	public boolean hasNext() {
		return pos<maxPos;
	}

	@Override
	public AVector next() {
		assert(pos<maxPos);
		return source.getRow(pos++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from MatrixIterator");
	}
}
