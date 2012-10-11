package mikera.vectorz.impl;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import mikera.vectorz.AVector;

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
	public int size() {
		return wrappedVector.length();
	}
}
