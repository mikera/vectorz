package mikera.vectorz.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * A basic iterator that returns a single double value
 */
public class SingleDoubleIterator implements Iterator<Double> {
	boolean used=false;
	final double value;
	
	public SingleDoubleIterator(double value) {
		this.value=value;
	}
	
	@Override
	public boolean hasNext() {
		return !used;
	}

	@Override
	public Double next() {
		if (used) throw new NoSuchElementException("Iterator has already been traversed!");
		used=true;
		return value;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
