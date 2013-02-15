package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for vectors that index into other sources
 * @author Mike
 */
@SuppressWarnings("serial")
abstract class AIndexedVector extends AVector {
	protected final int[] indexes;
	protected final int length;

	protected AIndexedVector(int length) {
		indexes=new int[length];
		this.length=length;
	}
	
	public AIndexedVector(int[] indexes) {
		this.indexes=indexes;
		this.length=indexes.length;
	}
	
	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public int length() {
		return length;
	}
}
