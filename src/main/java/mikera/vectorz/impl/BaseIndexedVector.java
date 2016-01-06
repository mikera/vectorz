package mikera.vectorz.impl;

import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for vectors that index into other sources
 * @author Mike
 */
@SuppressWarnings("serial")
abstract class BaseIndexedVector extends ASizedVector {
	protected final int[] indexes;

	protected BaseIndexedVector(int length) {
		super(length);
		
		indexes=new int[length];
	}
	
	public BaseIndexedVector(int[] indexes) {
		super(indexes.length);
		this.indexes=indexes;
	}
	
	protected abstract BaseIndexedVector replaceIndex(int[] newIndices);
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public void validate() {
		if (length!=indexes.length) throw new VectorzException("Wrong index length");
		super.validate();
	}
}
