package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;

public final class StridedVector extends AVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private final double[] data;
	private final int length;
	private final int offset;
	private final int stride;
	
	private StridedVector(double[] data, int offset, int length, int stride) {
		this.data=data;
		this.offset=offset;
		this.length=length;
		this.stride=stride;
	}
	
	public static StridedVector wrapStrided(double[] data, int offset, int length, int stride) {
		return new StridedVector(data,offset,length,stride);
	}

	public static AVector wrap(double[] data, int offset, int length, int stride) {
		if (stride==1) {
			return ArraySubVector.wrap(data, offset, length);
		}
		return wrapStrided(data,offset,length,stride);
	}
	
	@Override
	public int length() {
		return length;
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public double dotProduct(AVector v) {
		assert(v.length()==length);
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i*stride]*v.get(i);
		}
		return result;
	}
	
	@Override
	public void set(AVector v) {
		assert(v.length()==length);
		for (int i=0; i<length; i++) {
			data[offset+i*stride]=v.get(i);
		}
	}
	
	@Override
	public AVector subVector(int start, int length) {
		assert(start>=0);
		assert((start+length)<=this.length);
		if (length==1) {
			return ArraySubVector.wrap(data, offset+start*stride, 1);
		} else if (length>0) {
			return wrapStrided(data,offset+start*stride,length,stride);
		} else {
			return Vector0.INSTANCE;
		}
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
	public void copyTo(double[] dest, int destOffset) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]=data[offset+(i*stride)];
		}
	}
	
	@Override
	public StridedVector exactClone() {
		double[] data=this.data.clone();
		return wrapStrided(data,offset,length,stride);
	}

	@Override
	public void validate() {
		if (length>0) {
			if ((offset<0)||(offset>=data.length)) throw new VectorzException("offset out of bounds: "+offset);
			int lastIndex=offset+(stride*(length-1));
			if ((lastIndex<0)||(lastIndex>=data.length)) throw new VectorzException("lastIndex out of bounds: "+lastIndex);
		}
		
		super.validate();
	}
}
