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
	public final double get(int i) {
		checkIndex(i);
		return data[offset+i*stride];
	}
	
	@Override
	public final double unsafeGet(int i) {
		return data[offset+i*stride];
	}
	
	@Override
	public void set(int i, double value) {
		checkIndex(i);
		data[offset+i*stride]=value;
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		data[offset+i*stride]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[offset+i*stride]+=value;
	}
	
	@Override
	public double dotProduct(double[] ds, int off) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i*stride]*ds[i+off];
		}
		return result;
	}
	
	@Override
	public void set(AVector v) {
		int length=checkSameLength(v);
		v.copyTo(0, data, offset, length, stride);
	}
}
