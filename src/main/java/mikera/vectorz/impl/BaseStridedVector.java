package mikera.vectorz.impl;

import mikera.vectorz.AVector;

public abstract class BaseStridedVector extends AStridedVector {

	protected BaseStridedVector(int length, double[] data, int offset, int stride) {
		super(length, data);
		this.stride=stride;
		this.offset=offset;
	}

	protected final int stride;
	protected final int offset;

	@Override
	public int getArrayOffset() {
		return offset;
	}

	@Override
	public int getStride() {
		return stride;
	}

	@Override
	protected int index(int i) {
		return offset+i*stride;
	}

	@Override
	public double get(int i) {
		checkIndex(i);
		return unsafeGet(i);
	}
	
	@Override
	public double unsafeGet(int i) {
		return data[index(i)];
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		unsafeSet(i,value);
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[index(i)]=value;
	}
}
