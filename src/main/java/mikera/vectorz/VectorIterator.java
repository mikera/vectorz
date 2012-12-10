package mikera.vectorz;

import java.util.Iterator;

/**
 * General purpose iterator for arbitrary vectors.
 * 
 * @author Mike
 */
public class VectorIterator implements Iterator<Double> {
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
		assert(pos<maxPos);
		return source.get(pos++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from VectorIterator");
	}

}
