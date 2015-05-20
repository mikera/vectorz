package mikera.vectorz.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Specialised iterator for a repeated Double value.
 * 
 * This is useful to minimise memory pressure in several quite common cases 
 * where you don't want to re-box a double value for every step.
 * 
 * @author Mike
 */
public class RepeatedElementIterator implements Iterator<Double> {
	long n;
	final Double value;
	
	public RepeatedElementIterator(long count, Double value) {
		this.value=value;
		n=count;
	}
	
	@Override
	public boolean hasNext() {
		return n>0;
	}

	@Override
	public Double next() {
		if (n<=0) throw new NoSuchElementException("Iterator has already been traversed!");
		n--;
		return value;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
