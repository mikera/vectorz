package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for vectors that index into other sources
 * @author Mike
 */
@SuppressWarnings("serial")
abstract class IndexedVector extends AVector {
	protected final int[] indexes;
	protected final int length;

	protected IndexedVector(int length) {
		indexes=new int[length];
		this.length=length;
	}
	
	public IndexedVector(int[] indexes) {
		this.indexes=indexes;
		this.length=indexes.length;
	}
	
	@Override
	public boolean isReference() {
		return true;
	}

	@Override
	public int length() {
		return length;
	}
}
