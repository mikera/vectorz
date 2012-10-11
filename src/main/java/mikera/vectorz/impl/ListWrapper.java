package mikera.vectorz.impl;

import java.util.AbstractList;
import mikera.vectorz.AVector;

/**
 * Simple wrapper class to view a vector according to the java.util.List interface
 * 
 * @author Mike
 */
public final class ListWrapper extends AbstractList<Double> {
	private final AVector wrappedVector;
			
	public ListWrapper(AVector v) {
		this.wrappedVector=v;
	}
	
	@Override
	public Double get(int index) {
		return wrappedVector.get(index);
	}
	
	@Override
	public Double set(int index, Double value) {
		wrappedVector.set(index,value);
		return null;
	}

	@Override
	public int size() {
		return wrappedVector.length();
	}
}
