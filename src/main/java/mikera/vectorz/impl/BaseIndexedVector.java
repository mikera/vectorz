package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for vectors that index into other sources
 * @author Mike
 */
@SuppressWarnings("serial")
abstract class BaseIndexedVector extends AVector {
	protected final int[] indexes;
	protected final int length;

	protected BaseIndexedVector(int length) {
		indexes=new int[length];
		this.length=length;
	}
	
	public BaseIndexedVector(int[] indexes) {
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
	
	@Override
	public void validate() {
		if (length!=indexes.length) throw new VectorzException("Wrong index length");
		super.validate();
	}
}
