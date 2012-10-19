package mikera.vectorz;

import java.util.Iterator;

/**
 * General purpose iterator for arbitrary vectors.
 * 
 * @author Mike
 */
public class VectorIterator implements Iterator<Double> {
	private final AVector source;
	private final int length;
	private int pos=0;
	
	public VectorIterator(AVector source) {
		this.source=source;
		this.length=source.length();
	}
	
	@Override
	public boolean hasNext() {
		return pos<length;
	}

	@Override
	public Double next() {
		assert(pos<length);
		return source.get(pos++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from VectorIterator");
	}

}
