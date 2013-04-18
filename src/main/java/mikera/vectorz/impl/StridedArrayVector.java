package mikera.vectorz.impl;

import mikera.vectorz.AVector;

public class StridedArrayVector extends AVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private double[] data;
	private int length;
	private int offset;
	private int stride;
	
	private StridedArrayVector(double[] data, int offset, int length, int stride) {
		this.data=data;
		this.offset=offset;
		this.length=length;
		this.stride=stride;
	}

	public static StridedArrayVector wrap(double[] data, int offset, int length, int stride) {
		return new StridedArrayVector(data,offset,length,stride);
	}
	
	@Override
	public int length() {
		return length;
	}
	
	@Override
	public StridedArrayVector subVector(int start, int length) {
		assert(start>=0);
		assert((start+length)<=this.length);
		return wrap(data,offset+start*stride,length,stride);
	}
	
	@Override
	public double get(int i) {
		return data[offset+i*stride];
	}
	
	@Override
	public void set(int i, double value) {
		data[offset+i*stride]=value;
	}
	
	@Override
	public void addAt(int i, double value) {
		data[offset+i*stride]+=value;
	}
	
	@Override
	public AVector exactClone() {
		double[] data=this.data.clone();
		return wrap(data,offset,length,stride);
	}


}
