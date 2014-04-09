package mikera.vectorz.impl;

import java.util.AbstractList;
import java.util.Iterator;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Simple wrapper class to view a vector according to the java.util.List interface
 * 
 * @author Mike
 */
public final class ListWrapper extends AbstractList<Double> {
	private final AVector wrappedVector;
	private final int length;
	
	private static final Double ZERO=0.0;
			
	public ListWrapper(AVector v) {
		this.wrappedVector=v;
		this.length=v.length();
	}
	
	@Override
	public Double get(int index) {
		double v=wrappedVector.get(index);
		if (v==0.0) return ZERO; // saves boxing with sparse arrays
		return v;
	}
	
	@Override
	public Double set(int index, Double value) {
		if ((index<0)||(index>=length)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(wrappedVector, index));
		wrappedVector.unsafeSet(index,value);
		return null;
	}

	@Override
	public int size() {
		return length;
	}
	
	@Override
	public Iterator<Double> iterator() {
		return wrappedVector.iterator();
	}
}
