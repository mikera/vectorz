package mikera.vectorz.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RepeatedElementIterator implements Iterator<Double> {
	long n;
	final Double value;
	
	public RepeatedElementIterator(long count, double value) {
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
