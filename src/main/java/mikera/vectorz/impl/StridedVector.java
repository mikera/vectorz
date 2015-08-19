package mikera.vectorz.impl;

import mikera.vectorz.AVector;

public final class StridedVector extends BaseStridedVector {
	private static final long serialVersionUID = 5807998427323932401L;
	
	private StridedVector(double[] data, int offset, int length, int stride) {
		super(length,data,offset,stride);
	}
	
	public static StridedVector wrapStrided(double[] data, int offset, int length, int stride) {
		return new StridedVector(data,offset,length,stride);
	}

	public static StridedVector wrap(double[] data, int offset, int length, int stride) {
		return wrapStrided(data,offset,length,stride);
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
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);

		if (length==0) return Vector0.INSTANCE;
		if (length==len) return this;
		
		if (length==1) {
			return ArraySubVector.wrap(data, offset+start*stride, 1);
		} 
		return wrapStrided(data,offset+start*stride,length,stride);
	}
	
	@Override
	public StridedVector exactClone() {
		double[] data=this.data.clone();
		return wrapStrided(data,offset,length,stride);
	}
}
